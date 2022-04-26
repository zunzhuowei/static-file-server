## 静态文件服务器
```html
该项目为静态文件服务器，基于Netty实现，启动服务可以指定目录暴露到http服务器中访问到文件；
同时还加入了vod hls 点播流播放实现；
```

### NOTE
```html
ffmpeg -i demo.mp4 -profile:v baseline -level 3.0 -start_number 0 -hls_time 10 -hls_list_size 0 -f hls demo.m3u8
```