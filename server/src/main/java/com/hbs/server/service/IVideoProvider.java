package com.hbs.server.service;


import com.hbs.server.model.VideoReq;

import java.io.IOException;

/**
 * Created by zun.wei on 2020/1/2 14:07.
 * Description:
 */
public interface IVideoProvider {


    boolean existSplitTs(String uuid);

    /**
     *  转 mp4 成ts，ts 切片
     */
    String splitMp42Ts(VideoReq videoReq);

    /**
     *  获取播放列表配置
     */
    void getPlayerList(VideoReq videoReq) throws IOException;

    /**
     *  根据播放列表配置下标获取播放数据ts块
     */
    void getPlayerDataByIndex(VideoReq videoReq) throws IOException;

    /**
     *  随机获取 点播视频 uuid
     */
    String randomMovies();

    /**
     * 保存文件
     */
    void saveFile(VideoReq videoReq) throws IOException;

    /**
     *  下载视频
     */
    void downVideo(VideoReq videoReq) throws IOException;

    /**
     * 删除视频
     */
    boolean deleteVideo(VideoReq videoReq) throws IOException;

}
