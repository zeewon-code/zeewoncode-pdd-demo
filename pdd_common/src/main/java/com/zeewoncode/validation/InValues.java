package com.zeewoncode.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = InValuesValidator.class)
public @interface InValues {
    String message() default "取值不合法";
    int[] value();                          // 允许的值列表
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}