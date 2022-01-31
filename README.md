# Mirai-Plugin-HRobot

![](https://github.com/ShrBox/ACGPro/workflows/Java%20CI%20with%20Gradle/badge.svg)  ![](https://img.shields.io/badge/Author-Happysnaker-green.svg)  ![](https://img.shields.io/badge/Name-HRobot-green.svg)

mirai 群聊机器人插件，支持简单对话聊天，支持发送随机涩图(可指定tag)、美图、风景图等，支持随机鸡汤发送，支持坎公骑冠剑会战统计、前线报道、会战报表等功能，支持在线点歌，综合多个音乐平台发送最佳音乐(以卡片形式)，更多功能正在开发......

## 快速使用

建议您使用 mirai-console-loader 快速启动 mirai 机器人，下载地址：[Releases · iTXTech/mirai-console-loader (github.com)](https://github.com/iTXTech/mirai-console-loader/releases)，下载后解压文件。

HRobot 目前已适配至 mcl-1.2.2 版本。

然后前往[Releases · happysnaker/mirai-plugin-HRobot (github.com)](https://github.com/happysnaker/mirai-plugin-HRobot/releases)选择对应版本下载插件 jar 包 plugin-1.0-SNAPSHOT.mirai.jar，将该 jar 包移动至 mcl 文件下的 plugins 文件夹下，然后无须配置任何文件，点击 mcl.cmd 即可启动机器人。

> Linux 下需要在当前文件下输入 mcl start 命令启动，注意必须要求具备 JVM 环境。

启动后在命令行中键入：`login QQ账号 QQ密码` 即可登录机器人，更多信息请移步[mamoe/mirai: 高效率 QQ 机器人支持库 (github.com)](https://github.com/mamoe/mirai) 和  [iTXTech/mirai-console-loader: 模块化、轻量级且支持完全自定义的 mirai 加载器。 (github.com)](https://github.com/iTXTech/mirai-console-loader)

> 在登录过程中，通常您需要将 mcl 提供给你的链接发送至手机QQ进行验证，对于某些需要滑动验证码的 QQ 而言，还需要在手机上下载对于 apk，apk 相关信息可参看 [mzdluo123/TxCaptchaHelper: 腾讯滑动验证码助手 (github.com)](https://github.com/mzdluo123/TxCaptchaHelper)

如果您需要砍公骑冠剑相关功能，那么您必须配置相关信息，配置文件请移步[配置文件手册](https://github.com/happysnaker/mirai-plugin-HRobot/blob/master/CONFIG.md)

## 示例

| 特殊关键字 | 示例                   | 功能                                                     |
| ---------- | :--------------------- | -------------------------------------------------------- |
| 帮助       | @机器人 帮助           | 显示主菜单                                               |
| help       | @机器人 help           | 显示主菜单                                               |
| 天气       | @机器人 天气深圳       | 查看深圳的天气                                           |
| 翻译       | @机器人 翻译i love you | 中英翻译                                                 |
| 笑话       | @机器人 笑话           | 笑话                                                     |
| 歌词       | @机器人 歌词后来       | 查看歌词                                                 |
| 成语       | @机器人 成语暗度陈仓   | 查看成语的释义                                           |
| 前线报道   | 前线报道               | 查看工会战前线Boss信息                                   |
| 会战报表   | 会战报表               | 查看当日出刀情况                                         |
| 会战统计   | 会战统计               | 统计本次会战所有参与玩家数据，包括对任一 Boss 的数据统计 |
| 鸡汤       | 鸡汤                   | 发送随机鸡汤                                             |
| 美图       | 美图                   | 发送随机二刺螈美图                                       |
| 风景图     | 风景图                 | 发送随机风景图                                           |
| 神秘代码   | 神秘代码萝莉 白丝      | 发送涩图，可指定 tag                                     |
| 音乐       | 音乐克罗地亚狂想曲     | 从酷我、QQ、网易云聚合搜索音乐，并发送卡片               |

## 声明

我不是生产者，我只是 API 的搬运工，由于 API 随时可能停用，故此插件可能随时出问题，请您持续关注此项目，一般情况下 API 失效都会第一时间解决。

## 配置

HRobot 支持配置文件，配置文件在 mcl/config/com.happysnaker.HRobot/config.json 中，您可以手动创建配置文件，不过建议您运行一次 mcl.cmd，这会自动生产配置文件，配置文件相关信息可参考：[配置手册](https://github.com/happysnaker/mirai-plugin-HRobot/blob/master/CONFIG.md)

## 贡献

提出你的创意，或者再此源代码上进行二次开发，开发相关手册可参考：[开发手册](https://github.com/happysnaker/mirai-plugin-HRobot/blob/master/CONFIG.md)


