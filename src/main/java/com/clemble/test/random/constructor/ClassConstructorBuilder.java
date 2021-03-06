package com.clemble.test.random.constructor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.clemble.test.random.ValueGeneratorFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Generates new {@link Object}, based on Builder.
 *
 * @param <T> {@link Class} to use.
 * @author Anton Oparin
 */
public class ClassConstructorBuilder<T> extends ClassConstructor<T> {

    /**
     * {@link ClassConstructor} builder method to use.
     */
    final private ClassConstructorFactory<?> builderFactoryMethod;
    /**
     * {@link ClassPropertyCombinedSetter} to use in builder class.
     */
    final private ClassPropertySetter<?> classPropertySetter;
    /**
     * Method that generates target value
     */
    final private Method valueBuilderMethod;

    /**
     * Default constructor.
     *
     * @param builderFactoryMethod factory method to construct builder.
     * @param classPropertySetter  {@link Collection} of {@link Supplier} to use as property setters.
     * @param valueBuilderMethod   method to generate value.
     */
    private ClassConstructorBuilder(final ClassConstructorFactory<?> builderFactoryMethod,
                                    final ClassPropertySetter<?> classPropertySetter, final Method valueBuilderMethod) {
        this.builderFactoryMethod = checkNotNull(builderFactoryMethod);
        this.classPropertySetter = checkNotNull(classPropertySetter);
        this.valueBuilderMethod = checkNotNull(valueBuilderMethod);

    }

    @Override
    @SuppressWarnings({"unchecked"})
    public T construct() {
        try {
            Object builder = builderFactoryMethod.construct();
            classPropertySetter.setProperties(builder);
            valueBuilderMethod.setAccessible(true);
            return ((T) valueBuilderMethod.invoke(builder, (Object[]) null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Supplier<?>> getValueGenerators() {
        return classPropertySetter.getValueGenerators();
    }

    public ClassConstructor<T> clone(List<Supplier<?>> generatorsToUse) {
        return new ClassConstructorBuilder<T>(builderFactoryMethod, classPropertySetter.clone(generatorsToUse), valueBuilderMethod);
    }

    public static Collection<Method> getPossibleBuilders(final ClassAccessWrapper<?> classToGenerate) {
        // Step 1. Filter static methods, that return instance of the type as a result
        return Collections2.filter(classToGenerate.getMethods(), new Predicate<Method>() {
            @Override
            public boolean apply(final Method method) {
                if ((method.getModifiers() & Modifier.STATIC) == 0)
                    return false;
                // Checking that returned type has methods, that return instance of target class
                boolean builder = false;
                for (Method builderMethod : classToGenerate.wrap(method.getReturnType()).getMethods()) {
                    if (builderMethod.getDeclaringClass() != Object.class)
                        builder = builder || classToGenerate.canBeReplacedWith(builderMethod.getReturnType());
                }
                if (!builder)
                    return false;
                // Checking list of parameters
                for (Class<?> methodArgument : method.getParameterTypes())
                    if (classToGenerate.canBeReplacedWith(methodArgument) || classToGenerate.canReplace(methodArgument))
                        return false;
                return true;
            }
        });
    }

    /**
     * Tries to build {@link ClassConstructor} based on Builder class.
     *
     * @param <T>                   the type of object to construct
     * @param classToGenerate       {@link Class} to generate.
     * @param valueGeneratorFactory {@link ValueGeneratorFactory} to use.
     * @return {@link ClassConstructor} if it is possible to generate one, <code>null</code> otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> ClassConstructorBuilder<T> build(final ClassAccessWrapper<?> classToGenerate, final ValueGeneratorFactory valueGeneratorFactory) {
        // Step 1. Filter static methods, that return instance of the type as a result
        Collection<Method> possibleBuilders = getPossibleBuilders(classToGenerate);
        // Step 2. If there is no such method return null
        if (possibleBuilders.size() == 0)
            return null;
        // Step 3. Select constructor with most arguments
        ClassConstructorBuilder constructorBuilder = null;
        Method builder = null;
        for (Method candidate : possibleBuilders) {
            if (constructorBuilder == null || candidate.getParameterTypes().length > builder.getParameterTypes().length) {
                // Step 4. Selecting most factory method based
                Collection<Supplier<?>> suppliers = Arrays.asList(candidate.getParameters()).
                    stream().
                    map(valueGeneratorFactory::getByParameter).
                    collect(Collectors.toList());
                ClassConstructorFactory<T> builderMethod = new ClassConstructorFactory<T>(candidate, suppliers);
                ClassPropertySetter<T> builderPropertySetter = ((ClassPropertySetter<T>) ClassPropertySetter.constructPropertySetter(classToGenerate.wrap(candidate.getReturnType()), valueGeneratorFactory));

                Method valueBuilderMethod = null;
                for (Method constructorMethod : candidate.getReturnType().getDeclaredMethods()) {
                    if (classToGenerate.canBeReplacedWith(constructorMethod.getReturnType())) {
                        valueBuilderMethod = constructorMethod;
                    }
                }

                ClassConstructorBuilder candidateBuilder = new ClassConstructorBuilder<T>(builderMethod, builderPropertySetter, valueBuilderMethod);
                try {
                    candidateBuilder.construct();
                    constructorBuilder = candidateBuilder;
                    builder = candidate;
                } catch (Throwable throwable) {
                    // Ignore this candidate builder if you can't build value from it
                }
            }
        }
        return constructorBuilder;
    }

}