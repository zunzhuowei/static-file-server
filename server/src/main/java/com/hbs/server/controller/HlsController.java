package com.hbs.server.controller;

import com.hbs.server.model.VideoReq;
import com.hbs.server.service.IVideoProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by zun.wei on 2020/1/2 17:26.
 * Description: HLS 服务
 */
@Controller
@RequestMapping("/vod")
public class HlsController {

    @Resource
    private IVideoProvider videoProvider;


    @PermitAll
    @RequestMapping(value = "/{uuid}/index.m3u8")
    public void downIndexM3u82(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable String uuid) throws IOException {
        VideoReq videoReq = new VideoReq();
        videoReq.setRequest(request);
        videoReq.setResponse(response);
        videoReq.setUuid(uuid);
        videoProvider.getPlayerList(videoReq);
    }

    @PermitAll
    @RequestMapping("/{uuid}/{index}.ts")
    public void downTs(HttpServletRequest request,
                       HttpServletResponse response,
                       @PathVariable String index,
                       @PathVariable String uuid) throws IOException {
        VideoReq videoReq = new VideoReq();
        videoReq.setRequest(request);
        videoReq.setResponse(response);
        videoReq.setIndex(index);
        videoReq.setUuid(uuid);
        videoProvider.getPlayerDataByIndex(videoReq);
    }

    //http://127.0.0.1:7719/hls/api/14ab66e35c7848e1adfc4c3c822510a8/index.m3u8


}
