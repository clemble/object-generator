package com.clemble.test.random;

import java.util.concurrent.Callable;

abstract public class AbstractValueGenerator<T> implements Callable<T>, Cloneable {
	
	public int scope() {
		return 1;
	}

	@SuppressWarnings("unchecked")
	public Callable<T> clone() {
		try {
			return (Callable<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
