package com.stresstest.random.factory;

import org.junit.Assert;
import org.junit.Test;

import com.clemble.test.random.generator.RandomValueGenerator;

public class RandomValueGeneratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testRandomStringValueGeneratorException() {
        RandomValueGenerator.randomString(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRandomAlphabeticStringValueGeneratorException() {
        RandomValueGenerator.randomAlphabeticString(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRandomAsciiStringValueGeneratorException() {
        RandomValueGenerator.randomAsciiString(-2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRandomAlphanumericStringValueGeneratorException() {
        RandomValueGenerator.randomAlphanumericString(-3);
    }
    
    @Test
    public void testRandomStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerator.randomString(20).call();
        Assert.assertEquals(generatedValue.length(), 20);
    }
    
    @Test
    public void testRandomAlphabeticStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerator.randomAlphabeticString(30).call();
        Assert.assertEquals(generatedValue.length(), 30);
    }
    
    @Test
    public void testRandomAsciiStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerator.randomAsciiString(40).call();
        Assert.assertEquals(generatedValue.length(), 40);
    }
    
    @Test
    public void testRandomAlphanumericStringValueGenerator() throws Exception {
        String generatedValue = RandomValueGenerator.randomAlphanumericString(50).call();
        Assert.assertEquals(generatedValue.length(), 50);
    }
}
