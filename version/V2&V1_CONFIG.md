# 配置文件

- 请注意，v1.0 版本插件在配置上存在一些问题，请前往 release 页上查看详情。

- 获取坎公 cookie 教程请往下翻。
- 配置文件为 JSON 格式，配置后，可以前往[JSON在线格式化校验工具 (ohjson.cn)](http://ohjson.cn/)验证格式是否正确。
- 模板为最新版插件 v2.0-beta，历史版本请前往 [历史版本配置](../HISTORY_VERSION.md) 查看，说明都是一致的。

## 模板

HRobot 支持配置文件，配置文件在 mcl/config/com.happysnaker.HRobot/config.json 中，您可以手动创建配置文件，不过建议您运行一次 mcl.cmd，这会自动生产配置文件 config.json，如下是配置文件的模板（最新版插件）：

```json
{
    "menu":"主菜单",
    "administrator":["超级管理员QQ号", "管理员QQ号", "管理员QQ号"],
    "gtAdministrator":["坎公管理员QQ号", "坎公管理员QQ号"],
    "groupAdministrator":["群管理员QQ号", "群管理员QQ号"],
    "customKeywordSimilarity": 0.8,
    "customKeyword":{
        "全局关键字":"全局关键字回复",
        "#regex#全局正则表达式":"回复",
        "群号1":{
            "群内关键字":"群内关键字回复"
         }
    },
    "exclude":["群号1", "群号2"],
    "include":[],
    "pictureWithdrawalTime": 30,
    "gtConfig":[
         {
             "groupId":"群号1",
             "gtCookie":"cookie1",
             "members":["成员1", "成员2"]
         },
         {
             "groupId":"群号2",
             "gtCookie":"cookie2"
         },
         {
             "groupId":"",
             "gtCookie":"cookie3"
         }
    ]
}

```

## 介绍

- menu：这是机器人的主菜单，当 @机器人 并发送 help 或者 帮助 时，我们会发送此菜单，如果你想使用默认的菜单项，请擦除掉 menu 项(即删除 `"menu":"主菜单",` 一行)。

- administrator：机器人的管理员列表，第一项为超级管理员。

- gtAdministrator：坎公管理员列表。

- groupAdministrator：群管理员列表。

- customKeywordSimilarity：自定义关键字相似度，即当消息与自定义关键字达到此相似度时，视为触发自定义关键字，范围：(0, 1.0]。

- customKeyword：自定义关键字回复配置，如果 key 为群号，则此项只在特定群内生效；否则全局生效。

- exclude： exclude 和 include 之间需选填一项而置另外一项为空，机器人不会处理 exclude 中的群，当  exclude 为空时，机器人会处理所有的群。

- include： include 和 exclude 之间需选填一项而置另外一项为空，当  include 为空时，机器人会处理所有的群，当  include 不为空时，只有被 include 标识的群才会被处理。

  当你同时配置 exclude 和 include 所产生的语义是不确定的，你必须只配置一项。当你两项都不配置时，机器人会处理所有的群。

- pictureWithdrawalTime：涩图发送后自动撤回的等待时长(秒)，该值必须为自然数。

- gtConfig：砍公相关配置

  - groupId：对应的群，当不配置 groupId 或 groupId 为空时，说明匹配所有的群。
  - gtCookie：对应 bigfun 账号的 Cookie，该 cookie 对应 groupId。
  - members：公会成员配置。
  
  例如在上述模板配置中，如果群 1 发送关键字“前线报道”，那么将会用 cookie1 进行查询，同理群 2 会使用 cookie2 进行查询，而群 3、群4、群N都会使用 cookie3 进行查询，这是因为 cookie3 对应的 groupId 为空，因此会匹配所有群。
  
  如果 groupId 为空，请务必将其配置成最后一项(并且只有一个)，因为程序是顺序匹配的。**此外，如果 groupId 存在为空的项，通过命令设置 cookie 可能会设置到末尾(空项后面)，导致无法匹配到！**因此，如果不是特殊需要，强烈建议您不要使 groupId 为空。
  
  

