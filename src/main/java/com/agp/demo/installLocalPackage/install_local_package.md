# 本地jar包安装
pom项目缺少依赖，单是是历史项目，可能找不到了。
从同事本地maven仓库copyjar包。
根据pom里面文件打包
## 示例
***
        <dependency>
            <groupId>com.onecard</groupId>
            <artifactId>onecard-test</artifactId>
            <scope>test</scope>
            <version>1.0.15</version>
        </dependency>
                <dependency>
                    <groupId>com.onecard</groupId>
                    <artifactId>onecard-user-api-client-feign</artifactId>
                    <version>1.0.0</version>
                </dependency>
             
***
## 步骤：
- mvn install:install-file -Dfile=jar包的位置 -DgroupId=上面的groupId -DartifactId=上面的artifactId -Dversion=上面的version -Dpackaging=jar 
- mvn install:install-file -Dfile=C:\Users\Administrator\Desktop\onecard-test-1.0.15.jar -DgroupId=com.onecard -Dversion=1.0.15 -DartifactId=onecard-test -Dpackaging=jar
- mvn install:install-file -Dfile=C:\Users\Administrator\Desktop\onecard-user-api-client-feign-1.0.0.jar -DgroupId=com.onecard -DartifactId=onecard-user-api-client-feign -Dversion=1.0.0 -Dpackaging=jar