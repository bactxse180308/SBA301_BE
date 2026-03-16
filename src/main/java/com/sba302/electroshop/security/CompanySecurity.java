package com.sba302.electroshop.security;

import com.sba302.electroshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("companySecurity")
@RequiredArgsConstructor
public class CompanySecurity {

    private final UserRepository userRepository;

    /**
     * Check if the currently authenticated user belongs to the given Company.
     * Use in @PreAuthorize: "@companySecurity.isOwner(#id)"
     */
    public boolean isOwner(Integer companyId) {
        String currentUserIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentUserIdStr == null || "anonymousUser".equals(currentUserIdStr)) {
            return false;
        }

        try {
            Integer userId = Integer.parseInt(currentUserIdStr);
            return userRepository.findById(userId)
                    .map(user -> user.getCompany() != null && user.getCompany().getCompanyId().equals(companyId))
                    .orElse(false);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
