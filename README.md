# Mirai-Plugin-HRobot

![](https://github.com/ShrBox/ACGPro/workflows/Java%20CI%20with%20Gradle/badge.svg)  ![](https://img.shields.io/badge/Author-Happysnaker-green.svg)  ![](https://img.shields.io/badge/Name-HRobot-green.svg)

mirai 群聊机器人插件，支持发送涩图(可指定tag)、美图、风景图等，支持随机鸡汤发送，支持坎公骑冠剑会战统计、前线报道、会战报表等功能，支持在线点歌，综合多个音乐平台发送最佳音乐，支持简单对话聊天，更多功能正在开发。

## 快速使用

机器人运行必须要求 Java 运行时环境版本大于等于 11

建议您使用 mirai-console-loader 快速启动 mirai 机器人，下载地址：[Releases · iTXTech/mirai-console-loader (github.com)](https://github.com/iTXTech/mirai-console-loader/releases)，下载后解压文件。

<<<<<<< HEAD
> 如果你是新手，不会安装 java 环境，
=======
> 如果你是新手，不会安装 java 环境，你可以使用安装器一键安装 java 环境 和 mcl，请移步：[mcl 一键安装](https://github.com/iTXTech/mcl-installer)
>>>>>>> bdcb53295ddd69391e995fed9e206db571c05459

经过测试，HRobot 适配 mcl-1.2.2、mcl-1.2.1 版本，更早版本暂未测试。

前往[Releases · happysnaker/mirai-plugin-HRobot (github.com)](https://github.com/happysnaker/mirai-plugin-HRobot/releases)选择对应版本下载插件 jar 包 plugin-SNAPSHOT.mirai.jar，将该 jar 包移动至 mcl 文件下的 plugins 文件夹下，然后无须配置任何文件，点击 mcl.cmd 即可启动机器人，注意如果你是第一次下载，你必须先启动一次 mcl.cmd 才会有 plugins 相关文件夹生成。

> Linux 下需要在当前文件下输入 ./mcl 命令启动.

启动后在命令行中键入：`login QQ账号 QQ密码` 即可登录机器人，登录相关步骤请参考：[登录步骤](./STEP.md)

> 更多信息请移步[mamoe/mirai: 高效率 QQ 机器人支持库 (github.com)](https://github.com/mamoe/mirai) 和  [iTXTech/mirai-console-loader: 模块化、轻量级且支持完全自定义的 mirai 加载器。 (github.com)](https://github.com/iTXTech/mirai-console-loader)

如果你需要砍公骑冠剑相关功能，那么您必须配置相关信息，配置文件请移步[配置文件手册](https://github.com/happysnaker/mirai-plugin-HRobot/blob/master/CONFIG.md)

如果你不会配置或者想先体验一下，可提出对应 Issue 或邮件联系我：happysnaker@foxmail.com，可以让机器人加上你的群。

## 关键词示例

表格展示为最新版插件功能：

| 特殊关键字 | 示例                   | 功能                                                         |
| ---------- | :--------------------- | ------------------------------------------------------------ |
| 帮助       | @机器人 帮助           | 显示主菜单                                                   |
| help       | @机器人 help           | 显示主菜单                                                   |
| 天气       | @机器人 天气深圳       | 查看深圳的天气                                               |
| 翻译       | @机器人 翻译i love you | 中英翻译                                                     |
| 笑话       | @机器人 笑话           | 笑话                                                         |
| 歌词       | @机器人 歌词后来       | 查看歌词                                                     |
| 成语       | @机器人 成语暗度陈仓   | 查看成语的释义                                               |
| 前线报道   | 前线报道               | 查看工会战前线Boss信息                                       |
| 会战报表   | 会战报表               | 查看当日出刀情况                                             |
| 会战统计   | 会战统计               | 统计本次会战所有参与玩家数据，包括对任一 Boss 的数据统计     |
| 会战统计   | 会战统计好开心         | 统计玩家名为“好开心”的会战数据                               |
| 谁未出刀   | 谁未出刀               | 查看今日谁没出刀，注意此功能必须要求群成员昵称完全等于游戏名 |
| 谁没出刀   | 谁没出刀               | 同谁未出刀                                                   |
| 催刀       | 催刀                   | 机器人 @ 所有未出刀的群成员并催刀，注意此功能必须要求群成员昵称完全等于游戏名 |
| 一键催刀   | 一键催刀               | 机器人 @ 所有未出刀的群成员并催刀，相较于 催刀 而言，此功能还会 @ 出了刀但未出满的成员，注意此功能必须要求群成员昵称完全等于游戏名 |
| 鸡汤       | 鸡汤                   | 发送随机鸡汤                                                 |
| 美图       | 美图                   | 发送随机二刺螈美图                                           |
| 风景图     | 风景图                 | 发送随机风景图                                               |
| 神秘代码   | 神秘代码萝莉 白丝      | 发送涩图，可指定 tag                                         |
| 涩图       | 涩图萝莉 白丝          | 发送涩图，可指定 tag，相较于神秘代码而言，此涩图放开 R18 限制，更加涩涩，默认情况下发送图片后 30s 后自动撤回，可通过配置修改撤回时间 |
| 音乐       | 音乐克罗地亚狂想曲     | 从酷我、QQ、网易云聚合搜索音乐，并发送卡片                   |

## 声明

我不是生产者，我只是 API 的搬运工，由于 API 随时可能停用，故此插件可能随时出问题，请您持续关注此项目，一般情况下 API 失效都会第一时间解决。

## 配置

正常使用本插件不需要任何配置，除非涉及到某些需要 cooike 的功能，例如砍公会战管理。

HRobot 支持配置文件，配置文件在 mcl/config/com.happysnaker.HRobot/config.json 中，您可以手动创建配置文件，不过建议您运行一次 mcl.cmd，这会自动生产配置文件，配置文件相关信息可参考：[配置手册](https://github.com/happysnaker/mirai-plugin-HRobot/blob/master/CONFIG.md)

## 贡献

提出你的创意，或者再此源代码上进行二次开发，开发相关手册可参考：[开发手册](https://github.com/happysnaker/mirai-plugin-HRobot/blob/master/DEV.md)

