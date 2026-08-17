package com.buildcrew.notification;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NotificationRepository implements PanacheRepositoryBase<Notification, UUID> {

    public List<Notification> findByCompany(UUID companyId, boolean unreadOnly) {
        if (unreadOnly) {
            return list("companyId = ?1 and isRead = false", Sort.descending("createdAt"), companyId);
        }
        return list("companyId", Sort.descending("createdAt"), companyId);
    }

    public boolean existsSimilarToday(UUID companyId, String type, String message) {
        OffsetDateTime startOfToday = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        return count("companyId = ?1 and type = ?2 and message = ?3 and createdAt >= ?4",
                companyId, type, message, startOfToday) > 0;
    }
}
