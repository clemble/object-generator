package com.clemble.test.random.constructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import com.clemble.test.random.ObjectGenerator;

/**
 * Property Setter for Collection fields.
 * 
 * @author Anton Oparin
 * 
 * @param <T>
 *            parameterized {@link Class}
 */
final class ClassPropertyCollectionSetter<T> extends ClassPropertySetter<T> {

    /**
     * SimplePropertySetter to initialize field with empty Collection.
     */
    final ClassPropertySimpleSetter<T> initialPropertySetter;
    /**
     * Add method if there is one for the field (It is impossible to identify collection type in runtime),
     */
    final private Method method;
    /**
     * ValueGenerator to use for additional value generation.
     */
    final private Callable<T> valueGenerator;

    ClassPropertyCollectionSetter(final ClassPropertySimpleSetter<T> iInitialPropertySetter, final Method iMethod, final Callable<T> iValueGenerator) {
        this.method = iMethod;
        this.valueGenerator = iValueGenerator;
        this.initialPropertySetter = iInitialPropertySetter;
    }

    /**
     * Default constructor.
     * 
     * @param sourceClass
     *            source Class for the object
     * @param field
     *            object field
     */
    @SuppressWarnings("unchecked")
    ClassPropertyCollectionSetter(final ClassAccessWrapper<?> sourceClass, final Field field) {
        Method addMethod = findAddMethod(sourceClass, ClassPropertySetter.extractFieldName(field));
        Method setMethod = findSetMethod(sourceClass, ClassPropertySetter.extractFieldName(field));

        this.initialPropertySetter = new ClassPropertySimpleSetter<T>(field, setMethod, (Callable<T>) ObjectGenerator.getValueGenerator(field.getType()));

        if (addMethod != null) {
            this.valueGenerator = (Callable<T>) ObjectGenerator.getValueGenerator(addMethod.getParameterTypes()[0]);
            this.method = addMethod;
        } else {
            this.valueGenerator = null;
            this.method = null;
        }
    }

    @SuppressWarnings("unchecked")
    ClassPropertyCollectionSetter(final ClassAccessWrapper<?> sourceClass, final Method setMethod) {
        Method addMethod = findAddMethod(sourceClass, setMethod.getName().substring(3));

        this.initialPropertySetter = new ClassPropertySimpleSetter<T>(null, setMethod, (Callable<T>) ObjectGenerator.
                getValueGenerator(setMethod.getParameterTypes()[0]));

        if (addMethod != null) {
            this.valueGenerator = (Callable<T>) ObjectGenerator.getValueGenerator(addMethod.getParameterTypes()[0]);
            this.method = addMethod;
        } else {
            this.valueGenerator = null;
            this.method = null;
        }

    }

    @Override
    public void setProperties(Object target) {
        // Step 1. Generating initial empty Collection
        initialPropertySetter.setProperties(target);
        // Step 2. Setting method as a regular expression
        Object valueToSet = null;
        try {
            if (method != null && valueGenerator != null) {
                valueToSet = valueGenerator.call();
                method.invoke(target, valueToSet);
            }
        } catch (Exception methodSetException) {
            try {
                method.setAccessible(true);
                method.invoke(target, valueToSet);
            } catch (Exception exception) {
            }
        }
    }

    @Override
    protected Class<?> getAffectedClass() {
        return initialPropertySetter.getAffectedClass();
    }

    @Override
    public String toString() {
        return initialPropertySetter.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Callable<?>> getValueGenerators() {
        return (List<Callable<?>>) (Collection<?>) Collections.singletonList(valueGenerator);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClassPropertySetter<T> clone(List<Callable<?>> generatorsToUse) {
        return new ClassPropertyCollectionSetter<T>(initialPropertySetter, method, (Callable<T>) generatorsToUse.remove(0));
    }
}