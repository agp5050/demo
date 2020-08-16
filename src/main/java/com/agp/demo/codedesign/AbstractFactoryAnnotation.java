package com.agp.demo.codedesign;

import com.agp.demo.ABC;
import com.agp.demo.Person;

/**
 * 抽象工厂方法，定义一个产品簇。 比如一个AbstractFactory 生产 武器、代步工具、食品。
 * 由具体的集成抽象工厂的实体工厂。比如 modernFactory ,magicFactory。生产的
 * 产品簇完全不一样。
 */
/*
* 形容词用接口、名称用抽象类--》 如果设计时*/
public class AbstractFactoryAnnotation {
}
abstract class AFactory{
    abstract Person createPerson();
    abstract ABC createABC();
}
class ModernFactory extends AFactory{
    @Override
    Person createPerson() {
        return new Person();  //new ModernPerson;
    }

    @Override
    ABC createABC() {
        return null; //new ModernABC();
    }
}
