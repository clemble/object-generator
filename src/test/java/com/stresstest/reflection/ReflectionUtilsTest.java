package com.stresstest.reflection;

import com.clemble.test.reflection.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.Min;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavarazy on 4/23/15.
 */
public class ReflectionUtilsTest {

    public static class AnnotatedClass {

        final private String methodAnnotated;
        @Min(1)
        final private String fieldAnnotated;

        public AnnotatedClass(
            String methodAnnotated,
            String fieldAnnotated,
            @Min(1) String constructorAnnotated) {
            this.methodAnnotated = methodAnnotated;
            this.fieldAnnotated = fieldAnnotated;
        }

        @Min(1)
        public String getMethodAnnotated() {
            return methodAnnotated;
        }

        public String getFieldAnnotated() {
            return fieldAnnotated;
        }
    }

    @Test
    public void testMethodExtraction() {
        List<Annotation> methodAnnotations = ReflectionUtils.findAllAnnotations("methodAnnotated", AnnotatedClass.class).collect(Collectors.toList());
        Assert.assertEquals(methodAnnotations.size(), 1);
    }

    @Test
    public void testFieldExtraction() {
        List<Annotation> fieldAnnotations = ReflectionUtils.findAllAnnotations("fieldAnnotated", AnnotatedClass.class).collect(Collectors.toList());
        Assert.assertEquals(fieldAnnotations.size(), 1);
    }

}
