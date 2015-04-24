package com.clemble.test.random.constructor;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.clemble.test.random.ObjectGenerator;
import com.clemble.test.random.ValueGeneratorFactory;
import com.clemble.test.reflection.ReflectionUtils;

/**
 * Abstraction of object property.
 * 
 * @author Anton Oparin
 * 
 * @param <T>
 *            parameterized {@link Class}.
 */
abstract public class ClassPropertySetter<T> {

    /**
     * Sets value for configured field.
     * 
     * @param target
     *            T value to update
     */
    abstract public void setProperties(Object target);

    abstract public List<Supplier<?>> getValueGenerators();

    abstract public ClassPropertySetter<T> clone(List<Supplier<?>> generatorsToUse);

    /**
     * Returns affected Class. Supposed to be used primerely inside invocation.
     * 
     * @return affected {@link Class}.
     */
    abstract protected Class<?> getAffectedClass();

    /**
     * Filter for applicable methods, uses only set and add methods
     */
    final private static Predicate<Member> FILTER_APPLICABLE_METHODS = (input) -> {
        if ((input.getModifiers() & Modifier.STATIC) != 0)
            return false;
        String name = input.getName().toLowerCase();
        return name.startsWith("set") || name.startsWith("add");
    };

    /**
     * Comparator based on String presentation, needed to distinguish the same field on the different levels of inheritance.
     */
    final private static Comparator<ClassPropertySetter<?>> COMPARE_STRING_PRESENTATION = new Comparator<ClassPropertySetter<?>>() {
        @Override
        public int compare(final ClassPropertySetter<?> firstPropertySetter, final ClassPropertySetter<?> secondPropertySetter) {
            return firstPropertySetter.toString().compareTo(secondPropertySetter.toString());
        }
    };

    /**
     * Comparator based on Presentation type.
     */
    final static Comparator<ClassPropertySetter<?>> COMPARE_PRESENTATION_TYPE = new Comparator<ClassPropertySetter<?>>() {
        @Override
        public int compare(final ClassPropertySetter<?> firstPropertySetter, final ClassPropertySetter<?> secondPropertySetter) {
            boolean firstSimpleProperty = firstPropertySetter instanceof ClassPropertySimpleSetter;
            boolean secondSimpleProperty = secondPropertySetter instanceof ClassPropertySimpleSetter;
            if (firstSimpleProperty && secondSimpleProperty) {
                // Step 1. Check field names
                Field firstField = ((ClassPropertySimpleSetter<?>) firstPropertySetter).field;
                Field secondField = ((ClassPropertySimpleSetter<?>) secondPropertySetter).field;
                if (firstField != null && secondField != null) {
                    int comparison = secondField.getName().compareTo(firstField.getName());
                    if (comparison != 0)
                        return comparison;
                }
                // Step 2. Check method names
                Method firstMethod = ((ClassPropertySimpleSetter<?>) firstPropertySetter).method;
                Method secondMethod = ((ClassPropertySimpleSetter<?>) secondPropertySetter).method;
                if (firstMethod != null && secondMethod != null) {
                    int comparison = secondMethod.getName().compareTo(firstMethod.getName());
                    if (comparison != 0)
                        return comparison;
                }
                // Step 2. Check classes
                Class<?> firstClass = ((ClassPropertySimpleSetter<?>) firstPropertySetter).getAffectedClass();
                Class<?> secondClass = ((ClassPropertySimpleSetter<?>) secondPropertySetter).getAffectedClass();
                if (firstClass != secondClass) {
                    return firstClass.isAssignableFrom(secondClass) ? 1 : -1;
                }
            } else if (!firstSimpleProperty && !secondSimpleProperty) {
                // Comparison of Collections is equivalent to comparison of the types
                return compare(((ClassPropertyCollectionSetter<?>) firstPropertySetter).initialPropertySetter,
                        ((ClassPropertyCollectionSetter<?>) secondPropertySetter).initialPropertySetter);
            } else if (firstSimpleProperty) {
                return 1;
            } else {
                return -1;
            }
            return 0;
        }
    };

    /**
     * Builds property setter for the specified field.
     * 
     * @param field
     *            field to set
     * @param sourceClass
     *            {@link ClassAccessWrapper} to use.
     * @return PropertySetter for the provided field.
     */
    public static <T> ClassPropertySetter<T> createFieldSetter(final ClassAccessWrapper<?> sourceClass, final Field field) {
        // Step 1. Sanity check
        if (field == null)
            throw new IllegalArgumentException();
        // Step 2. Retrieve possible set name for the field
        Method possibleMethods = ReflectionUtils.findSetMethod(sourceClass, field);
        // Step 3. Create possible field setter.
        return create(sourceClass, field, possibleMethods);
    }

