<!--[**🌎English Documentation**](README-EN.md)-->

-------------------------------------------------------------------------------

## 📚简介
Data-Converter是一个支持多种数据格式协议的数据转换组件


-------------------------------------------------------------------------------

## 🛠️支持协议及相关模块


|模块|协议|
|---|---|
|data-converter-fastjson|JSON|
|data-converter-gson|JSON|
|data-converter-jackson-json|JSON|
|data-converter-jackson-xml|Xml|
|data-converter-avro|Avro|
|data-converter-binary|default-binary|
|data-converter-hessian|Hessian|
|data-converter-kryo|Kryo|
|data-converter-protobuf|Protobuf|
|data-converter-protostuff|Protostuff|
|data-converter-text|Text|


可以根据需求对每个模块单独引入，也可以通过引入`data-converter-all`方式引入所有模块。

-------------------------------------------------------------------------------

-------------------------------------------------------------------------------

## 📦安装

### 🍊Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>com.chm.converter</groupId>
    <artifactId>data-converter-all</artifactId>
    <version>Version</version>
</dependency>
```

### 🍐Gradle
```
implementation 'com.chm.converter:data-converter-all:Version'
```

### 📥下载jar

点击以下链接，下载`data-converter-all-X.X.X.jar`即可：

- [Maven中央库](https://repo1.maven.org/maven2/cn/hutool/hutool-all/5.7.13/)


### 🚽编译安装

访问Data-converter的Gitee主页：[Gitee](https://gitee.com/CHMing7/data-converter) 下载整个项目源码（master分支）然后进入Hutool项目目录执行：

```sh
gradle install
```

然后就可以使用Maven引入了。

-------------------------------------------------------------------------------

## 🏗️添砖加瓦

### 🐞提供bug反馈或建议

提交问题反馈请说明正在使用的JDK版本呢、Data-Converter版本和相关依赖库版本。

- [Gitee issue](https://gitee.com/CHMing7/data-converter/issues)


### 🧬贡献代码的步骤
-----------------------------------

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

Copyright (c) 2021 CHMing