# tool-apidoc

`tool-apidoc` 工具使用java8编写，他会收集Java代码中的方法信息，并生成API信息，目前针对

- Spring5 + Swagger

进行支持。

## Change History

- 1.0-SNAPSHOT
- 1.1-SNAPSHOT，增加对getter方法的支持，1.0版本中，只会读取类的字段
- 1.3-SNAPSHOT，修复没有请求体、响应体的输出报错问题
- 1.4-SNAPSHOT，修复字段查询不到的问题
- 1.5-SNAPSHOT，修复父类字段查询不到的问题
- 1.6-SNAPSHOT
  - 字段类型number改用具体的java数据类型
  - 返回体中的【必填】改成【空指针描述】，说明对象属性是否必需返回
