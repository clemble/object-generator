package com.clemble.test.random.constructor.validation;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import javax.validation.constraints.Size;

public class SizeConstraintValidator implements ConstraintValidator {

    @Override
    public Supplier apply(Supplier t, Annotation a) {
        if (test(a)) {
            Size size = (Size) a;
            return t;
        } else {
            return t;
        }
    }

    @Override
    public boolean test(Annotation o) {
        return o instanceof Size;
    }

//    <li>{@code CharSequence} (length of character sequence is evaluated)</li>
// *     <li>{@code Collection} (collection size is evaluated)</li>
// *     <li>{@code Map} (map size is evaluated)</li>
// *     <li>Array (array length is evaluated)</li>

    final public static class CharSequenceSizeConstraint implements Supplier<CharSequence> {

        final private int min;
        final private int max;
        final private Supplier<CharSequence> delegate;

        public CharSequenceSizeConstraint(int min, int max, Supplier<CharSequence> delegate) {
            this.min = min;
            this.max = max;
            this.delegate = delegate;
        }

        @Override
        public CharSequence get() {
            CharSequence generated = delegate.get();
            while (generated.length() < min) {
                generated = generated.toString() + delegate.get();
            }
            while (generated.length() > max) {
                generated = generated.subSequence(1, generated.length());
            }
            return generated;
        }

    }

}
