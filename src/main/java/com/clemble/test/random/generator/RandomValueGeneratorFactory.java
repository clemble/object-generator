package com.clemble.test.random.generator;

import com.clemble.test.random.AbstractValueGeneratorFactory;
import com.clemble.test.random.constructor.ClassPropertySetterRegistry;

import java.util.concurrent.Callable;

public class RandomValueGeneratorFactory extends AbstractValueGeneratorFactory {

    public RandomValueGeneratorFactory() {
        super(new ClassPropertySetterRegistry(), RandomValueGenerators.DEFAULT_GENERATORS);
    }

    public RandomValueGeneratorFactory(ClassPropertySetterRegistry propertySetterManager) {
        super(propertySetterManager, RandomValueGenerators.DEFAULT_GENERATORS);
    }

    @Override
    public <T> Callable<T> enumValueGenerator(Class<T> enumClass) {
        return RandomValueGenerators.enumValueGenerator(enumClass);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Callable arrayValueGenerator(Class klass) {
        final Callable valueGenerator = get(klass.getComponentType());
        return () -> {
            int size = 1 + RandomValueGenerators.RANDOM_UTILS.nextInt(10);
            Object[] values = new Object[size];
            for (int i = 0; i < size; i++)
                values[i] = valueGenerator.call();
            return values;
        };
    }

}
