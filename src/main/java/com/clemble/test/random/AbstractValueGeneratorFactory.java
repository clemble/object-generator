package com.clemble.test.random;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import com.clemble.test.random.constructor.ClassAccessWrapper;
import com.clemble.test.random.constructor.ClassConstructor;
import com.clemble.test.random.constructor.ClassPropertySetter;
import com.clemble.test.random.constructor.ClassPropertySetterRegistry;
import com.clemble.test.random.constructor.ClassValueGenerator;
import com.google.common.collect.ImmutableMap;
import com.clemble.test.reflection.ReflectionUtils;

abstract public class AbstractValueGeneratorFactory implements ValueGeneratorFactory {
    /**
     * {@link Collection} random value generator, as a result empty {@link ArrayList} returned.
     */
    @SuppressWarnings("rawtypes")
    final public static Callable<Collection<?>> COLLECTION_VALUE_GENERATOR = () -> new ArrayList();

    /**
     * {@link List} random value generator, as a result generates empty {@link ArrayList} returned.
     */
    @SuppressWarnings("rawtypes")
    final public static Callable<List<?>> LIST_VALUE_GENERATOR = () ->  new ArrayList();

    /**
     * {@link Queue} random value generator as a result empty {@link ArrayDeque} returned.
     */
    @SuppressWarnings("rawtypes")
    final public static Callable<Queue<?>> QUEUE_VALUE_GENERATOR = () -> new ArrayDeque();

    /**
     * {@link Deque} random value generator, as a result emptu {@link ArrayDeque} returned.
     */
    @SuppressWarnings("rawtypes")
    final public static Callable<Deque<?>> DEQUE_VALUE_GENERATOR = () -> new ArrayDeque();

    /**
     * {@link Set} random value generator, as a result empty {@link HashSet} returned.
     */
    @SuppressWarnings("rawtypes")
    final public static Callable<Set<?>> SET_VALUE_GENERATOR = () -> new HashSet();

    /**
     * {@link Map} random value generator, as a result empty {@link HashMap} returned.
     */
    @SuppressWarnings("rawtypes")
    final public static Callable<Map<?, ?>> MAP_VALUE_GENERATOR = () -> new HashMap();

    final private Map<Class<?>, Callable<?>> DEFAULT_GENERATORS;

    final private Map<Class<?>, Callable<?>> REGISTERED_GENERATORS = new HashMap<Class<?>, Callable<?>>();

    final private ClassPropertySetterRegistry propertySetterManager;

    public AbstractValueGeneratorFactory() {
        this(new ClassPropertySetterRegistry());
    }

    public AbstractValueGeneratorFactory(final ClassPropertySetterRegistry setterManager) {
        this(setterManager, null);
    }

    public AbstractValueGeneratorFactory(final ClassPropertySetterRegistry setterManager, Map<Class<?>, Callable<?>> defaultGenerators) {
        this.propertySetterManager = setterManager != null ? setterManager : new ClassPropertySetterRegistry();

        HashMap<Class<?>, Callable<?>> standardValueGenerators = new HashMap<Class<?>, Callable<?>>();

        standardValueGenerators.put(Collection.class, COLLECTION_VALUE_GENERATOR);
        standardValueGenerators.put(List.class, LIST_VALUE_GENERATOR);
        standardValueGenerators.put(Set.class, SET_VALUE_GENERATOR);
        standardValueGenerators.put(Queue.class, QUEUE_VALUE_GENERATOR);
        standardValueGenerators.put(Deque.class, DEQUE_VALUE_GENERATOR);

        standardValueGenerators.put(Map.class, MAP_VALUE_GENERATOR);

        if (defaultGenerators != null)
            standardValueGenerators.putAll(defaultGenerators);

        DEFAULT_GENERATORS = ImmutableMap.copyOf(standardValueGenerators);
    }

    final public <T> void put(Class<T> klass, Callable<T> valueGenerator) {
        if (klass != null && valueGenerator != null)
            REGISTERED_GENERATORS.put(klass, valueGenerator);
    }

    @Override
    final public Collection<Callable<?>> getValueGenerators(Class<?>[] parameters) {
        // Step 1. Sanity check
        if (parameters == null || parameters.length == 0)
            return Collections.emptyList();
        // Step 2. Sequential check
        Collection<Callable<?>> resultGenerators = new ArrayList<Callable<?>>();
        for (Class<?> parameter : parameters) {
            if (parameter != null)
                resultGenerators.add(getValueGenerator(parameter));
        }
        return resultGenerators;
    }

