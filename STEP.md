# 登录步骤

首先你需要前往[Releases · mzdluo123/TxCaptchaHelper (github.com)](https://github.com/mzdluo123/TxCaptchaHelper/releases)下载对应 apk 到你的手机上，注意是使用手机下载，不要下载到电脑上，后续我们需要使用此 apk。

1. 在命令行中输入`login 账号 密码`，然后回车。

2. 随后弹出窗口，点击 Open with TXCa.... 按钮。如果你没有弹出此窗口，而是弹出第四步的窗口，那么直接执行第四步即可。

   ![image-20220206211520272](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220206211520272.png)

3. 此时出现需要请求码，在手机上打开 apk 输入请求码完成滑块验证。

   ![image-20220206211648787](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220206211648787.png)

4. 通过后会弹出如下窗口，复制 URL 发送到手机 QQ，在手机 QQ 中打开，进行人脸识别或QQ扫码即可，成功后需要手动关闭弹窗。

   ![image-20220206211827253](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora/image-20220206211827253.png)

**登陆后，可以在命令行输入`autoLogin add 账号 密码`以配置自动登录。**