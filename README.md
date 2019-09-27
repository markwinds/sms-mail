# August - sms to mail tool

## 简介

![使用示范](https://img-blog.csdnimg.cn/20190927153524915.gif)

August是一款可以将手机上的短信转发到指定邮箱的app,如果你安装了他，那么当你忘记带手机却需要手机验证码验证的时候就可以通过邮箱来查看验证码，当然你也可以用他来转发其他短信。具体的操作有一下几步

## 使用方法

- 设置你的发件邮箱
  - 填写你发件邮箱的服务器一般为smtp.*name*.com 其中name是qq,gmail等服务商名称
  - 填写你发送邮件的地址
  - 填写你发送邮件帐号的密码

- 添加收件邮箱 按一下首页的浮动按钮，添加收件箱

- 使能收件箱，点击对应收件箱的开关到开的位置

- 将August锁定，就是不要让你的手机关闭Augusu（可以退出August但不能杀死这个进程）August只是注册了一个监听，请放心使用，他对性能和续航的影响几乎可以忽略不计

## 待开发的功能

- 状态栏提醒
- 首次打开app的使用介绍
- 输入合法检测
- 短信过滤

## Bug

## Tips

### August用到的开源库

仓库|简介|使用中遇到的问题
--|--|--
[ListViewAnimations](https://github.com/nhaarman/ListViewAnimations)|一个自带动画功能的强大listview|需要自定义adpter继承自他的模板，重写特有的几个函数
[FloatingActionButton](https://github.com/makovkastar/FloatingActionButton)|悬浮菜单按钮|其中的布局文件用到的几个组件因为android版本更新的问题已经被重新命名了，所以报错组件找不到
[SwitchButton](https://github.com/kyleduo/SwitchButton)|就是上面演示的选择开关|在adpter中设置类似checkBox组件时要先设置监听再设置checkBox的选中状态
[material-dialogs](https://github.com/afollestad/material-dialogs)|对话框|对于Bottom Sheets的自定义布局要先加载布局然后findViewById
[MaterialTextField](https://github.com/florent37/MaterialTextField)|美化输入框|无
[TinyDB](https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo)|帮助你方便存储数据（arrayList等都很方便）|kotlin普通类型作为泛型传参的方法MailItem::class.java
[maildroid](https://github.com/nedimf/maildroid)|邮件发送的封装库|发送邮件的正文部分中文乱码，可以通过重写MaildroidX类的html编码方式来解决
[SmsVerifyCatcher](https://github.com/stfalcon-studio/SmsVerifyCatcher)|监听过滤短信|无

### Android UI设计相关的开源库

- [安卓UI设计库](https://gamedun.github.io/-----https://github.com/wasabeef/awesome-android-ui)
- [material图标库](https://github.com/google/material-design-icons)
- [material输入框](https://github.com/rengwuxian/MaterialEditText)
- [自动隐藏的浮动按钮](https://github.com/makovkastar/FloatingActionButton)
- [MaterialSheetFab](https://github.com/gowong/material-sheet-fab)
- [带图标的图标输入框](https://github.com/florent37/MaterialTextField)
- [app刚进入提示使用](https://github.com/sjwall/MaterialTapTargetPrompt)
- [滚动列表下拉菜单](https://github.com/traex/ExpandableLayout)
- [app首次安装的滑动介绍](https://github.com/sacot41/SCViewPager)

## 几款同类型的app

- [Transmis](https://gamedun.github.io/-----https://github.com/dss886/Transmis)能够接受电话短信，能够邮件钉钉提醒信息过滤，自定义信息接受格式