    @Override
    final public ClassPropertySetterRegistry getPropertySetterManager() {
        return propertySetterManager;
    }

    /**
     * Produces {@link Callable} for specified {@link Class}.
     * 
     * @param klass
     *            generated {@link Class}
     * @return {@link Callable} for procided {@link Class}
     */
    @SuppressWarnings("unchecked")
    public <T> Callable<T> getValueGenerator(Class<T> klass) {
        // Step 1. Checking that it can be replaced with standard constructors
        Callable<T> valueGenerator = (Callable<T>) DEFAULT_GENERATORS.get(klass);
        if (valueGenerator != null)
            return valueGenerator;
        // Step 1.1. Checking registered generators
        valueGenerator = (Callable<T>) REGISTERED_GENERATORS.get(klass);
        if(valueGenerator != null)
            return valueGenerator;
        // Step 2. If this is enum replace with Random value generator
        if (klass.isEnum())
            return enumValueGenerator(klass);
        // Step 3. Initialize value generator with primarily public access
        valueGenerator = construct(ClassAccessWrapper.createPublicAccessor(klass));
        if (valueGenerator != null)
            return valueGenerator;
        // Step 4. Trying to initialize with all available access
        valueGenerator = construct(ClassAccessWrapper.createAllMethodsAccessor(klass));
        if (valueGenerator != null)
            return valueGenerator;
        // Step 5. Special case for an array
        if(klass.isArray()) {
            return arrayValueGenerator(klass);
        }
        for(Class<?> registered: REGISTERED_GENERATORS.keySet()) {
            if(klass.isAssignableFrom(registered)) {
                return (Callable<T>) REGISTERED_GENERATORS.get(registered);
            }
        }
        // Step 7. If there is no result throw IllegalArgumentException
        throw new IllegalArgumentException("Can't construct " + klass.getSimpleName() + "(" + (klass.getCanonicalName() != null ? klass.getCanonicalName() : (klass.getName() != null ? klass.getName() : klass.toString())) + ")");
    }

    /**
     * Produces {@link Callable} for provided {@link ClassAccessWrapper}, returned {@link Callable} can return any subtype of the target
     * {@link Class}.
     * 
     * @param sourceClass
     *            {@link ClassAccessWrapper} with defined level of access.
     * @return {@link Callable} for provided class if, there is possible to create one.
     */
    @SuppressWarnings("unchecked")
    private <T> Callable<T> construct(final ClassAccessWrapper<T> sourceClass) {
        Callable<T> valueGenerator = sourceClass.constructable() ? tryConstruct(sourceClass) : null;
        if (valueGenerator != null)
            return valueGenerator;
        // Step 3.1 Trying to initialize sub classes
        Collection<Class<? extends T>> subClasses = ReflectionUtils.findPossibleImplementations(sourceClass.getSourceClass());
        // Step 3.2 Checking extended list of candidates
        for (Class<?> subClass : subClasses) {
            ClassAccessWrapper<?> childWrapper = sourceClass.wrap(subClass);
            if(REGISTERED_GENERATORS.containsKey(childWrapper.getSourceClass()))
                return (Callable<T>) REGISTERED_GENERATORS.get(childWrapper.getSourceClass());
            if (childWrapper.constructable()) {
                valueGenerator = (Callable<T>) tryConstruct(childWrapper);
                if (valueGenerator != null)
                    return valueGenerator;
            }
        }
        return null;
    }

    /**
     * Produces {@link Callable} for a {@link Class} with restricted access.
     * 
     * @param classToGenerate
     *            {@link ClassAccessWrapper} for the class.
     * @return {@link Callable} if it is possible to create one, with defined access level, <code>null</code> otherwise.
     */
    private <T> Callable<T> tryConstruct(final ClassAccessWrapper<T> classToGenerate) {
        // Step 1. Selecting appropriate constructor
        ClassConstructor<T> objectConstructor = ClassConstructor.construct(classToGenerate, this);
        if (objectConstructor == null)
            return null;
        // Step 2. Selecting list of applicable specific selectors from specific properties
        ClassPropertySetter<T> classPropertySetter = ClassPropertySetter.constructPropertySetter(classToGenerate, this);
        // Step 3. Generating final ClassGenerator for the type
        return new ClassValueGenerator<T>(objectConstructor, classPropertySetter);
    }

    protected abstract <T> Callable<T> enumValueGenerator(Class<T> klass);

    protected abstract <T> Callable<T> arrayValueGenerator(Class<?> klass);

}
