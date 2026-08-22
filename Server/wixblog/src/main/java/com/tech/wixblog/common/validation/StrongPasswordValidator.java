package com.tech.wixblog.common.validation;

import com.tech.wixblog.common.validation.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator
        implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(
            String password,
            ConstraintValidatorContext context
                          ) {

        if (password == null || password.isBlank()) {
            return true;
        }

        boolean hasUppercase =
                password.chars().anyMatch(Character::isUpperCase);

        boolean hasLowercase =
                password.chars().anyMatch(Character::isLowerCase);

        boolean hasDigit =
                password.chars().anyMatch(Character::isDigit);

        return hasUppercase
                && hasLowercase
                && hasDigit;
    }
}