package com.clemble.test.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.clemble.test.random.constructor.ClassAccessWrapper;
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

    /**
     * Extracts and normalizes Member name.
     *
     * @param member member, which parameters to extract
     * @return member name
     */
    public static String extractMemberName(Member member) {
        String fieldName = extractFieldName(member);
        return (fieldName.startsWith("set") || fieldName.startsWith("add") || fieldName.startsWith("get")) ? fieldName.substring(3) : fieldName;
    }

    /**
     * Extracts and normalizes field name.
     *
     * @param member member, which name to extract
     * @return field name
     */
    public static String extractFieldName(Member member) {
        return member != null ? member.getName().toLowerCase() : "";
    }

    public static Method findSetMethod(final ClassAccessWrapper<?> searchClass, final Field field) {
        return findSetMethod(searchClass, extractFieldName(field));
    }

    /**
     * Finds possible method for specified field name.
     *
     * @param searchClass
     *            class to search in.
     * @param methodName
     *            name of the Method
     * @return possible set method for specified field name.
     */
    public static Method findSetMethod(final ClassAccessWrapper<?> searchClass, final String methodName) {
        // Step 1. Filter method candidates
        Collection<Method> methodCandidates = searchClass.
                getMethods().
                stream().
                filter((method) ->
                    method.getParameterTypes().length == 1 &&
                    method.getName().toLowerCase().startsWith("set") &&
                    extractMemberName(method).equals(methodName)
                ).
                collect(Collectors.toList());
        // Step 2. Return first method in the Collection
        return methodCandidates.isEmpty() ? null : methodCandidates.iterator().next();
    }

    public static Field findField(final ClassAccessWrapper<?> searchClass, final Method method) {
        return findField(searchClass, extractMemberName(method));
    }

    /**
     * Finds field for specified field name.
     *
     * @param searchClass
     *            class to search in.
     * @param fieldName
     *            name of the field.
     * @return Field or null if not found.
     */
    public static Field findField(final ClassAccessWrapper<?> searchClass, final String fieldName) {
        // Step 1. Filter all field's with specified name
        Collection<Field> fieldCandidates = searchClass.
                getFields().
                stream().
                filter((field) -> fieldName.equals(extractFieldName(field))).
                collect(Collectors.toList());
        // Step 2. Return first field in sorted Collection.
        return fieldCandidates.isEmpty() ? null : fieldCandidates.iterator().next();
    }

    public static Method findAddMethod(final ClassAccessWrapper<?> searchClass, final Field field) {
        return findAddMethod(searchClass, extractFieldName(field));
    }

    /**
     * Finds possible add method for specified field name.
     *
     * @param searchClass
     *            Class to search for.
     * @param methodName
     *            name of the method.
     * @return possible add method for specified field name.
     */
    public static Method findAddMethod(final ClassAccessWrapper<?> searchClass, final String methodName) {
        // Step 1. Filter method candidates
        Collection<Method> methodCandidates = searchClass.
            getMethods().stream().filter((method) -> {
                String possibleFieldName = extractMemberName(method);
                return method.getParameterTypes().length == 1 &&
                        method.getName().toLowerCase().startsWith("add") &&
                        (methodName.startsWith(possibleFieldName) || possibleFieldName.startsWith(methodName));
            }).
            collect(Collectors.toList());
        // Step 2. Return first field
        return methodCandidates.isEmpty() ? null : methodCandidates.iterator().next();
    }

}
