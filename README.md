## 静态文件服务器
```html
该项目为静态文件服务器，基于Netty实现，启动服务可以指定目录暴露到http服务器中访问到文件；
同时还加入了vod hls 点播流播放实现（将静态mp4文件转换成hls流，使用hls播放器播放）；
```
## 效果展示
<table>
    <tr>
        <td><img src="https://raw.githubusercontent.com/zunzhuowei/static-file-server/master/doc/list.png"/></td>
        <td><img src="https://raw.githubusercontent.com/zunzhuowei/static-file-server/master/doc/hls.png"/></td>
    </tr>
</table>

## 注意
* 如果需要 hls 点播服务需要，下载 ffmpeg 库；（https://ffmpeg.org/download.html）

## 配置文件说明
```html
#静态服务器端口号
serverPort: 8080
#静态文件存储目录
filePath: "H:/disk/"
#是否启用Hls服务
enableHlsServer: true

server:
  #hls服务端口号
  port: 8081
spring:
  # 模板引擎
  thymeleaf:
    mode: HTML
    encoding: utf-8
    # 禁用缓存
    cache: false

com:
  hbs:
    # ffmpeg 安装目录
    ffmpegPath: "D:\\ffmpeg-20191223-5b42d33-win64-static\\bin\\"
    # mp4文件切片成ts； 切片文件保存根路径
    sliceFilePath: "D:\\temp\\"
```