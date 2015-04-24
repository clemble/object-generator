package com.clemble.test.random.constructor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.clemble.test.random.ValueGeneratorFactory;
import com.google.common.collect.ImmutableList;

/**
 * Generates new {@link Object}, based on constructor.
 * 
 * @author Anton Oparin
 * 
 * @param <T>
 *            {@link Class} parameter.
 */
public final class ClassConstructorSimple<T> extends ClassConstructor<T> {
	/**
	 * Constructor to use.
	 */
	final private Constructor<T> constructor;
	/**
	 * Set of values to generate parameters for the constructor.
	 */
	final private List<Supplier<?>> constructorValueGenerators;

	/**
	 * Constructor based generation.
	 * 
	 * @param constructor
	 *            constructor to use.
	 * @param constructorValueGenerators
	 *            {@link Supplier} to use.
	 */
	public ClassConstructorSimple(final Constructor<T> constructor, final Collection<Supplier<?>> constructorValueGenerators) {
		this.constructor = checkNotNull(constructor);
		this.constructorValueGenerators = ImmutableList.<Supplier<?>>copyOf(checkNotNull(constructorValueGenerators));
	}

	/**
	 * Getter of constructor.
	 * 
	 * @return constructor.
	 */
	public Constructor<T> getConstructor() {
		return constructor;
	}

	/**
	 * Returns a {@link Collection} of {@link Supplier} to use, while construction.
	 * 
	 * @return {@link Collection} of {@link Supplier} to use, while construction.
	 */
	public Collection<Supplier<?>> getConstructorValueGenerators() {
		return constructorValueGenerators;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public T construct() {
		Object generatedObject = null;
		try {
            // Step 1. Generate value for Constructor
            Collection values = new ArrayList();
            for (Supplier<?> valueGenerator : getConstructorValueGenerators())
                values.add(valueGenerator.get());
            // Step 1.1 Make constructor accessible if needed
            if (!constructor.isAccessible())
			    constructor.setAccessible(true);
            // Step 2. Invoke constructor, creating empty Object
			generatedObject = values.size() == 0 ? getConstructor().newInstance() : getConstructor().newInstance(values.toArray());
		} catch (Exception e) {
			throw new RuntimeException("Failed to construct using " + getConstructor(),e);
		}
		return (T) generatedObject;
	}

	@Override
	public List<Supplier<?>> getValueGenerators() {
		return constructorValueGenerators;
	}
	
	@Override
	public ClassConstructor<T> clone(List<Supplier<?>> generatorsToUse) {
		return new ClassConstructorSimple<T>(constructor, generatorsToUse);
	}

	/**
	 * Tries to build {@link ClassConstructor} based on constructor.
     *
	 * @param <T> the type of object to construct
	 * @param classToGenerate
	 *            {@link Class} to generate.
	 * @param valueGeneratorFactory
	 *            {@link ValueGeneratorFactory} to use.
	 * @return {@link ClassConstructor} if it is possible to generate one, <code>null</code> otherwise.
	 */
	@SuppressWarnings("unchecked")
    public static <T> ClassConstructorSimple<T> build(
        final ClassAccessWrapper<?> classToGenerate,
        final ValueGeneratorFactory valueGeneratorFactory) {
        Constructor<?>[] constructors = classToGenerate.getConstructors();
        // Step 1. Selecting appropriate constructor
        if (constructors.length == 0)
            return null;
        // Step 2. Searching for default Constructor or constructor with least configurations
        // Step 2.1 Filtering classes with parameters that can be cast to constructed class
        Collection<Constructor<?>> filteredConstructors = Arrays.
            asList(constructors).
            stream().
            filter((constructor) -> {
                for (Class<?> parameter : constructor.getParameterTypes())
                    // Prevent circular references
                    if (classToGenerate.canBeReplacedWith(parameter) || classToGenerate.canReplace(parameter))
                        return false;
                return true;
            }).
            collect(Collectors.toList());
        // Step 3. Selecting constructor that would best fit for processing
        ClassConstructorSimple<T> simpleConstructor = null;
        Constructor<?> bestCandidate = null;
        for (Constructor<?> candidate : filteredConstructors) {
            if (bestCandidate == null || candidate.getParameterTypes().length > bestCandidate.getParameterTypes().length) {
                // Step 4.1 Choosing generators for Constructor variable
                ClassConstructorSimple<T> candidateConstructor =  new ClassConstructorSimple<T>((Constructor<T>) candidate, valueGeneratorFactory.get(candidate.getParameterTypes()));
                try {
                    candidateConstructor.construct();
                    simpleConstructor = candidateConstructor;
                    bestCandidate = candidate;
                } catch (Throwable throwable) {
                    // Ignore construction failure
                }
            }
        }
        // Step 4.2 Returning default null value
        return simpleConstructor;
    }

}