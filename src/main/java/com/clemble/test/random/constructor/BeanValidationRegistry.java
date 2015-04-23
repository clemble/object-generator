package com.clemble.test.random.constructor;

import java.util.concurrent.Callable;

public class BeanValidationRegistry {

	public ClassPropertySetter<?> validate(ClassPropertySetter<?> propertySetter) {
		return null;
	}

    public <T> Callable<T> get(String field, Class<?> klass, Callable<T> original) {
        return original;
    }
}
