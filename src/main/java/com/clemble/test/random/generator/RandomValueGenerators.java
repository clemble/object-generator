package com.clemble.test.random.generator;

import java.util.*;
import java.util.function.Supplier;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.ImmutableMap;

public abstract class RandomValueGenerators {

    /**
     * Generic source of randomness in all value generators (shared for performance reasons).
     */
    final public static Random RANDOM_UTILS = new Random();

    /**
     * {@link Boolean} random value generator.
     */
    final public static Supplier<Boolean> BOOLEAN_VALUE_GENERATOR = () -> RANDOM_UTILS.nextBoolean();

    /**
     * {@link Byte} array random value generator.
     */
    final public static Supplier<boolean[]> BOOLEAN_ARRAY_VALUE_GENERATOR = () -> {
        boolean[] resultArray = new boolean[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = BOOLEAN_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link Byte} random value generator.
     */
    final public static Supplier<Byte> BYTE_VALUE_GENERATOR = () -> (byte) RANDOM_UTILS.nextInt();

    /**
     * {@link Byte} array random value generator.
     */
    final public static Supplier<byte[]> BYTE_ARRAY_VALUE_GENERATOR = () -> {
        byte[] resultArray = new byte[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = BYTE_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link Character} random value generator.
     */
    final public static Supplier<Character> CHAR_VALUE_GENERATOR = () -> (char) RANDOM_UTILS.nextInt((int) Character.MAX_VALUE);

    /**
     * {@link Character} array random value generator.
     */
    final public static Supplier<char[]> CHAR_ARRAY_VALUE_GENERATOR = () -> {
        char[] resultArray = new char[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = CHAR_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link Integer} random value generator.
     */
    final public static Supplier<Integer> INTEGER_VALUE_GENERATOR = () -> RANDOM_UTILS.nextInt();

    /**
     * {@link Integer} array random value generator.
     */
    final public static Supplier<int[]> INTEGER_ARRAY_VALUE_GENERATOR = () -> {
        int[] resultArray = new int[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = INTEGER_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link Short} random value generator.
     */
    final public static Supplier<Short> SHORT_VALUE_GENERATOR = () -> (short) RANDOM_UTILS.nextInt();

    /**
     * {@link Short} array random value generator.
     */
    final public static Supplier<short[]> SHORT_ARRAY_VALUE_GENERATOR = () -> {
        short[] resultArray = new short[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = SHORT_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link Long} random value generator.
     */
    final public static Supplier<Long> LONG_VALUE_GENERATOR = () -> RANDOM_UTILS.nextLong();

    /**
     * {@link Long} array random value generator.
     */
    final public static Supplier<long[]> LONG_ARRAY_VALUE_GENERATOR = () -> {
        long[] resultArray = new long[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = LONG_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link Float} random value generator.
     */
    final public static Supplier<Float> FLOAT_VALUE_GENERATOR = () -> RANDOM_UTILS.nextFloat();

    /**
     * {@link Float} array random value generator.
     */
    final public static Supplier<float[]> FLOAT_ARRAY_VALUE_GENERATOR = () -> {
        float[] resultArray = new float[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = LONG_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link Double} random value generator.
     */
    final public static Supplier<Double> DOUBLE_VALUE_GENERATOR = () -> RANDOM_UTILS.nextDouble();

    /**
     * {@link Double} array random value generator.
     */
    final public static Supplier<double[]> DOUBLE_ARRAY_VALUE_GENERATOR = () -> {
        double[] resultArray = new double[1 + RANDOM_UTILS.nextInt(10)];
        for (int i = 0; i < resultArray.length; i++)
            resultArray[i] = LONG_VALUE_GENERATOR.get();
        return resultArray;
    };

    /**
     * {@link String} generates random String of 10 characters long.
     */
    final public static Supplier<String> STRING_VALUE_GENERATOR = () -> RandomStringUtils.randomAscii(10);

    /**
     * Generates random selection from list of enums
     *
     * @param <T> the type of enum class
     * @param enumClass source enum classs
     * @return random enum value
     */
    final public static <T> Supplier<T> enumValueGenerator(final Class<T> enumClass) {
        if (!enumClass.isEnum())
            throw new IllegalArgumentException("Class must be of type enum");
        return valueGenerator(Arrays.asList(enumClass.getEnumConstants()));
    }

    /**
     * Generates random selection from list of elements
     *
     * @param <T> the type of elements in list
     * @param iterable
     *            {@link Iterable} of possible values.
     * @return ValueGenerator that returns one of the elements of original {@link Iterable}.
     */
    final public static <T> Supplier<T> valueGenerator(final Iterable<T> iterable) {
        final List<T> randomValues = new ArrayList<T>();
        for (T value : iterable)
            randomValues.add(value);
        return () -> randomValues.get(RANDOM_UTILS.nextInt(randomValues.size()));
    }

    /**
     * Generates random {@link String} generator, that produces random {@link String} of defined length.
     * 
     * @param length
     *            size of the {@link String} to generate.
     * @return random String.
     */
    final public static Supplier<String> randomString(final int length) {
        if (length <= 0)
            throw new IllegalArgumentException("Length must be possitive");
        return () -> RandomStringUtils.random(length);
    }

    /**
     * Generates random alphabetic {@link String} generator, that produces random alphabetic {@link String} of defined length.
     * 
     * @param length
     *            size of the {@link String} to generate.
     * @return random alphabetic {@link String}.
     */
    final public static Supplier<String> randomAlphabeticString(final int length) {
        if (length <= 0)
            throw new IllegalArgumentException("Length must be possitive");
        return () -> RandomStringUtils.randomAlphabetic(length);
    }

    /**
     * Generates random alphanumeric {@link String} generator, that produces random alphanumeric {@link String} of defined length.
     * 
     * @param length
     *            size of the {@link String} to generate.
     * @return random alphanumeric {@link String}.
     */
    final public static Supplier<String> randomAlphanumericString(final int length) {
        if (length <= 0)
            throw new IllegalArgumentException("Length must be possitive");
        return () -> RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * Generates random ASCII {@link String} generator, that produces random ASCII {@link String} of defined length.
     * 
     * @param length
     *            size of the {@link String} to generate.
     * @return random ASCII {@link String}.
     */
    final public static Supplier<String> randomAsciiString(final int length) {
        if (length <= 0)
            throw new IllegalArgumentException("Length must be possitive");
        return () -> RandomStringUtils.randomAscii(length);
    }

    /**
     * Collection of standard value generators, which must be used by default
     */
    final public static Map<Class<?>, Supplier<?>> DEFAULT_GENERATORS;
    static {
        Map<Class, Supplier> valueGenerators = new HashMap<Class, Supplier>();
        valueGenerators.put(String.class, RandomValueGenerators.STRING_VALUE_GENERATOR);

        valueGenerators.put(Boolean.class, RandomValueGenerators.BOOLEAN_VALUE_GENERATOR);
        valueGenerators.put(boolean.class, RandomValueGenerators.BOOLEAN_VALUE_GENERATOR);
        valueGenerators.put(boolean[].class, RandomValueGenerators.BOOLEAN_ARRAY_VALUE_GENERATOR);

        valueGenerators.put(Byte.class, RandomValueGenerators.BYTE_VALUE_GENERATOR);
        valueGenerators.put(byte.class, RandomValueGenerators.BYTE_VALUE_GENERATOR);
        valueGenerators.put(byte[].class, RandomValueGenerators.BYTE_ARRAY_VALUE_GENERATOR);

        valueGenerators.put(Character.class, RandomValueGenerators.CHAR_VALUE_GENERATOR);
        valueGenerators.put(char.class, RandomValueGenerators.CHAR_VALUE_GENERATOR);
        valueGenerators.put(char[].class, RandomValueGenerators.CHAR_ARRAY_VALUE_GENERATOR);

        valueGenerators.put(Short.class, RandomValueGenerators.SHORT_VALUE_GENERATOR);
        valueGenerators.put(short.class, RandomValueGenerators.SHORT_VALUE_GENERATOR);
        valueGenerators.put(short[].class, RandomValueGenerators.SHORT_ARRAY_VALUE_GENERATOR);

        valueGenerators.put(Integer.class, RandomValueGenerators.INTEGER_VALUE_GENERATOR);
        valueGenerators.put(int.class, RandomValueGenerators.INTEGER_VALUE_GENERATOR);
        valueGenerators.put(int[].class, RandomValueGenerators.INTEGER_ARRAY_VALUE_GENERATOR);

        valueGenerators.put(Long.class, RandomValueGenerators.LONG_VALUE_GENERATOR);
        valueGenerators.put(long.class, RandomValueGenerators.LONG_VALUE_GENERATOR);
        valueGenerators.put(long[].class, RandomValueGenerators.LONG_ARRAY_VALUE_GENERATOR);

        valueGenerators.put(Float.class, RandomValueGenerators.FLOAT_VALUE_GENERATOR);
        valueGenerators.put(float.class, RandomValueGenerators.FLOAT_VALUE_GENERATOR);
        valueGenerators.put(float[].class, RandomValueGenerators.FLOAT_ARRAY_VALUE_GENERATOR);

        valueGenerators.put(Double.class, RandomValueGenerators.DOUBLE_VALUE_GENERATOR);
        valueGenerators.put(double.class, RandomValueGenerators.DOUBLE_VALUE_GENERATOR);
        valueGenerators.put(double[].class, RandomValueGenerators.DOUBLE_ARRAY_VALUE_GENERATOR);

        DEFAULT_GENERATORS = ImmutableMap.copyOf((Map<Class<?>, Supplier<?>>)(Map<?, ?>) valueGenerators);
    }

}
