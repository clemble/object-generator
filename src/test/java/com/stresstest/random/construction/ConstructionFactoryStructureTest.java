package com.stresstest.random.construction;

import org.junit.Assert;

import org.junit.Test;

import com.clemble.test.random.ValueGeneratorFactory;
import com.clemble.test.random.constructor.ClassConstructorFactory;
import com.clemble.test.random.constructor.ClassConstructorSimple;
import com.clemble.test.random.constructor.ClassValueGenerator;
import com.clemble.test.random.generator.RandomValueGeneratorFactory;

import java.util.concurrent.Callable;

@SuppressWarnings("unused")
public class ConstructionFactoryStructureTest {

    final private ValueGeneratorFactory valueGeneratorFactory = new RandomValueGeneratorFactory();

    private static class PrivateFactoryMethodClass {
        final private boolean data;

        private PrivateFactoryMethodClass(boolean dataValue) {
            this.data = dataValue;
        }

        private boolean getData() {
            return data;
        }

        private static class PrivateFactoryMethodClassBuilder {
            private boolean value;

            private PrivateFactoryMethodClass build() {
                return new PrivateFactoryMethodClass(value);
            }
        }

        private static PrivateFactoryMethodClass create(boolean value) {
            return new PrivateFactoryMethodClass(value);
        }

        private static PrivateFactoryMethodClass create(final PrivateFactoryMethodClass value) {
            return value;
        }

        private static PrivateFactoryMethodClassBuilder newBuilder() {
            return new PrivateFactoryMethodClassBuilder();
        }
    }

    @Test
    public void testPrivateFactoryConstructorUsed() {
        Callable<PrivateFactoryMethodClass> factoryGenerator = valueGeneratorFactory.get(PrivateFactoryMethodClass.class);
        ClassValueGenerator<PrivateFactoryMethodClass> classValueGenerator = (ClassValueGenerator<PrivateFactoryMethodClass>) factoryGenerator;
        Assert.assertTrue(classValueGenerator.getObjectConstructor() instanceof ClassConstructorFactory);
    }

    protected static class ProtectedFactoryMethodClass {
        final private boolean data;

        protected ProtectedFactoryMethodClass(boolean dataValue) {
            this.data = dataValue;
        }

        protected boolean getData() {
            return data;
        }

        protected static class ProtectedFactoryMethodClassBuilder {
            protected boolean value;

            protected ProtectedFactoryMethodClass build() {
                return new ProtectedFactoryMethodClass(value);
            }
        }

        protected static ProtectedFactoryMethodClass create(boolean value) {
            return new ProtectedFactoryMethodClass(value);
        }

        protected static ProtectedFactoryMethodClass create(final ProtectedFactoryMethodClass value) {
            return value;
        }

        protected static ProtectedFactoryMethodClassBuilder newBuilder() {
            return new ProtectedFactoryMethodClassBuilder();
        }
    }

    @Test
    public void testProtectedFactoryConstructorUsed() {
        Callable<ProtectedFactoryMethodClass> factoryGenerator = valueGeneratorFactory.get(ProtectedFactoryMethodClass.class);
        ClassValueGenerator<ProtectedFactoryMethodClass> classValueGenerator = (ClassValueGenerator<ProtectedFactoryMethodClass>) factoryGenerator;
        Assert.assertTrue(classValueGenerator.getObjectConstructor() instanceof ClassConstructorFactory);
    }

    static class DefaultFactoryMethodClass {
        final private boolean data;

        protected DefaultFactoryMethodClass(boolean dataValue) {
            this.data = dataValue;
        }

        boolean getData() {
            return data;
        }

        static class DefaultFactoryMethodClassBuilder {
            protected boolean value;

            protected DefaultFactoryMethodClass build() {
                return new DefaultFactoryMethodClass(value);
            }
        }

        static DefaultFactoryMethodClass create(boolean value) {
            return new DefaultFactoryMethodClass(value);
        }
        
        static DefaultFactoryMethodClass create(final DefaultFactoryMethodClass value) {
            return value;
        }

        static DefaultFactoryMethodClassBuilder newBuilder() {
            return new DefaultFactoryMethodClassBuilder();
        }
    }

    @Test
    public void testDefaultFactoryConstructorUsed() {
        Callable<DefaultFactoryMethodClass> factoryGenerator = valueGeneratorFactory.get(DefaultFactoryMethodClass.class);
        ClassValueGenerator<DefaultFactoryMethodClass> classValueGenerator = (ClassValueGenerator<DefaultFactoryMethodClass>) factoryGenerator;
        Assert.assertTrue(classValueGenerator.getObjectConstructor() instanceof ClassConstructorFactory);
    }

    public static class PublicFactoryMethodClass {
        final private boolean data;

        private PublicFactoryMethodClass(boolean dataValue) {
            this.data = dataValue;
        }

        public boolean getData() {
            return data;
        }

        public static class PublicFactoryMethodClassBuilder {
            private boolean value;

            public PublicFactoryMethodClass build() {
                return new PublicFactoryMethodClass(value);
            }
        }

        public static PublicFactoryMethodClass create(boolean value) {
            return new PublicFactoryMethodClass(value);
        }
        
        public static PublicFactoryMethodClass create(PublicFactoryMethodClass value) {
            return value;
        }

        public static PublicFactoryMethodClassBuilder newBuilder() {
            return new PublicFactoryMethodClassBuilder();
        }
    }

    @Test
    public void testPublicFactoryConstructorUsed() {
        Callable<PublicFactoryMethodClass> factoryGenerator = valueGeneratorFactory.get(PublicFactoryMethodClass.class);
        ClassValueGenerator<PublicFactoryMethodClass> classValueGenerator = (ClassValueGenerator<PublicFactoryMethodClass>) factoryGenerator;
        Assert.assertTrue(classValueGenerator.getObjectConstructor() instanceof ClassConstructorFactory);
    }

    private static class TwoParametersClass {

        final private int numberValue;
        final private boolean booleanValue;

        private TwoParametersClass() {
            this(10);
        }

        private TwoParametersClass(final int number) {
            this(number, false);
        }

        private TwoParametersClass(final int number, final boolean booleanValue) {
            this.numberValue = number;
            this.booleanValue = booleanValue;
        }
    }

    @Test
    public void testMostParametersUsed() throws Exception {
        Callable<TwoParametersClass> factoryGenerator = valueGeneratorFactory.get(TwoParametersClass.class);
        ClassValueGenerator<TwoParametersClass> classValueGenerator = (ClassValueGenerator<TwoParametersClass>) factoryGenerator;
        Assert.assertTrue(classValueGenerator.getObjectConstructor() instanceof ClassConstructorSimple);
        Assert.assertNotNull(factoryGenerator.call());
        ClassConstructorSimple<?> constructor = (ClassConstructorSimple<?>) classValueGenerator.getObjectConstructor();
        Assert.assertEquals(constructor.getConstructor().getParameterTypes().length, 2);
    }
}
