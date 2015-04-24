package com.clemble.test.random.constructor.validation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.validation.constraints.Size;

public class SizeConstraintValidator implements ConstraintValidator {

    @Override
    public Supplier apply(Supplier t, Annotation a) {
        if (test(a)) {
            Size size = (Size) a;
            Object test = t.get();
            if (test instanceof CharSequence) {
                return charSequenceSupplier.apply(size, t);
            } else if (test instanceof Collection) {
                return collectionSupplier.apply(size, t);
            } else if (test instanceof Map) {
                return mapSupplier.apply(size, t);
            }
            // TODO add array processing
            return t;
        } else {
            return t;
        }
    }

    @Override
    public boolean test(Annotation o) {
        return o instanceof Size;
    }

    private BiFunction<Size, Supplier<Collection>, Supplier<Collection>> collectionSupplier = (size, supplier) -> {
        return () -> {
            Collection generated = supplier.get();
            while (generated.size() < size.min()) {
                generated.addAll(supplier.get());
            }
            while (generated.size() > size.max()) {
                generated.remove(generated.iterator().next());
            }
            return generated;
        };
    };

    private BiFunction<Size, Supplier<CharSequence>, Supplier<CharSequence>> charSequenceSupplier = (size, supplier) -> {
        return () -> {
            CharSequence generated = supplier.get();
            while (generated.length() < size.min()) {
                generated = generated.toString() + supplier.get();
            }
            while (generated.length() > size.max()) {
                generated = generated.subSequence(1, generated.length());
            }
            return generated;
        };
    };

    private BiFunction<Size, Supplier<Map>, Supplier<Map>> mapSupplier = (size, supplier) -> {
        return () -> {
            Map generated = supplier.get();
            while (generated.size() < size.min()) {
                generated.putAll(supplier.get());
            }
            while (generated.size() > size.max()) {
                generated.remove(generated.keySet().iterator().next());
            }
            return generated;
        };
    };

}
