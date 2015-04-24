package com.clemble.test.random.constructor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.clemble.test.random.ValueGeneratorFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

/**
 * Generates new {@link Object}, based on factory method.
 * 
 * @author Anton Oparin
 * 
 * @param <T>
 *            {@link Class} parameter.
 */
final public class ClassConstructorFactory<T> extends ClassConstructor<T> {
    /**
     * Factory method to use.
     */
    final private Method builder;
    /**
     * {@link Collection} of {@link Supplier} to use in factory method.
     */
    final private List<Supplier<?>> constructorValueGenerators;

    /**
     * Default constructor.
     * 
     * @param builder
     *            factory method to use.
     * @param constructorValueGenerators
     *            {@link Collection} of {@link java.util.concurrent.Callable} to use.
     */
    public ClassConstructorFactory(Method builder, Collection<Supplier<?>> constructorValueGenerators) {
        this.builder = checkNotNull(builder);
        this.constructorValueGenerators = ImmutableList.<Supplier<?>>copyOf(checkNotNull(constructorValueGenerators));
    }

    @Override
	@SuppressWarnings({"rawtypes", "unchecked"})
    public T construct() {
        Object generatedObject = null;
        try {
            // Step 1. Generate value for Constructor
            Collection values = new ArrayList();
            for (Supplier<?> valueGenerator : constructorValueGenerators)
                values.add(valueGenerator.get());
            // Step 2. Invoke constructor, creating empty Object
            builder.setAccessible(true);
            generatedObject = values.size() == 0 ? builder.invoke(null, ImmutableList.of().toArray()) : builder.invoke(null, values.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (T) generatedObject;
    }

	@Override
	public List<Supplier<?>> getValueGenerators() {
		return constructorValueGenerators;
	}

	@Override
	public ClassConstructor<T> clone(List<Supplier<?>> generatorsToUse) {
		return new ClassConstructorFactory<T>(builder, generatorsToUse);
	}

	public static Collection<Method> getPossibleFactoryMethods(final ClassAccessWrapper<?> classToGenerate) {
	    return Collections2.filter(classToGenerate.getMethods(), new Predicate<Method>() {
            @Override
            public boolean apply(final Method method) {
            if ((method.getModifiers() & Modifier.STATIC) == 0 || !classToGenerate.canBeReplacedWith(method.getReturnType()))
                return false;
            for (Class<?> parameter : method.getParameterTypes()) {
                if (classToGenerate.canBeReplacedWith(parameter) || classToGenerate.canReplace(parameter))
                    return false;
            }
            return true;
            }
        });
	}
	
    /**
     * Tries to build {@link ClassConstructor} based on factory method.
     *
     * @param <T> the type of object to construct
     * @param classToGenerate
     *            {@link Class} to generate.
     * @param valueGeneratorFactory
     *            {@link ValueGeneratorFactory} to use.
     * @return {@link ClassConstructor} if it is possible to generate one, <code>null</code> otherwise.
     */
    public static <T> ClassConstructorFactory<T> build(
            final ClassAccessWrapper<?> classToGenerate,
            final ValueGeneratorFactory valueGeneratorFactory) {
        // Step 1. Filter static methods, that return instance of the type as a result
        Collection<Method> possibleBuilders = getPossibleFactoryMethods(classToGenerate);
        // Step 2. If there is no such method return null
        if (possibleBuilders.size() == 0)
            return null;
        // Step 3. Select constructor with most arguments
        Method builder = null;
        ClassConstructorFactory<T> constructorFactory = null;
        for (Method candidate : possibleBuilders) {
            try {
                // Step 3.1. Checking that candidate can actually constuct the Object
                ClassConstructorFactory<T> candidateFactory =  new ClassConstructorFactory<T>(candidate, valueGeneratorFactory.get(candidate.getParameterTypes()));
                candidateFactory.construct();
                // Step 3.2. If involves more parameters use it
                if (builder == null || candidate.getParameterTypes().length > builder.getParameterTypes().length) {
                    builder = candidate;
                    constructorFactory = candidateFactory;
                }
            } catch (Throwable throwable) {
                // Ignore failure, try another one
            }
        }
        // Step 4. Creating factory method based
        return constructorFactory;
    }
    
}