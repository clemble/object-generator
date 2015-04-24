package com.stresstest.random.factory;

import org.junit.Assert;
import org.junit.Test;

import com.clemble.test.random.generator.RandomValueGenerators;

public class RandomValueGeneratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testRandomStringValueGeneratorException() {
        RandomValueGenerators.randomString(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRandomAlphabeticStringValueGeneratorException() {
        RandomValueGenerators.randomAlphabeticString(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRandomAsciiStringValueGeneratorException() {
        RandomValueGenerators.randomAsciiString(-2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRandomAlphanumericStringValueGeneratorException() {
        RandomValueGenerators.randomAlphanumericString(-3);
    }
    
    @Test
    public void testRandomStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerators.randomString(20).get();
        Assert.assertEquals(generatedValue.length(), 20);
    }
    
    @Test
    public void testRandomAlphabeticStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerators.randomAlphabeticString(30).get();
        Assert.assertEquals(generatedValue.length(), 30);
    }
    
    @Test
    public void testRandomAsciiStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerators.randomAsciiString(40).get();
        Assert.assertEquals(generatedValue.length(), 40);
    }
    
    @Test
    public void testRandomAlphanumericStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerators.randomAlphanumericString(50).get();
        Assert.assertEquals(generatedValue.length(), 50);
    }
}
