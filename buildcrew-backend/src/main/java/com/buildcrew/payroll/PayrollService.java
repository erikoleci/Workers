package com.buildcrew.payroll;

import com.buildcrew.common.dto.PageResponse;
import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PayrollService {

    @Inject
    PayrollRepository payrollRepository;

    @Inject
    TenantContext tenantContext;

    @Inject
    EntityManager em;

    @Inject
    com.buildcrew.notification.NotificationService notificationService;

    public PageResponse<PayrollDTO> search(String status, String workerId, int page, int size) {
        UUID companyId = tenantContext.getCompanyId();
        UUID wId = workerId != null ? UUID.fromString(workerId) : null;

        List<PayrollDTO> items = payrollRepository.search(companyId, status, wId, page, size)
                .stream().map(this::toDTO).toList();

        long total = payrollRepository.countSearch(companyId, status, wId);
        return new PageResponse<>(items, page, size, total);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<PayrollDTO> generate(PayrollGenerateDTO dto) {
        UUID companyId = tenantContext.getCompanyId();

        List<UUID> workerIds;
        if (dto.workerIds != null && !dto.workerIds.isEmpty()) {
            workerIds = dto.workerIds.stream().map(UUID::fromString).toList();
        } else {
            Query q = em.createNativeQuery(
                    "SELECT id FROM workers WHERE company_id = :companyId AND status = 'active'");
            q.setParameter("companyId", companyId);
            workerIds = ((List<Object>) q.getResultList()).stream().map(o -> (UUID) o).toList();
        }

        List<PayrollDTO> results = workerIds.stream().map(workerId -> generateForWorker(companyId, workerId, dto)).toList();

        notificationService.notifyCompanyManagers(
                companyId,
                "payroll_ready",
                "Payroll for " + dto.periodStart + " to " + dto.periodEnd + " is ready for review"
        );

        return results;
    }

    private PayrollDTO generateForWorker(UUID companyId, UUID workerId, PayrollGenerateDTO dto) {
        Query workerQ = em.createNativeQuery(
                "SELECT pay_type, daily_salary, price_per_m2 FROM workers WHERE id = :id");
        workerQ.setParameter("id", workerId);
        Object[] worker = (Object[]) workerQ.getSingleResult();

        String payType = (String) worker[0];
        BigDecimal dailySalary = (BigDecimal) worker[1];
        BigDecimal pricePerM2 = (BigDecimal) worker[2];

        BigDecimal baseAmount;

        if ("daily".equals(payType)) {
            Query daysQ = em.createNativeQuery(
                    "SELECT COUNT(DISTINCT dr.report_date) FROM daily_reports dr " +
                    "JOIN crew_members cm ON cm.crew_id = dr.crew_id " +
                    "WHERE cm.worker_id = :workerId AND dr.report_date BETWEEN :start AND :end");
            daysQ.setParameter("workerId", workerId);
            daysQ.setParameter("start", dto.periodStart);
            daysQ.setParameter("end", dto.periodEnd);
            long daysWorked = ((Number) daysQ.getSingleResult()).longValue();
            baseAmount = dailySalary != null ? dailySalary.multiply(BigDecimal.valueOf(daysWorked)) : BigDecimal.ZERO;
        } else {
            Query m2Q = em.createNativeQuery(
                    "SELECT COALESCE(SUM(wdr.completed_m2), 0) FROM worker_daily_reports wdr " +
                    "WHERE wdr.worker_id = :workerId AND wdr.report_date BETWEEN :start AND :end");
            m2Q.setParameter("workerId", workerId);
            m2Q.setParameter("start", dto.periodStart);
            m2Q.setParameter("end", dto.periodEnd);
            BigDecimal totalM2 = (BigDecimal) m2Q.getSingleResult();
            baseAmount = pricePerM2 != null ? pricePerM2.multiply(totalM2) : BigDecimal.ZERO;
        }

        Payroll payroll = new Payroll();
        payroll.id = UUID.randomUUID();
        payroll.companyId = companyId;
        payroll.workerId = workerId;
        payroll.periodStart = dto.periodStart;
        payroll.periodEnd = dto.periodEnd;
        payroll.baseAmount = baseAmount;
        payroll.bonuses = BigDecimal.ZERO;
        payroll.deductions = BigDecimal.ZERO;
        payroll.finalAmount = baseAmount;
        payroll.status = "pending";
        payroll.createdAt = OffsetDateTime.now();

        payrollRepository.persist(payroll);
        return toDTO(payroll);
    }

    @Transactional
    public PayrollDTO adjust(UUID id, PayrollAdjustDTO dto) {
        Payroll payroll = find(id);

        if (dto.bonuses != null) payroll.bonuses = dto.bonuses;
        if (dto.deductions != null) payroll.deductions = dto.deductions;

        payroll.finalAmount = payroll.baseAmount
                .add(payroll.bonuses)
                .subtract(payroll.deductions);

        return toDTO(payroll);
    }

    @Transactional
    public PayrollDTO markPaid(UUID id) {
        Payroll payroll = find(id);
        payroll.status = "paid";
        return toDTO(payroll);
    }

    private Payroll find(UUID id) {
        Payroll payroll = payrollRepository.findById(id);
        if (payroll == null || !payroll.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Payroll record not found");
        }
        return payroll;
    }

    private PayrollDTO toDTO(Payroll p) {
        PayrollDTO dto = new PayrollDTO();
        dto.id = p.id.toString();
        dto.workerId = p.workerId.toString();
        dto.periodStart = p.periodStart;
        dto.periodEnd = p.periodEnd;
        dto.baseAmount = p.baseAmount;
        dto.bonuses = p.bonuses;
        dto.deductions = p.deductions;
        dto.finalAmount = p.finalAmount;
        dto.status = p.status;

        Query q = em.createNativeQuery("SELECT full_name FROM workers WHERE id = :id");
        q.setParameter("id", p.workerId);
        List<Object> result = q.getResultList();
        dto.workerName = result.isEmpty() ? null : (String) result.get(0);

        return dto;
    }
}
