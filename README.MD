# Bili-Dowloader
## 这是什么
~~可能是个视频下载器~~
## 目前完成的功能
1. B站登录 (gui.login/utils.login)
    - 账号密码登录
    - 短信验证码登录
    - 扫码登陆
2. 下载支持 (gui.viewVideo/utils.download/utils.video)
    - BV/AV号支持
    - 视频分P支持
    - 视频封面获取支持
## 划为计划内的未完成功能
1. ~~Bilibili视频信息获取及下载~~
2. VLC播放视频支持
   - ~~播放本地/无防盗链视频支持~~
   - bilibili防盗链视频支持(~~可能需要一个反代?~~)
## 已知的BUG (求各位大佬Pr修复啊)
1. 下载视频时会卡死Gui线程
## 信息
使用的api来自Bilibili官方 [Bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)

运行所需环境: 
- v1.0.1≤java版本≥11
- 更高版本需要java版本>11
## 如何下载
开发版当前情况:

![bulid](https://img.shields.io/github/workflow/status/heartalborada-del/bili-downloader/Bulid%20Dev%20Version)

[戳我去Github Actions下载](https://github.com/heartalborada-del/bili-downloader/actions/workflows/dev.yml)

Release版本当前情况:

![release](https://img.shields.io/github/v/release/heartalborada-del/bili-downloader.svg)
![bulid](https://img.shields.io/github/workflow/status/heartalborada-del/bili-downloader/Bulid%20Release%20Version)

[戳我去Github Releases下载](https://github.com/heartalborada-del/bili-downloader/releases)