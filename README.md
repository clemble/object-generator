# object-generator
[![Build Status](https://api.shippable.com/projects/54b551075ab6cc1352887908/badge?branchName=master)](https://app.shippable.com/projects/54b551075ab6cc1352887908/builds/latest)
[![Dependency Status](https://www.versioneye.com/user/projects/54b931a78d5508da6b000061/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54b931a78d5508da6b000061)
***
**Description** 

This module provide tools to generate classes with determined fields. Object generator can be used in cases where you need to create many different objects. Here is some examples when you can use it:
* Stress testing
* Create objects for your unit-testing
* Fill data base with values

Module can provide you different ways to generate objects.
* You can use cached or non-cached values(enableCaching, disableCaching)
* Can provide standart random values or sequential random values if it nessesery for your goals

***
***How to use*** 

1. Create a class wich will be used in generating object
2. use ObjectGenerator.generate to create new object

***
**Methods Summary**

| Name | Type | Arguments | Description |
--- | --- | ----- | --- |
| generate | <T> T | Class<T> classToGenerate | Generates random T value using classToGenerate. |
| generateList | <T> List<T> | Class<T> classToGenerate | Generate list of T values with capacity of 2. |
| generateList | <T> List<T> | Class<T> classToGenerate, int num | Generate list of T values with capacity of num. |
| getValueGenerator | ValueGenerator<T> | Class<T> classToGenerate | Get ValueGenerator for classToGenerate by using default value generator. |
| register | <T, V> void | final Class<T> searchClass<br /> final String name<br /> final ValueGenerator<V> valueGenerator | Register propery setter. |
| register | <T> void | final Class<T> klass, final ValueGenerator<T> valueGenerator | Add valueGenerator to default object generator value generators. | 
| getPossibleValues | <T> Iterable<T> | final Class<T> targetClass | Get possible values that can be generated by value generator for this object generator. |
| enableCaching | void | no arguments | Enable caching of object generator standart ValueGenerator. |
| disableCaching | void | no arguments | Dicable caching of object generator standartValueGenerator. |

***
**Examples**

**Basic implementation**

**Disabling and enabling caching**





