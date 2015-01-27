# object-generator
[![Build Status](https://api.shippable.com/projects/54b551075ab6cc1352887908/badge?branchName=master)](https://app.shippable.com/projects/54b551075ab6cc1352887908/builds/latest)
[![Dependency Status](https://www.versioneye.com/user/projects/54b931a78d5508da6b000061/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54b931a78d5508da6b000061)

Author : Anton Oparin
***
Module name: object generator

Description: used to generate objects of required to you type with random value.

Methods Summary:
< public static <T> T generate(Class<T> classToGenerate) >

< public static <T> List<T> generateList(Class<T> classToGenerate) >

< public static <T> List<T> generateList(Class<T> classToGenerate, int num) >

< public static <T> ValueGenerator<T> getValueGenerator(Class<T> classToGenerate) >

< public static <T, V> void register(final Class<T> searchClass, final String name, final ValueGenerator<V> valueGenerator) >

< public static <T> void register(final Class<T> klass, final ValueGenerator<T> valueGenerator) > 

< public static <T> Iterable<T> getPossibleValues(final Class<T> targetClass) >

< public static void enableCaching() >

< public static void disableCaching() >

//not ready yet
Example:
T oneObject = ObjectGenerator.generate(classToGenerate);

Implementation features:
1. You can use cached or non-cached values
2. Can provide standart random values or sequential random values if it nessesery for your goals

