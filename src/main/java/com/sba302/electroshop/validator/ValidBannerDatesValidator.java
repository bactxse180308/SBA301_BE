package com.sba302.electroshop.validator;

import com.sba302.electroshop.dto.request.BannerCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidBannerDatesValidator implements ConstraintValidator<ValidBannerDates, BannerCreateRequest> {

    @Override
    public boolean isValid(BannerCreateRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true;
        }
        return !request.getStartDate().isAfter(request.getEndDate());
    }
}
