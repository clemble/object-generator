# object-generator
[![Build Status](https://api.shippable.com/projects/54b551075ab6cc1352887908/badge?branchName=master)](https://app.shippable.com/projects/54b551075ab6cc1352887908/builds/latest)
[![Dependency Status](https://www.versioneye.com/user/projects/54b931a78d5508da6b000061/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54b931a78d5508da6b000061)
***
**Module name** 

Object generator
***
**Description** 

Used to generate objects of required to you type with random value.
***
**Methods Summary**

* public static <T> T generate(Class<T> classToGenerate) - generates random T value using classToGenerate

* public static <T> List<T> generateList(Class<T> classToGenerate) - generate list of T values with capacity of 2

* public static <T> List<T> generateList(Class<T> classToGenerate, int num) - generate list of T values with capacity of num

* public static <T> ValueGenerator<T> getValueGenerator(Class<T> classToGenerate) - get ValueGenerator for classToGenerate by using Default value generator.

* public static <T, V> void register(final Class<T> searchClass, final String name, final ValueGenerator<V> valueGenerator)

* public static <T> void register(final Class<T> klass, final ValueGenerator<T> valueGenerator) 

* public static <T> Iterable<T> getPossibleValues(final Class<T> targetClass)

* public static void enableCaching() -enable Caching of object generator standart ValueGenerator

* public static void disableCaching() - dicable Caching of object generator standartValueGenerator

***
**Example**:

T oneObject = ObjectGenerator.generate(classToGenerate);

**Implementation features**:
* You can use cached or non-cached values
* Can provide standart random values or sequential random values if it nessesery for your goals

