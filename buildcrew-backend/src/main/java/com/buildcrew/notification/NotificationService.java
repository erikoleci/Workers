package com.buildcrew.notification;

import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NotificationService {

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    TenantContext tenantContext;

    @Inject
    EntityManager em;

    public List<NotificationDTO> list(boolean unreadOnly) {
        UUID companyId = tenantContext.getCompanyId();
        return notificationRepository.findByCompany(companyId, unreadOnly)
                .stream().map(NotificationDTO::from).toList();
    }

    @Transactional
    public void markRead(UUID id) {
        Notification n = notificationRepository.findById(id);
        if (n == null || !n.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Notification not found");
        }
        n.isRead = true;
    }

    @Transactional
    public void markAllRead() {
        UUID companyId = tenantContext.getCompanyId();
        Query q = em.createNativeQuery(
                "UPDATE notifications SET is_read = true WHERE company_id = :companyId AND is_read = false");
        q.setParameter("companyId", companyId);
        q.executeUpdate();
    }

    /**
     * Creates one notification per owner/manager of the company, avoiding
     * duplicates if a matching notification was already created today.
     * Used both by the scheduled job and by other services (e.g. payroll ready).
     */
    @Transactional
    public void notifyCompanyManagers(UUID companyId, String type, String message) {
        if (notificationRepository.existsSimilarToday(companyId, type, message)) {
            return;
        }

        Query q = em.createNativeQuery(
                "SELECT id FROM users WHERE company_id = :companyId AND role IN ('owner','manager') AND status = 'active'");
        q.setParameter("companyId", companyId);

        @SuppressWarnings("unchecked")
        List<Object> userIds = q.getResultList();

        for (Object userIdObj : userIds) {
            Notification n = new Notification();
            n.id = UUID.randomUUID();
            n.companyId = companyId;
            n.userId = (UUID) userIdObj;
            n.type = type;
            n.message = message;
            n.isRead = false;
            n.createdAt = OffsetDateTime.now();
            notificationRepository.persist(n);
        }
    }
}
