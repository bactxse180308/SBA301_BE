package com.sba302.electroshop.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidBannerDatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBannerDates {
    String message() default "Ngày bắt đầu không được lớn hơn ngày kết thúc";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
