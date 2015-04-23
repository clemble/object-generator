package com.clemble.test.random.generator;

import com.clemble.test.random.AbstractValueGeneratorFactory;
import com.clemble.test.random.constructor.ClassPropertySetterRegistry;

import java.util.concurrent.Callable;

public class RandomValueGeneratorFactory extends AbstractValueGeneratorFactory {

    public RandomValueGeneratorFactory() {
        super(new ClassPropertySetterRegistry(), RandomValueGenerator.DEFAULT_GENERATORS);
    }

    public RandomValueGeneratorFactory(ClassPropertySetterRegistry propertySetterManager) {
        super(propertySetterManager, RandomValueGenerator.DEFAULT_GENERATORS);
    }

    @Override
    public <T> Callable<T> enumValueGenerator(Class<T> enumClass) {
        return RandomValueGenerator.enumValueGenerator(enumClass);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Callable arrayValueGenerator(Class klass) {
        final Callable valueGenerator = getValueGenerator(klass.getComponentType());
        return () -> {
            int size = 1 + RandomValueGenerator.RANDOM_UTILS.nextInt(10);
            Object[] values = new Object[size];
            for (int i = 0; i < size; i++)
                values[i] = valueGenerator.call();
            return values;
        };
    }

}
