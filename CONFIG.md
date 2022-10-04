# 配置文件

- [声明](#声明)
- [模板](#模板)
- [获取坎公 Cookie](#获取砍公-cookie)

## 声明
- v3.0 之后正式使用 yaml 作为配置文件，早期版本 json 配置请移步: [历史版本配置](version/V2&V1_CONFIG.md)，对于正在使用 json 配置文件的用户可以前往 [JSON转YAML,YAML转JSON - 在线工具 - OKTools ](https://oktools.net/json2yaml)将 JSON 转为 yaml 文件进行迁移。
- v3.0 之后在 `mcl/config/com.happysnaker.HRobot` 目录下存在一个 sensitiveWord.txt 文件夹，这个文件作为 HRobot 的敏感词库，敏感词需换行作为分割，**默认为空文件，需要你自行配置**。本仓库提供了一个轻量级的敏感词库：[敏感词库](./sensitiveWord.txt) ，你可以直接复制到 mcl/config/com.happysnaker.HRobot 目录下的 sensitiveWord.txt 文件中。或者使用 shell 命令 `wget https://raw.githubusercontent.com/happysnaker/mirai-plugin-HRobot/master/sensitiveWord.txt` 下载。
- 在 `mcl/data/com.happysnaker.HRobot` 目录下存在一个 `error.log`，这里会记录机器人运行时发生的错误。

## 模板

我们在配置文件给出了每一项配置的详细说明，你可以移步配置文件模板查看对应功能，在首次运行 mcl 后，HRobot 会自动生成配置文件模板。

如果配置文件未能给出详细说明，你可以查看目录看看是否有你想要的答案，如果仍有疑问请提出 ISSUE。

- [v3.3 配置文件模板](./version/v3.3_config.md)
- [v3.2 配置文件模板](./version/v3.2_config.md)
- [v3.1 配置文件模板](./version/v3.1_config.md)
- [v3.0 配置文件模板](./version/v3.0_config.md)
- [更早版本配置文件模板](./version/V2&V1_CONFIG.md)

## 获取砍公 cookie

坎公相关功能需要配置 Cookie，请参考如下步骤获取：

1. 用电脑点击[坎公百宝袋-bigfun社区](https://www.bigfun.cn/tools/gt/)并使用自己账号登录。
2. 点击 f12 打开游览器开发工具，找到网络工具。
3. 保持 f12 打开，同时点击前线报道，找到名称为 feweb?target=kan-gong-guild-boss-info%2Fa 的项。
![image-20220131193950450](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220131193950450.png)

4. 再标头中找到请求标头，复制 cookie 即可。
![image-20220131194105416](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220131194105416.png)





