    /**
     * * Constructs property setter based on provided Method.
     * 
     * @param method
     *            target method.
     * @param sourceClass
     *            {@link ClassAccessWrapper} to use.
     * @return constructed PropertySetter for the method, or <code>null</code> if such PropertySetter can't be created.
     */
    public static <T> ClassPropertySetter<T> createMethodSetter(final ClassAccessWrapper<?> sourceClass, final Method method) {
        if (method == null)
            throw new IllegalArgumentException();
        if (method.getParameterTypes().length != 1)
            return null;
        Field possibleField = ReflectionUtils.findField(sourceClass, method);
        return create(sourceClass, possibleField, method);
    }

    /**
     * Generates PropertySetter for provided Field, Method and {@link Supplier}.
     * 
     * @param field
     *            target field.
     * @param method
     *            target set method.
     * @param sourceClass
     *            {@link ClassAccessWrapper} to use.
     * @return constructed PropertySetter.
     */
    public static <T> ClassPropertySetter<T> create(final ClassAccessWrapper<?> sourceClass, final Field field, final Method method) {
        return create(sourceClass, field, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> ClassPropertySetter<T> create(final ClassAccessWrapper<?> sourceClass, final Field field, final Method method, Supplier<T> valueGenerator) {
        if(field == null && method == null)
            return null;

        Class<T> targetClass = (Class<T>) (field != null ? field.getType() : method.getParameterTypes()[0]);
        if (valueGenerator == null) {
            if (Collection.class.isAssignableFrom(targetClass)) {
                if (field != null)
                    return new ClassPropertyCollectionSetter<T>(sourceClass, field);
                else
                    return new ClassPropertyCollectionSetter<T>(sourceClass, method);
            } else {
                valueGenerator = ObjectGenerator.getValueGenerator(targetClass);
            }
        }

        return new ClassPropertySimpleSetter<T>(field, method, valueGenerator);
    }

    /**
     * Extracts all possible PropertySetters with specified access level.
     * @param <T>
     *            source class parameter, for whom property setters are needed.
     * @param searchClass
     *            {@link ClassAccessWrapper} access wrapper to generate properties for.
     * @return list of all PropertySetter it can set, ussing specified field.
     */
    public static <T> Collection<ClassPropertySetter<?>> extractAvailableProperties(final ClassAccessWrapper<T> searchClass, final ValueGeneratorFactory valueGeneratorFactory) {
        // Step 1. Create Collection field setters
        final Collection<ClassPropertySetter<?>> propertySetters = new TreeSet<ClassPropertySetter<?>>(COMPARE_STRING_PRESENTATION);
        propertySetters.addAll(valueGeneratorFactory.getPropertySetterManager().getApplicableProperties(searchClass));
        for (Field field : searchClass.getFields()) {
            if ((field.getModifiers() & Modifier.STATIC) == 0)
                propertySetters.add(createFieldSetter(searchClass, field));
        }
        // Step 2. Create Collection of method setters
        searchClass.getMethods().stream().
            filter(FILTER_APPLICABLE_METHODS).
            forEach((method) -> {
                if (method.getParameterTypes().length != 1 ||
                    method.getParameterTypes()[0] == Object.class ||
                    searchClass.getSourceClass().equals(method.getParameterTypes()[0])) {
                } else {
                    ClassPropertySetter<?> propertySetter = createMethodSetter(searchClass, method);
                    if (propertySetter != null) {
                        propertySetters.add(propertySetter);
                    }
                }
            });

        final List<ClassPropertySetter<?>> resultSetters = new ArrayList<ClassPropertySetter<?>>(propertySetters);
        Collections.sort(resultSetters, COMPARE_PRESENTATION_TYPE);
        // Step 3. Returning accumulated result
        return resultSetters;
    }

    public static <T> ClassPropertySetter<T> constructPropertySetter(final ClassAccessWrapper<T> searchClass, final ValueGeneratorFactory valueGeneratorFactory) {
        return new ClassPropertyCombinedSetter<T>(extractAvailableProperties(searchClass, valueGeneratorFactory));
    }

}
