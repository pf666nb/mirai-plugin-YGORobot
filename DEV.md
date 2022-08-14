# 开发手册
你可以阅读 [mirai 开发手册](https://github.com/mamoe/mirai/blob/dev/docs/README.md) 开发属于你自己的机器人，或参与本项目建设，在本项目基础上进行二次开发，提出 MR，共同建设 HRobot。

接下来会介绍如何在本项目上进行二次开发。

## 环境准备
必备的材料：
- [IntelliJ IDEA：JetBrains 功能强大、符合人体工程学的 Java IDE](https://www.jetbrains.com.cn/idea/promo/?utm_source=baidu&utm_medium=cpc&utm_campaign=cn-bai-br-intellij-ph-pc&utm_content=intellij-core&utm_term=idea&bd_vid=12388101124270214987)
- jdk11 或以上版本

搭建步骤：
1. fork 本项目，随后在 IDEA 中使用 GET FROM VCS 使用 utl 从 fork 下来的仓库导入项目，导入完成后等待 IDEA 加载并下载相关模块完成，并跟随 IDEA 指示安装  Kotlin 插件
2. 在 src 目录下创建 `test/kotlin/RunTerminal.kt` 文件，文件内容为：
  ```java
  package com.happysnaker

import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import java.io.File

fun setupWorkingDir() {
    // see: net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal
    System.setProperty("user.dir", File("debug-sandbox").absolutePath)
}

suspend fun main() {
    setupWorkingDir()

    MiraiConsoleTerminalLoader.startAsDaemon()

    val pluginInstance = Main.INSTANCE

    pluginInstance.load() // 主动加载插件, Console 会调用 Main.onLoad
    pluginInstance.enable() // 主动启用插件, Console 会调用 Main.onEnable
    val bot = MiraiConsole.addBot(123456, "密码").alsoLogin() // 登录一个测试环境的 Bot
    MiraiConsole.job.join()
}
```
目录结构：

![image](https://user-images.githubusercontent.com/73147033/184528536-187d6715-2d35-44e2-aaaf-f579b5d0feac.png)

3. 刷新 build.gradle.kts 依赖的模块，Gradle 是一个类似于 maven 的依赖管理工具，通常在 IDEA 右侧工具栏中会有相关工具，当万事具备时，您可以尝试在 `RunTerminal.kt` 的 main 方法中登录 QQ 账号，并尝试运行 main 方法，第一次运行会生成 `debug-sandbox` 配置目录，第二次运行将会登录您的 QQ，如果无误，您的 QQ 机器人已经成功上线了！登录相关可参考：[登录步骤](https://github.com/happysnaker/mirai-plugin-HRobot/blob/master/STEP.md)


## 开发
HRobot 是一个群聊机器人，因此只订阅了群聊消息事件，我们会教你如何快速开发群聊机器人，项目封装了一些必要的 API 调用，你也可以参考 [mirai 开发手册](https://github.com/mamoe/mirai/blob/dev/docs/README.md)。

### 消息事件处理器
MessageEventHandler 是处理群聊消息的核心逻辑，HRobot 所用 handler 都被定义在 `com.happysnaker.handler` 包下，MessageEventHandler 有两个接口待实现：
```java
public interface MessageEventHandler {
    /**
     * 处理一个新的消息事件，返回要回复的消息，可以是多条，也可以为 null（代表不回复）
     *
     * @param event 消息事件
     * @param ctx 在 handlers 之间传递的上下文
     */
    List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx);


    /**
     * 是否应该处理事件，子类应该扩展它
     *
     * @param event 消息事件
     * @param ctx 在 handlers 之间传递的上下文
     * @return 如果需要处理，则返回 true；如果不需要处理，则返回 false
     */
    boolean shouldHandle(MessageEvent event, Context ctx);
}
```
`shouldHandle` 方法表示此 handler 是否对此消息感兴趣，如果是的话，`handleMessageEvent` 将会被调用并返回消息列表，HRobot 会自动处理将消息列表回复到相关群中，逻辑相当简单，我们可以来实现一个简单的图片分享处理类，一旦用户发送关键字 "图片"，此处理者就会自动分享一张图片：
```java
@handler(priority = 1)
public class CaseMessageEventHandler extends GroupMessageEventHandler {
    @Override
    public List<MessageChain> handleMessageEvent(MessageEvent event, Context ctx) {
        try {
            // 向腾讯服务器上传一张 bing 美图， uploadImage 是 HRobot 封装的 api
            Image image = uploadImage(event, new URL(BingApi.getRandomImageUrl()));

            // 构造消息链，消息链可以是图片、语音、文本字符串等等...可以使用 buildMessageChain 方法将他们组合为一条消息
            MessageChain chain1 = buildMessageChain("图片分享：\n", image);
            MessageChain chain2 = buildMessageChain("图片链接：" + Image.queryUrl(image));
            return buildMessageChainAsList(chain1, chain2);
        } catch (FileUploadException | MalformedURLException e) {
            // 记录错误日志到文件中，便于后续检查
            recordFailLog(event, "发送图片错误");
            return buildMessageChainAsList("异常的事情发生了....");
        }
    }

    @Override
    public boolean shouldHandle(MessageEvent event, Context ctx) {
        // 获取消息中的文字信息
        String content = getPlantContent(event);
        // 只有消息是 "图片" 关键字时此处理器才感兴趣
        return content.equals("图片");
    }
}
```
至此，一个消息处理器就完成了，可以登录机器人进行验证，效果图如下：
![c911c55d0a604dd0442f227c5b8b651](https://user-images.githubusercontent.com/73147033/184530084-27948d1a-e440-4c97-a05f-b2c36f0ad202.jpg)

这个 case 类继承了 `GroupMessageEventHandler` 类，可以看下整体的架构：
![image](https://user-images.githubusercontent.com/73147033/184530226-135130e1-40d6-4e28-acc5-3f97df864566.png)

父类集成了一些通用的功能，你可以阅读代码了解，RobotUtil 封装了许多有用的 API 供开发者调用，例如上述代码 getPlantContent、uploadImage、buildMessageChain、buildMessageChainAsList 均是 RobotUtil 中封装的 API，更多信息可以阅读代码，所有方法都有注释。

不知道你是否注意到了 `@handler(priority = 1)` 这一行代码，**@handler 标识着此类成为一个真正的消息处理器**，被 @handler 标注的类才会正式被 HRobot 扫描到并添加至 handler 列表中，priority 表示优先级，当有多个 handler 时，优先级越高则会被优先访问，一旦某个 handler 的 `shoudHandle` 方法返回 true，那么后面的 handler 将不在会被调用，除非你显示的执行 ` ctx.continueExecute();` 表示希望能够继续运行下去。

context 可以在多个 handler 之间传递信息，也可以在 `shoudHandle` 方法和 `handleMessageEvent` 方法传递信息，某些时候会很有用。

总结一下，想要编写你自己的群机器人只需要两步：
1. 实现 MessageEventHandler 接口中的两个方法，当然最好选择继承 GroupMessageEventHandler 类
2. 使用 @handler 注解标识

一个群聊机器人就诞生了！


### 拦截器
在 HRobot 中，敏感词检测、回复替代、群聊开关 都是通过拦截器实现的，HRobot 中的拦截器在 com.happysnaker.intercept 包下，想要实现一个拦截器也非常简单，和上面一样，只需要两步：
1. 实现 `Interceptor` 接口
2. 使用 `intercept` 注解标识

`Interceptor` 接口中有两个方法，分别为前置拦截和后置拦截，前置拦截器在消息到达 handler 之前进行拦截，如果返回 true 则消息被拦截，无法到达 handler；后置拦截器对 handler 返回的消息进行处理，例如可以进行关键词替换、屏蔽之类的事情：
```java
public interface Interceptor {
    /**
     * 在事件到达 handler 之前拦截事件，如果返回真则将该事件拦截
     * @param event
     * @return 返回真拦截，返回假通过
     */
    boolean interceptBefore(MessageEvent event);


    /**
     * 在 handler 返回消息之后拦截事件及消息，在这里可以对消息进行一些处理，或者选择返回 null 以过滤消息
     * @param event 事件
     * @param mc 由 handler 返回的回复消息
     * @return 返沪经过处理后的消息，或者返回 null 以过滤此回复
     */
    List<MessageChain> interceptAfter(MessageEvent event, List<MessageChain> mc);
}
```
你可以参考包下的相关拦截器进行开发。

### 命令处理器
HRobot 没有使用传统的命令行方式，这太不方便了，HRobot 采用群聊对话的方式检测命令并执行，管理员可以专门创建一个群用于测试和执行命令，一个命令需要以特定前缀开头，由于命令比较危险，每个命令都需要被记录，因此 HRobot 中，每个命令都会被暂时的记录下来，超级管理员可以看到谁执行了什么命令，除非管理员将其刷新，当命令执行失败时，HRobot 会自动记录下错误日志。

开发者无需关注复杂的逻辑，简单来看命令处理器也是一种消息处理器，想要实现一个命令处理器仅需三步：
1. 继承 `DefaultCommandMessageEventHandlerManager` 类，实现 `parseCommand` 方法
2. 在构造器中调用父类的 `registerKeywords` 方法注册关键字
3. 使用 @handler 标注类，通常我们约定命令的优先级很高，优先级设置为一个比较大的数

例如，我们实现一个开启、关闭机器人操作，关键词为关闭机器人、开启机器人，需要超级管理员才能执行：
```java
@handler(priority = 1024)
public class CaseCommandMessageEventHandler extends DefaultCommandMessageEventHandlerManager {

    public CaseCommandMessageEventHandler() {
        // 注册关键字
        super.registerKeywords("关闭机器人");
        super.registerKeywords("开启机器人");
    }

    @Override
    public List<MessageChain> parseCommand(MessageEvent event) throws CanNotParseCommandException, InsufficientPermissionsException {
        // 权限判断
        if (!Permission.hasSuperAdmin(getSenderId(event))) {
            return buildMessageChainAsList("权限不足哦，宝贝~");
        }
        
        String content = getPlantContent(event);
        // do something...
        if (content.equals("关闭机器人")) {
            RobotConfig.enableRobot = false;
        } else {
            RobotConfig.enableRobot = true;
        }
        return buildMessageChainAsList("您输入的命令是：", content);
    }
}
```

通过类图可以帮助你更好的理解相关概念，命令处理器不过也只是个消息处理器罢了：
![image](https://user-images.githubusercontent.com/73147033/184531974-388bd327-5a15-472d-af63-f66ebe369189.png)

### 贡献
最后，开发完之后别忘了提出 PullRequest 共同建设项目，非常感谢您的参与！
