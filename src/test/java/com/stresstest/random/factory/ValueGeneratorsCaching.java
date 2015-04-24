package com.stresstest.random.factory;

import org.junit.Assert;
import org.junit.Test;

import com.clemble.test.random.ValueGeneratorFactory;
import com.clemble.test.random.generator.CachedValueGeneratorFactory;
import com.clemble.test.random.generator.RandomValueGeneratorFactory;

import java.util.function.Supplier;

@SuppressWarnings("all")
public class ValueGeneratorsCaching {

    final private ValueGeneratorFactory simpleValueGeneratorFactory = new RandomValueGeneratorFactory();
    final private CachedValueGeneratorFactory cachedValueGeneratorFactory = new CachedValueGeneratorFactory(simpleValueGeneratorFactory);

    static class IntRandomClass {
        private int randomClass;
    }

    @SuppressWarnings("unused")
    public static class BooleanRandomClass {
        private boolean randomBoolean;
    }

    @SuppressWarnings("unused")
    public static class CombinedRandomClass {
        private IntRandomClass intRandomClass;
        private BooleanRandomClass booleanRandomClass;
    }

    @Test
    public void testStandardGeneraterCreatedEachTime() {
        Supplier<BooleanRandomClass> valueGenerator = simpleValueGeneratorFactory.get(BooleanRandomClass.class);
        Supplier<BooleanRandomClass> anotherValueGenerator = simpleValueGeneratorFactory.get(BooleanRandomClass.class);
        Assert.assertNotSame(valueGenerator, anotherValueGenerator);
    }

    @Test
    public void testCachedGeneraterCreatedEachTime() {
        Supplier<BooleanRandomClass> valueGenerator = cachedValueGeneratorFactory.get(BooleanRandomClass.class);
        Supplier<BooleanRandomClass> anotherValueGenerator = cachedValueGeneratorFactory.get(BooleanRandomClass.class);
        Assert.assertEquals(valueGenerator, anotherValueGenerator);
    }

    @Test
    public void testCachedGeneraterReused() {
        Supplier<BooleanRandomClass> booleanValueGenerator = cachedValueGeneratorFactory.get(BooleanRandomClass.class);
        Assert.assertEquals(booleanValueGenerator, cachedValueGeneratorFactory.get(BooleanRandomClass.class));

        Supplier<IntRandomClass> intValueGenerator = cachedValueGeneratorFactory.get(IntRandomClass.class);
        Assert.assertEquals(intValueGenerator, cachedValueGeneratorFactory.get(IntRandomClass.class));

        Supplier<CombinedRandomClass> combinedValueGenerator = cachedValueGeneratorFactory.get(CombinedRandomClass.class);
        Assert.assertEquals(combinedValueGenerator, cachedValueGeneratorFactory.get(CombinedRandomClass.class));
    }
}
