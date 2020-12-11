package com.agp.demo.flink;

/**
 * maven插件(3) - scala插件scala-maven-plugin
 * 这个插件是用来让maven能够编译、测试、运行scala项目的，使用文档参考
 * <plugins>
 * <plugin>
 *                 <groupId>org.scala-tools</groupId>
 *                 <artifactId>maven-scala-plugin</artifactId>
 *                 <version>2.15.2</version>
 *                 <executions>
 *                     <execution>
 *                         <id>scala-compile</id>
 *                         <goals>
 *                             <goal>compile</goal>
 *                         </goals>
 *                         <configuration>
 *                           <!--includes是一个数组，包含要编译的code-->
 *                             <includes>
 *                                 <include>**/ /*.scala</include>
        *</includes>
        *</configuration>
        *</execution>
        *<execution>
 *<id>scala-test-compile</id>
        *<goals>
 *<goal>testCompile</goal>
        *</goals>
        *</execution>
        *</executions>
        *</plugin>
        *</plugins>
 */

/** 比较常用的两个goal：

         compile 编译scala code
         testCompile 编译test code
 运行命令行：
 mvn clean scala:compile  compile

 */
public class 插件 {
}
