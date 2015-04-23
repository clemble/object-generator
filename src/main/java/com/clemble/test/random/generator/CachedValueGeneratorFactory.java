package com.clemble.test.random.generator;

import com.clemble.test.random.AbstractValueGeneratorFactory;
import com.clemble.test.random.ValueGeneratorFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.Callable;

/**
 * {@link ValueGeneratorFactory} implementation that uses caching to optimize {@link Callable} production.
 * 
 * @author Anton Oparin
 * 
 */
public class CachedValueGeneratorFactory extends AbstractValueGeneratorFactory {

    final private ValueGeneratorFactory valueGeneratorFactory;

    /**
     * Google LoadingCache that is used as a primary cache implementation.
     */
    final private LoadingCache<Class<?>, Callable<?>> cachedValueGenerators = CacheBuilder.newBuilder().build(
            new CacheLoader<Class<?>, Callable<?>>() {
                @Override
                public Callable<?> load(Class<?> klass) throws Exception {
                    return valueGeneratorFactory.getValueGenerator(klass);
                }
            });

    public CachedValueGeneratorFactory(ValueGeneratorFactory newValueGeneratorFactory) {
        super(newValueGeneratorFactory.getPropertySetterManager());
        this.valueGeneratorFactory = newValueGeneratorFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Callable<T> getValueGenerator(Class<T> klass) {
        try {
            return (Callable<T>) cachedValueGenerators.get(klass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <T> Callable<T> enumValueGenerator(Class<T> klass) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected <T> Callable<T> arrayValueGenerator(Class<?> klass) {
        throw new UnsupportedOperationException();
    }

}
