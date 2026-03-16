package com.sba302.electroshop.security;

import com.sba302.electroshop.repository.BulkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("bulkOrderSecurity")
@RequiredArgsConstructor
public class BulkOrderSecurity {

    private final BulkOrderRepository bulkOrderRepository;

    /**
     * Check if the currently authenticated user is the owner of the given Bulk Order.
     * Use in @PreAuthorize: "@bulkOrderSecurity.isOwner(#id)"
     */
    public boolean isOwner(Integer bulkOrderId) {
        String currentUserIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentUserIdStr == null || "anonymousUser".equals(currentUserIdStr)) {
            return false;
        }

        try {
            Integer currentUserId = Integer.parseInt(currentUserIdStr);
            return bulkOrderRepository.findById(bulkOrderId)
                    .map(bulkOrder -> bulkOrder.getUser().getUserId().equals(currentUserId))
                    .orElse(false);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
