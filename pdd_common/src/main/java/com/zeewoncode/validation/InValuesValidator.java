package com.zeewoncode.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class InValuesValidator implements ConstraintValidator<InValues, Integer> {
    private Set<Integer> allowed;

    @Override
    public void initialize(InValues anno) {
        allowed = Arrays.stream(anno.value()).boxed().collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Integer v, ConstraintValidatorContext ctx) {
        return v == null || allowed.contains(v);   // null 交给 @NotNull 管
    }
}