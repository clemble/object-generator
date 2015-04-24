package com.clemble.test.random.constructor.validation;

import java.lang.annotation.Annotation;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ConstraintValidator
    extends BiFunction<Supplier, Annotation, Supplier>, Predicate<Annotation> {

    @Override
    Supplier apply(Supplier t, Annotation u);

}
