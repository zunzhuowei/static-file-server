package com.hbs.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.hbs")
public class ConfigProperties {


    // ffmpeg 安装目录
    private String ffmpegPath;

    // ts 切片文件根路径
    private String sliceFilePath;

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getSliceFilePath() {
        return sliceFilePath;
    }

    public void setSliceFilePath(String sliceFilePath) {
        this.sliceFilePath = sliceFilePath;
    }
}
