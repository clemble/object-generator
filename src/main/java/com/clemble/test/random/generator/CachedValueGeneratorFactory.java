package com.clemble.test.random.generator;

import com.clemble.test.random.AbstractValueGeneratorFactory;
import com.clemble.test.random.ValueGeneratorFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.lang.reflect.Parameter;
import java.util.function.Supplier;

/**
 * {@link ValueGeneratorFactory} implementation that uses caching to optimize {@link Supplier} production.
 * 
 * @author Anton Oparin
 * 
 */
public class CachedValueGeneratorFactory extends AbstractValueGeneratorFactory {

    final private ValueGeneratorFactory valueGeneratorFactory;

    /**
     * Google LoadingCache that is used as a primary cache implementation.
     */
    final private LoadingCache<Class<?>, Supplier<?>> cachedValueGenerators = CacheBuilder.newBuilder().build(
            new CacheLoader<Class<?>, Supplier<?>>() {
                @Override
                public Supplier<?> load(Class<?> klass) throws Exception {
                    return valueGeneratorFactory.get(klass);
                }
            });

    public CachedValueGeneratorFactory(ValueGeneratorFactory newValueGeneratorFactory) {
        super(newValueGeneratorFactory.getPropertySetterManager());
        this.valueGeneratorFactory = newValueGeneratorFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> get(Class<T> klass) {
        try {
            return (Supplier<T>) cachedValueGenerators.get(klass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <T> Supplier<T> enumValueGenerator(Class<T> klass) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected <T> Supplier<T> arrayValueGenerator(Class<?> klass) {
        throw new UnsupportedOperationException();
    }

}
