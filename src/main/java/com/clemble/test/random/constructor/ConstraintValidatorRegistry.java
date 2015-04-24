package com.clemble.test.random.constructor;

import com.clemble.test.random.constructor.validation.ConstraintValidator;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConstraintValidatorRegistry {

    final private List<ConstraintValidator> validators = new ArrayList<>();

    public <T> Supplier<T> get(Stream<Annotation> annotation, Supplier<T> generator) {
        Collection<Annotation> allAnnotations = annotation.collect(Collectors.toList());
        // Step 1. Filtering validators
        for(ConstraintValidator validator: validators) {
            // Step 2. Filtering by annotation
            for(Annotation a: allAnnotations) {
                if(validator.test(a)) {
                    generator = validator.apply(generator, a);
                    break;
                }
            }
        }
        // Step 3. Returning accumulated response
        return generator;
    }

}
