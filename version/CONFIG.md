# 配置文件

- [声明](#声明)
- [模板](#模板)
- [订阅配置中的 key](#订阅配置中的-key)
  - [bilibili](#bilibili)
- [内部标签](#内部标签)
  - [使用 img 标签上传本地或网络图片](#使用-img-标签上传本地或网络图片)
  - [使用 at 标签艾特发送人或其他成员](#使用-at-标签艾特发送人或其他成员)
  - [使用 quote 标签引用事件触发消息源](#使用-quote-标签引用事件触发消息源)
- [配置敏感词库](#配置敏感词库)
- [获取坎公 Cookie](#获取坎公-cookie)

## 声明
- v3.0 之后正式使用 yaml 作为配置文件，早期版本 json 配置请移步: [历史版本配置](V2&V1_CONFIG.md)，对于正在使用 json 配置文件的用户可以前往 [JSON转YAML,YAML转JSON - 在线工具 - OKTools ](https://oktools.net/json2yaml)将 JSON 转为 yaml 文件进行迁移。
- 在 `mcl/data/com.happysnaker.HRobot` 目录下存在一个 `error.log`，这里会记录机器人运行时发生的错误。

## 模板

我们在配置文件给出了每一项配置的详细说明，你可以移步配置文件模板查看对应功能，在首次运行 mcl 后，HRobot 会自动生成配置文件模板。

如果配置文件未能给出详细说明，你可以查看目录看看是否有你想要的答案，如果仍有疑问请提出 ISSUE。

- [v3.4 配置文件模板](v3.4_config.md)
- [v3.3 配置文件模板](v3.3_config.md)
- [v3.2 配置文件模板](v3.2_config.md)
- [v3.1 配置文件模板](v3.1_config.md)
- [v3.0 配置文件模板](v3.0_config.md)
- [更早版本配置文件模板](V2&V1_CONFIG.md)

## 订阅配置中的 key

### bilibili

对于小破站而言，目前支持对 up 主动态订阅以及对番剧更新订阅，由不同的 type 驱动。

1. UP 主唯一 Key
点进 UP 主个人空间，如图所示，这一串数字便是 UP 主唯一标识:

![image-20221004130621037](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210041306793.png)

2. 番剧唯一 Key

   点进番剧页面，URL 地址中 ss 后面一串数字即是唯一 Key，例如下图中的唯一 Key 是 `42994`：
   
   ![image-20221004132010638](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210041320514.png)

## 内部标签
HRobot 的内部标签，可在 **机器人回复替代、定时消息发送任务、自定义回复**  等情况下使用，可以使用内部标签上传图片、引用消息、艾特成员。

语法：`[hrobot::$tag](val)`，tag 标识标签类型，val 代表值。
> HRobot 同样支持引用 mirai 码，但 HRobot 内部标签具有一些动态语义。
### 使用 img 标签上传本地或网络图片
语法 `[hrobot::$img](val)`，如果 val 是以 `http` 开头，则会被解释为一个网络链接，否则会被视为本地路径。

示例：
```yaml
[hrobot::$img](https://xxxx.png)    # 网络图片
[hrobot::$img](/app/test.png)       # 本地图片
```  
请注意，解析错误、文件不存在、网络超时等原因可能会导致解析失败，进而导致消息发送失败，可以在 `/data/com.happysnaker.HRobot/error.log` 中查看日志。

### 使用 at 标签艾特发送人或其他成员
语法 `[hrobot::$at](val)`，val 可以是群成员 QQ，当 HRobot 被动回复时，val 可以设置为 `sender` 表示艾特发送人。

例如在关键字匹配、回复语句替代中，可以使用 `[hrobot::$at](sender)` 来 at 事件触发人，这是 `mirai` 码所不具有的功能。

示例：
```yaml
[hrobot::$at](sender)        # at 发送人
[hrobot::$at](1586145)       # at QQ = 1586145 群成员
```  
请注意，艾特成员不存在、网络超时等原因可能会导致解析失败，进而导致消息发送失败，可以在 `/data/com.happysnaker.HRobot/error.log` 中查看日志。

### 使用 quote 标签引用事件触发消息源
语法 `[hrobot::$quote](val)`，val 的值可以任意填写，`quote` 标签只有一个语义，那就是引用回复事件触发的消息源。

例如在关键字匹配、回复语句替代中，可以使用 `[hrobot::$quote](sender)` 来引用一条触发事件的消息源，这是 `mirai` 码所不具有的功能。

示例：
```yaml
[hrobot::$quote](sender)        # 引用触发事件的消息源
```  
请注意，消息事件不存在可能会导致解析失败，进而导致消息发送失败，可以在 `/data/com.happysnaker.HRobot/error.log` 中查看日志。


## 配置敏感词库
v3.0 之后在 `mcl/config/com.happysnaker.HRobot` 目录下存在一个 sensitiveWord.txt 文件夹，这个文件作为 HRobot 的敏感词库，敏感词需换行作为分割，**默认为空文件，需要你自行配置**。  

本仓库提供了一个轻量级的敏感词库：[敏感词库](../sensitiveWord.txt) ，你可以直接复制到 mcl/config/com.happysnaker.HRobot 目录下的 sensitiveWord.txt 文件中。或者使用 shell 命令 `wget https://raw.githubusercontent.com/happysnaker/mirai-plugin-HRobot/master/sensitiveWord.txt` 下载。  

**在 v3.4 版本之后，如果敏感词库不存在，则默认会使用仓库中的轻量级敏感词库，如果您不想加载敏感词库到内存中，请不要删除这个文件，而是将文件内容置空。**

## 获取坎公 cookie
坎公相关功能需要配置 Cookie，请参考如下步骤获取：

1. 用电脑点击[坎公百宝袋-bigfun社区](https://www.bigfun.cn/tools/gt/)并使用自己账号登录。
2. 点击 f12 打开游览器开发工具，找到网络工具。
3. 保持 f12 打开，同时点击前线报道，找到名称为 feweb?target=kan-gong-guild-boss-info%2Fa 的项。
![image-20220131193950450](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220131193950450.png)

4. 再标头中找到请求标头，复制 cookie 即可。
![image-20220131194105416](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220131194105416.png)





















