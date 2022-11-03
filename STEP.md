# 登录步骤
首先你需要前往[Releases · mzdluo123/TxCaptchaHelper (github.com)](https://github.com/mzdluo123/TxCaptchaHelper/releases)下载对应 apk 到你的手机上，注意是使用手机下载，不要下载到电脑上，后续我们需要使用此 apk。

## 2022/10 出现版本过低请升级客户端问题解决方案
Mirai 已有对应 issue，可查看 [issue#2298](https://github.com/mamoe/mirai/issues/2298)

我自己也遇到了这个问题，我是使用容器部署机器人的，机器人是我 6 个月大的小号，我的解决方法是：
1. 删除 `bot/qq号/device.json` 文件
2. 手动在 `config/Console/AutoLogin.yml` 配置自动登录
3. protocol 配置修改为 `protocol: ANDROID_PAD`
4. 登陆时保持手机 QQ 同时在线
```yaml
# config/Console/AutoLogin.yml
accounts:
  - # 账号, 现只支持 QQ 数字账号
    account: 102536847
    password:
      # 密码种类, 可选 PLAIN 或 MD5
      kind: PLAIN
      # 密码内容, PLAIN 时为密码文本, MD5 时为 16 进制
      value: PASSWORD
    configuration:
      protocol: ANDROID_PAD
```

## WINDOWS 登录
1. 在命令行中输入`login 账号 密码`，然后回车。

2. 随后弹出窗口，点击 Open with TXCa.... 按钮。如果你没有弹出此窗口，而是弹出第四步的窗口，那么直接执行第四步即可。
   ![image-20220206211520272](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220206211520272.png)

3. 等待一段时间，此时出现需要请求码，在手机上打开刚刚下载的 apk 输入请求码完成滑块验证。
   ![image-20220206211648787](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220206211648787.png)

4. 通过后如果弹出如下窗口，复制 URL 在电脑游览器打开，使用 QQ 扫码即可，手机确认时，第一次可能会失败，多确认几次即可，成功后需要手动关闭弹窗。如果没有此步骤，忽略即可。
   ![image-20220206211827253](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220206211827253.png)

   ![image-20221001162352420](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011623580.png)

   
> PS: 如果使用电脑游览器打开无效，你需要将 URL 发送到手机 QQ 上，用手机 QQ 打开进行人脸识别或扫码

**登陆后，可以在命令行输入`autoLogin add 账号 密码`以配置自动登录。**

## Linux 或 Docker 环境登录
1. 在命令行中输入`login 账号 密码`，然后回车。
  ![image](https://user-images.githubusercontent.com/73147033/195557836-23bb220f-bd16-412d-8644-da62b9be20e8.png)
2. 如果出现上图提示，则需要进行验证，输入 `TxCaptchaHepler` ，然后回车，如果不需要验证则忽略。
   ![image-20221001162618180](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011626265.png)
3. 拿到请求码，在手机上打开刚刚下载的 apk 进行验证，如验证通过会有提示。如果仍需验证会有下列输出，如果不需要验证应该就直接登录了。
   ![image-20221001162330017](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011623180.png)
4. 在电脑游览器中打开上图箭头所指链接，进行 QQ 扫码验证，手机确认时，第一次可能会失败，多确认几次即可，成功后在命令行输入任意字符回车。
   ![image-20221001162352420](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011623580.png)
   ![image-20221001162437264](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011624360.png)

> PS: 如果使用电脑游览器打开无效，你需要将 URL 发送到手机 QQ 上，用手机 QQ 打开进行人脸识别或扫码

**登陆后，可以在命令行输入`autoLogin add 账号 密码`以配置自动登录。**
