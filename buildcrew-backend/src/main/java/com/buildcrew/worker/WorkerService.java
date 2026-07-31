package com.buildcrew.worker;

import com.buildcrew.common.dto.PageResponse;
import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WorkerService {

    @Inject
    WorkerRepository workerRepository;

    @Inject
    TenantContext tenantContext;

    public PageResponse<WorkerDTO> search(String query, String status, int page, int size) {
        UUID companyId = tenantContext.getCompanyId();

        List<WorkerDTO> items = workerRepository.search(companyId, query, status, page, size)
                .stream().map(WorkerDTO::from).toList();

        long total = workerRepository.countSearch(companyId, query, status);

        return new PageResponse<>(items, page, size, total);
    }

    public WorkerDTO findById(UUID id) {
        Worker worker = find(id);
        return WorkerDTO.from(worker);
    }

    @Transactional
    public WorkerDTO create(WorkerCreateDTO dto) {
        Worker worker = new Worker();
        worker.id = UUID.randomUUID();
        worker.companyId = tenantContext.getCompanyId();
        worker.fullName = dto.fullName;
        worker.phone = dto.phone;
        worker.position = dto.position;
        worker.payType = dto.payType;
        worker.dailySalary = dto.dailySalary;
        worker.pricePerM2 = dto.pricePerM2;
        worker.employmentDate = dto.employmentDate;
        worker.status = "active";
        worker.createdAt = OffsetDateTime.now();

        workerRepository.persist(worker);
        return WorkerDTO.from(worker);
    }

    @Transactional
    public WorkerDTO update(UUID id, WorkerCreateDTO dto) {
        Worker worker = find(id);

        worker.fullName = dto.fullName;
        worker.phone = dto.phone;
        worker.position = dto.position;
        worker.payType = dto.payType;
        worker.dailySalary = dto.dailySalary;
        worker.pricePerM2 = dto.pricePerM2;
        worker.employmentDate = dto.employmentDate;

        return WorkerDTO.from(worker);
    }

    @Transactional
    public void toggleStatus(UUID id) {
        Worker worker = find(id);
        worker.status = "active".equals(worker.status) ? "inactive" : "active";
    }

    @Transactional
    public void delete(UUID id) {
        Worker worker = find(id);
        workerRepository.delete(worker);
    }

    private Worker find(UUID id) {
        Worker worker = workerRepository.findById(id);
        if (worker == null || !worker.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Worker not found");
        }
        return worker;
    }
}
