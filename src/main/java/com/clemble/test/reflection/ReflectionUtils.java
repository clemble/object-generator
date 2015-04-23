package com.clemble.test.reflection;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.reflections.Reflections;

/**
 * Collection of internal Reflection utilities used by stresstest packages.
 *
 * @author Anton Oparin
 */
public class ReflectionUtils {

    /**
     * Protected constructor
     */
    private ReflectionUtils() {
        throw new IllegalAccessError();
    }

    /**
     * Searches for annotation in provided and all parent Classes.
     *
     * @param <T>             the type of annotation class
     * @param klass           source Class
     * @param annotationClass searched annotation
     * @return annotation on this Class or any of it's subclasses
     */
    public static <T extends Annotation> T findAnnotation(Class<?> klass, Class<T> annotationClass) {
        if (Object.class == klass)
            return null;
        T result = klass.getAnnotation(annotationClass);
        return result != null ? result : findAnnotation(klass.getSuperclass(), annotationClass);
    }

    /**
     * Searches for possible implementations of the package in original package of the Class and all underlying packages.
     *
     * @param <T>   the type of object to search
     * @param klass source Class
     * @return all implementations that can be used as Class
     * from original package and all sub packages.
     */
    public static <T> Set<Class<? extends T>> findPossibleImplementations(Class<T> klass) {
        if (klass == null || klass.getPackage() == null || klass.getPackage().getName() == null)
            return Collections.emptySet();
        String packageName = klass.getPackage().toString().replace("package ", "");
        return findPossibleImplementations(packageName, klass);
    }

    /**
     * Searches for possible implementations of the package in provided package and all underlying packages.
     *
     * @param <T>         the type of object to search
     * @param klass       source Class
     * @param packageName search start point
     * @return all implementations that can be used as Class
     * from provided package and all sub packages.
     */
    public static <T> Set<Class<? extends T>> findPossibleImplementations(String packageName, Class<T> klass) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(klass);
        return subTypes;
    }

    /**
     * Searches for all possible annotations associated with the field.
     *
     * @param klass       source Class
     * @param field       field name
     * @return Stream of all annotations associated with the field.
     */
    public static Stream<Annotation> findAllAnnotations(String field, Class<?> klass) {
        // Step 1. Checking methods
        Stream<Annotation> methodAnnotations = Arrays.asList(klass.getDeclaredMethods()).
            stream().
            filter(m -> m.getName().toLowerCase().contains(field.toLowerCase())).
            flatMap(m -> Arrays.asList(m.getDeclaredAnnotations()).stream());
        // Step 2. Checking field
        Stream<Annotation> fieldAnnotations = Arrays.asList(klass.getDeclaredFields()).
            stream().
            filter(f -> f.getName().toLowerCase().equals(field.toLowerCase())).
            flatMap(f -> Arrays.asList(f.getDeclaredAnnotations()).stream());
        // Step 3. Combining annotations
        // Can't extract data from constructor parameter, since there is no information saved after compilation
        return Stream.concat(fieldAnnotations, methodAnnotations);
    }

}
