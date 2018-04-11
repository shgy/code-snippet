对于大型项目, 有必要根据功能整个项目拆分成多模块. 以实现:
1. 清晰开发职责, 实现代码的复用.
2. 项目组多人协同开发, 效率更快.

比如: 通常,一个项目会分为:
1. commons 公共的类库, 比如工具类, 消息邮件提醒等功能.
2. api 对外的接口
3. service 服务层
4. web web层
这些模块.

参考:
https://www.cnblogs.com/blueness-sunshine/p/6015965.html