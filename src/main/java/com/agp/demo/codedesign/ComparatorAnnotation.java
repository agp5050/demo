package com.agp.demo.codedesign;

import java.util.Comparator;

/**
 * 策略模式
 */
public class ComparatorAnnotation {
    public static void main(String[] args) {
        Comparator comparator;  //比较器，策略模式。 不侵入类里面，在类外边规定了比较的方式。可以有不同的方式。策略不同
        //一般作法是方法里面传接口参数，然后由不同的实现类，去实现。实现不同的方法。策略模式。
        Comparable comparable;  //接口，对象实现comparable 实现后，就在类里面固定了。
    }
}
