# 登录步骤
首先你需要前往[Releases · mzdluo123/TxCaptchaHelper (github.com)](https://github.com/mzdluo123/TxCaptchaHelper/releases)下载对应 apk 到你的手机上，注意是使用手机下载，不要下载到电脑上，后续我们需要使用此 apk。
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
   ![image-20221001162201804](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011622811.png)
2. 如果出现上图提示，则需要进行验证，输入 `TxCaptchaHepler` ，然后回车，如果不需要验证则忽略。
   ![image-20221001162618180](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011626265.png)
3. 拿到请求码，在手机上打开刚刚下载的 apk 进行验证，如验证通过会有提示。如果仍需验证会有下列输出，如果不需要验证应该就直接登录了。
   ![image-20221001162330017](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011623180.png)
4. 在电脑游览器中打开上图箭头所指链接，进行 QQ 扫码验证，手机确认时，第一次可能会失败，多确认几次即可，成功后在命令行输入任意字符回车。
   ![image-20221001162352420](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011623580.png)
   ![image-20221001162437264](https://happysnaker-1306579962.cos.ap-nanjing.myqcloud.com/img/typora202210011624360.png)

> PS: 如果使用电脑游览器打开无效，你需要将 URL 发送到手机 QQ 上，用手机 QQ 打开进行人脸识别或扫码

**登陆后，可以在命令行输入`autoLogin add 账号 密码`以配置自动登录。**