<p align="center">
  <a href="https://mikuac.com/archives/675"><img src="https://mikuac.com/images/yuri.jpg" width="200" height="200" alt="Yuri"></a>
</p>

<div align="center">

# YuriBot

</div>

<p align="center">
    <img src="https://img.shields.io/github/stars/MisakaTAT/YuriBot?style=flat-square" alt="stars">
    <img src="https://img.shields.io/github/forks/MisakaTAT/YuriBot?style=flat-square" alt="forks">
    <a href="https://github.com/MisakaTAT/YuriBot/issues"><img src="https://img.shields.io/github/issues/MisakaTAT/YuriBot?style=flat-square" alt="issues"></a>
    <img src="https://img.shields.io/github/downloads/MisakaTAT/YuriBot/total?style=flat-square?style=flat-square" alt="downloads">
    <a href="https://github.com/MisakaTAT/YuriBot/blob/main/LICENSE"><img src="https://img.shields.io/github/license/MisakaTAT/YuriBot?style=flat-square" alt="license"></a>
    <img src="https://img.shields.io/badge/jdk-15+-brightgreen.svg?style=flat-square" alt="jdk-version">
    <a href="https://qm.qq.com/cgi-bin/qm/qr?k=Fl3-G9irYp84ng7LAFlTvqrOGIFHdufR&jump_from=webapi"><img src="https://img.shields.io/badge/QQ群-204219849-brightgreen.svg?style=flat-square" alt="qq-group"></a>
</p>

# Features

- [x] 一言
- [x] 复读姬
- [x] HttpCat
- [x] 色图time (
- [x] WhatAnime 搜番
- [x] SauceNao 搜图
- [x] AnimeThesaurus
- [x] 入群欢迎与退群提醒
- [x] 彩虹六号游戏战绩查询
- [x] 群组消息批量推送
- [x] 解析哔哩哔哩小程序
- [x] av号与bv号互相转换
- [x] Telegram消息转发至QQ `[已移除]`
- [x] 区块链虚拟货币信息查询 `[已移除]`
- [x] 逆转裁判字体风格图片生成
- [x] Steam Rep信息查询
- [x] 撤回含有敏感词的消息

# Install Maven For Centos

```shell
wget http://mirrors.hust.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
tar -zxvf  apache-maven-3.6.3-bin.tar.gz
# Rename dir
mv apache-maven-3.6.3 maven
# Edit /etc/profile add
export MAVEN_HOME=/root/maven
export PATH=$MAVEN_HOME/bin:$PATH
# Update profile
source /etc/profile
# Check mvn install success
mvn -version
```

# Build Jar Package

```shell
# Get source
git clone https://github.com/MisakaTAT/YuriBot.git
# Build and skip test
mvn clean package -Dmaven.test.skip=true
# Output to target dir
java -jar YuriBot-main/target/Yuri-Bot-v1.0.1-Alpha.jar
# The first run generates a default config file (you can custom this config file)
vim config.json
```

# Config File

<details>
<summary>点击查看详细内容</summary>
<pre><code>  
// 这是一个配置文件示例，首次运行将在目录下生成config.json，请根据实际需求修改
{
    "server": {
        // 运行地址
        "address": "127.0.0.1",
        // 运行端口
        "port": 5000
    },
    // 一言
    "hitokoto": {
        // 冷却时间，单位秒
        "cdTime": 10
    },
    "bot": {
        // Bot名
        "botName": "悠里",
        // 管理员QQ
        "adminId": 0,
        // Bot QQ
        "selfId": 0
    },
    "prefix": {
        // 指令前缀
        "prefix": "."
    },
    "setu": {
        // 色图ApiKey
        "apiKey": "Api Key Value",
        // 冷却时间，单位秒
        "cdTime": 120,
        // 撤回时间，单位秒
        "delTime": 30,
        // 每日上限
        "maxGet": 15
    },
    "repeat": {
        // 复读阈值
     "randomCountSize": 5
    },
    "banUtils": {
        // 搜图搜番时间阈值，单位秒
        "limitTime": 30,
        // 时间阈值最大发送图片数量
        "limitCount": 10
    },
    "sauceNao": {
        // SauceNao Api Key
        "apiKey": "Api Key Value"
    }
}
</code></pre>
</details>

# Credits

* Powered By [Shiro](https://github.com/MisakaTAT/Shiro)
* [setu-api](https://api.lolicon.app/#/setu)
* [heroku-pximg-proxy](https://github.com/Tsuk1ko/heroku-pximg-proxy)

# Client Connect

修改目录下生成的 `config.json`（也可使用默认值）

```json
{
  "server": {
    "address": "127.0.0.1",
    "port": 5000
  }
}
```

配置反向WebSocket

以go-cqhttp为例 修改`config.yaml`如下参数

```yaml
- ws-reverse:
    universal: ws://127.0.0.1:5000/ws/shiro
```

# License

MIT License

Copyright (c) <year> <copyright holders>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice (including the next paragraph) shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

# Thanks

Thanks [JetBrains](https://www.jetbrains.com/?from=mirai) Provide Free License Support OpenSource Project

[<img src="https://mikuac.com/images/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/?from=mirai)