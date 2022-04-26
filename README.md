## 静态文件服务器

```html
ffmpeg -i demo.mp4 -profile:v baseline -level 3.0 -start_number 0 -hls_time 10 -hls_list_size 0 -f hls demo.m3u8
```