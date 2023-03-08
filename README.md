<!--[**🌎English Documentation**](README-EN.md)-->

-------------------------------------------------------------------------------

## 📚简介

Data-Converter是一个支持多种数据格式协议的数据转换组件

-------------------------------------------------------------------------------

## 🛠️支持协议及相关模块

|               模块               |     协议     |                                                                               引用模块                                                                               |
|:------------------------------:|:----------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|    data-converter-fastjson     |    JSON    |                                                                   com.alibaba:fastjson:version                                                                   |
| data-converter-fastjson2-json  |    JSON    |                                                             com.alibaba.fastjson2:fastjson2:version                                                              |
| data-converter-fastjson2-jsonb |   JSONB    |                                                             com.alibaba.fastjson2:fastjson2:version                                                              |
|      data-converter-gson       |    JSON    |                                                                com.google.code.gson:gson:version                                                                 |
|  data-converter-jackson-json   |    JSON    | com.fasterxml.jackson.core:jackson-core:version<br>com.fasterxml.jackson.core:jackson-databind:version<br>com.fasterxml.jackson.core:jackson-annotations:version |
|   data-converter-jackson-xml   |    Xml     |                                                 com.fasterxml.jackson.dataformat:jackson-dataformat-xml:version                                                  |
|      data-converter-avro       |    Avro    |                                                                   org.apache.avro:avro:version                                                                   |
|     data-converter-hessian     |  Hessian   |                                                                    com.caucho:hessian:version                                                                    |
|      data-converter-kryo       |    Kryo    |                                                                com.esotericsoftware:kryo:version                                                                 |
|    data-converter-protobuf     |  Protobuf  |                                        com.google.protobuf:protobuf-java:version<br>io.protostuff:protostuff-core:version                                        |
|   data-converter-protostuff    | Protostuff |                                                              io.protostuff:protostuff-core:version                                                               |
|       data-converter-fst       |    Fst     |                                                                  de.ruedigermoeller:fst:version                                                                  |
| data-converter-jackson-msgpack |  Msgpack   |                                                          org.msgpack:jackson-dataformat-msgpack:version                                                          |
|     data-converter-spearal     |  Spearal   |                                                                 org.spearal:spearal-java:version                                                                 |
|     data-converter-thrift      |   Thrift   |                                                                 org.apache.thrift:thrift:version                                                                 |
|  data-converter-jackson-yaml   |    Yaml    |                                                 com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:version                                                 |
|  data-converter-jackson-cbor   |    Cbor    |                                                 com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:version                                                 |
|   data-converter-jackson-ion   |    Ion     |                                                 com.fasterxml.jackson.dataformat:jackson-dataformat-ion:version                                                  |
|  data-converter-jackson-smile  |   Smile    |                                                com.fasterxml.jackson.dataformat:jackson-dataformat-smile:version                                                 |

可以根据需求对每个模块单独引入，也可以通过引入`data-converter-all`方式引入所有模块。

-------------------------------------------------------------------------------

## 📦安装

### 🍊Maven

在项目的pom.xml的dependencies中引入all模块:

```xml

<dependency>
    <groupId>io.gitee.chming7</groupId>
    <artifactId>data-converter-all</artifactId>
    <version>Version</version>
</dependency>
```

或

**使用bom模块的方式引入**

**import方式**
<br>
<br>
在父模块中加入

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.gitee.chming7</groupId>
            <artifactId>data-converter-bom</artifactId>
            <version>Version</version>
            <type>pom</type>
            <!-- 注意这里是import -->
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

在子模块中引入自己需要的模块

```xml

<dependencies>
    <dependency>
        <groupId>io.gitee.chming7</groupId>
        <artifactId>data-converter-gson</artifactId>
    </dependency>
</dependencies>
```

**exclude方式**
<br>
<br>
如果你引入的模块比较多，但是某几个模块没用，你可以

```xml

<dependencies>
    <dependency>
        <groupId>io.gitee.chming7</groupId>
        <artifactId>data-converter-bom</artifactId>
        <version>Version</version>
        <!-- 加不加这句都能跑，区别只有是否告警  -->
        <type>pom</type>
        <exclusions>
            <exclusion>
                <groupId>io.gitee.chming7</groupId>
                <artifactId>data-converter-avro</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

### 🍐Gradle

**方式一：引入所有模块**
```
implementation ("io.gitee.chming7:data-converter-all:Version")
```

**方式二：引入自己需要的模块**
```
implementation enforcedPlatform('io.gitee.chming7:data-converter-bom:Version')
implementation ("io.gitee.chming7:data-converter-gson")
```

**方式三：排除不需要的模块**
```
implementation enforcedPlatform('io.gitee.chming7:data-converter-bom:Version')
implementation ("io.gitee.chming7:data-converter-bom"){
    exclude module: "data-converter-json"
}
```

### 📥下载jar

点击以下链接，搜索下载`data-converter-xxx-X.X.X.jar`即可：

- [Maven中央库](https://mvnrepository.com/search?q=io.gitee.chming7)

### 🚽编译安装

访问Data-converter的Gitee主页：[Gitee](https://gitee.com/CHMing7/data-converter)
下载整个项目源码（master分支）然后进入Data-Converter项目目录执行：

```sh
gradle install
```

然后就可以使用Maven引入了。

-------------------------------------------------------------------------------

## 📝简单示例

```java
JsonConverter converter=ConverterSelector.select(JsonConverter.class);
// 或者
        JsonConverter converter=JsonConverter.select();
// 序列化
        String encode=converter.encode(user);
// 反序列化
        User newUser=converter.convertToJavaObject(encode,User.class);
```

-------------------------------------------------------------------------------

## 🏗️添砖加瓦

### 🐞提供bug反馈或建议

提交问题反馈请说明正在使用的JDK版本呢、Data-Converter版本和相关依赖库版本。

- [Gitee issue](https://gitee.com/CHMing7/data-converter/issues)

### ⛽贡献代码的步骤
--------------------------------------------------------------------------------

1. 提issue，如果在gitee的issue中已经有您想解决的问题，可以直接将该issue分配给您自己。如若没有，可以自己在gitee上创建一个issue。
2. Fork 本项目的仓库
3. 新建分支，如果是加新特性，分支名格式为`feat_${issue的ID号}`，如果是修改bug，则命名为`fix_${issue的ID号}`。
4. 本地自测，提交前请通过所有的已经单元测试，以及为您要解决的问题新增单元测试。
5. 提交代码
6. 新建 Pull Request
7. 我会对您的PR进行验证和测试，如通过测试，我会合到`dev`分支上随新版本发布时再合到`master`分支上。

-------------------------------------------------------------------------------

项目协议
--------------------------
The MIT License (MIT)

Copyright (c) 2022 CHMing