package com.tech.wixblog.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
@Documented
public @interface StrongPassword {

    String message() default
            "Password must contain uppercase, lowercase and a number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}