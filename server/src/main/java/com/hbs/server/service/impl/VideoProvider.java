package com.hbs.server.service.impl;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.hbs.server.config.ConfigProperties;
import com.hbs.server.model.VideoReq;
import com.hbs.server.service.IVideoProvider;
import com.hbs.server.utils.VideoUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zun.wei on 2020/1/2 14:08.
 * Description:
 */
@Component
public class VideoProvider implements IVideoProvider {

    @Resource
    private ConfigProperties configProperties;

    @Override
    public void saveFile(VideoReq videoReq) throws IOException {
        InputStream inputStream = videoReq.getInputStream();
        //String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String originalFilename = videoReq.getOriginalFilename();
        //System.out.println("originalFilename = " + originalFilename);
        FileUtils.copyInputStreamToFile(inputStream, new File(originalFilename));
    }

    @Override
    public boolean existSplitTs(String uuid) {
        String sliceFilePath = configProperties.getSliceFilePath();
        String tssPath = sliceFilePath + uuid;
        File folder = new File(tssPath);
        if (!folder.exists()) {
            return false;
        }
        return true;
    }


    @Override
    public String splitMp42Ts(VideoReq videoReq) {
        String ffmpegPath = configProperties.getFfmpegPath();
        String sliceFilePath = configProperties.getSliceFilePath();
        //String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        final String uuid = videoReq.getUuid();
        Path BIN = Paths.get(ffmpegPath);

        System.out.println("uuid = " + uuid);
        String tssPath = sliceFilePath + uuid;
        File folder = new File(tssPath);
        if (!folder.isDirectory()) {
            boolean b = folder.mkdirs();
        }

        //ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            try (InputStream inputStream = videoReq.getInputStream()){
                File tempFileMp4 = File.createTempFile(uuid, ".mp4");
                File tempFileTs = File.createTempFile(uuid, ".ts");
                FileUtils.copyInputStreamToFile(inputStream, tempFileMp4);

                //ffmpeg -i test.mp4 -codec copy -vbsf h264_mp4toannexb test.ts
                // mp4 转 ts
                FFmpeg.atPath(BIN)
                        .addInput(UrlInput.fromPath(Paths.get(tempFileMp4.getPath())))
                        .setLogLevel(LogLevel.INFO) // 输入日志警告级别
                        .setOverwriteOutput(true) // 覆盖输出
                        .addArguments("-codec","copy") //视频\音频格式（直接复制）
                        .addArguments("-vbsf", "h264_mp4toannexb") // 转换的格式
                        .addArgument(tempFileTs.getPath())//输出位置
                        .execute();
                boolean deleteTempMp4 = tempFileMp4.delete();
                System.out.println("deleteTempMp4 = " + deleteTempMp4);

                //ffmpeg -i 12生肖.ts -c copy -map 0 -f segment -segment_list playlist.m3u8 -segment_time 10 output%03d.ts
                // ts 分片
                Path VIDEO_TS = Paths.get(tempFileTs.getPath());
                FFmpeg.atPath(BIN)
                        //.addInput(PipeInput.pumpFrom(inputStream))
                        .addInput(UrlInput.fromPath(VIDEO_TS))
                        .setLogLevel(LogLevel.INFO) // 输入日志警告级别
                        .setOverwriteOutput(true) // 覆盖输出
                        .addArguments("-c", "copy") //视频\音频格式（直接复制）
                        .addArguments("-map", "0")
                        .addArguments("-f", "segment") // 转换的格式
                        .addArguments("-segment_time", "10") // ts视频切片的长度
                        .addArguments("-segment_list", folder.getPath() + "/index.m3u8") //索引文件路径
                        .addArgument(folder.getPath() + "/" + uuid + "_%03d.ts")//切片文件路径
                        .execute();

                boolean delTemp = tempFileTs.delete();
                System.out.println("delTemp = " + delTemp);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    FileUtils.deleteDirectory(folder);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        //});
        return uuid;
    }

    /**
     *  获取请求响应封装
     */
    private void getResponse(HttpServletResponse response, InputStream inputStream, String fileName)
            throws IOException {
        System.out.println("fileName = " + fileName);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStream outputStream = response.getOutputStream()){
            IOUtils.copy(inputStream, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            long timeMillis = System.currentTimeMillis();
            response.setHeader("Date", VideoUtils.getDateString(timeMillis));
            response.setHeader("Content-Type", VideoUtils.getMimeType(fileName));
            response.setHeader("Content-Length", bytes.length + "");
            response.setHeader("Last-Modified", VideoUtils.getDateString(timeMillis));
            response.setHeader("Expires", VideoUtils.getDateString(timeMillis + VideoUtils.VOD_CACHE_TIME));
            response.setHeader("Cache-Control", "max-age=" + (VideoUtils.VOD_CACHE_TIME / 1000));
            //response.setHeader("Access-Control-Allow-Origin", "*");

            IOUtils.write(bytes, outputStream);
        }
    }

    @Override
    public void getPlayerList(VideoReq videoReq) throws IOException {
        String uuid = videoReq.getUuid();
        HttpServletResponse response = videoReq.getResponse();
        HttpServletRequest request = videoReq.getRequest();
        String sliceFilePath = configProperties.getSliceFilePath();
        File file = new File(sliceFilePath + uuid + "/index.m3u8");
        if (file.exists()) {
            try (InputStream fileInputStream = new FileInputStream(file)){
                String fileName = file.getName();
                getResponse(response, fileInputStream, fileName);
            }
        }
    }

    @Override
    public void getPlayerDataByIndex(VideoReq videoReq) throws IOException {
        String uuid = videoReq.getUuid();
        String index = videoReq.getIndex();
        HttpServletResponse response = videoReq.getResponse();
        HttpServletRequest request = videoReq.getRequest();
        String sliceFilePath = configProperties.getSliceFilePath();
        File file = new File(sliceFilePath + uuid + "/" + index + ".ts");
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)){
                String fileName = file.getName();
                getResponse(response, fileInputStream, fileName);
            }
        }
    }

    @Override
    public String randomMovies() {
        String sliceFilePath = configProperties.getSliceFilePath();
        File file = new File(sliceFilePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.nonNull(files)) {
                Random random = new Random();
                int index = random.nextInt(files.length);
                return files[index].getName();
            }
        }
        return "default";
    }

    @Override
    public void downVideo(VideoReq videoReq) throws IOException {
        String uuid = videoReq.getUuid();
        HttpServletResponse response = videoReq.getResponse();
        String sliceFilePath = configProperties.getSliceFilePath();
        String ffmpegPath = configProperties.getFfmpegPath();
        Path BIN = Paths.get(ffmpegPath);
        //log.debug("downVideo ---::{}", uuid);

        File fileDir = new File(sliceFilePath + uuid);
        if (fileDir.exists() && fileDir.isDirectory()) {
            String[] list = fileDir.list();
            if (Objects.nonNull(list) && list.length > 0) {
                List<String> tsFilesList = Arrays.stream(list)
                        .filter(e -> StringUtils.endsWith(e, ".ts"))
                        .sorted()
                        .collect(Collectors.toList());
                String path = fileDir.getPath(); // ts 文件所在目录
                String fileName = fileDir.getName();//原文件名称
                File tempFile = File.createTempFile(fileName, "__temp_ts.ts");
                File temp = File.createTempFile(fileName, "__temp_mp4.mp4");
                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile, true));
                     ServletOutputStream outputStream = response.getOutputStream()){
                    for (String tsfileName : tsFilesList) {
                        File tsFile = new File(path + File.separator + tsfileName);
                        FileUtils.copyFile(tsFile, out);
                    }

                    // 大 ts 文件 转换格式成mp4格式
                    Path input_path = Paths.get(tempFile.getPath());
                    Path out_path = Paths.get(temp.getPath());
                    //Path BIN = Paths.get("D:\\ffmpeg-20191223-5b42d33-win64-static\\bin\\");
                    FFmpeg.atPath(BIN)
                            .addInput(UrlInput.fromPath(input_path))
                            .setOverwriteOutput(true)
                            .addOutput(UrlOutput.toPath(out_path).copyAllCodecs())
                            .execute();

                    response.setHeader("Content-Disposition","attachment; filename=" + temp.getName());
                    response.setContentType("application/text");
                    // 将文件转到输出流中
                    FileUtils.copyFile(temp, outputStream);
                } finally {
                    // 删除 大ts文件
                    FileUtils.deleteQuietly(tempFile);
                    // 删除临时文件
                    FileUtils.deleteQuietly(temp);
                }
            }

        }
    }

    @Override
    public boolean deleteVideo(VideoReq videoReq) throws IOException {
        String uuid = videoReq.getUuid();
        //log.debug("deleteVideo ---::{}", uuid);
        String sliceFilePath = configProperties.getSliceFilePath();
        File file = new File(sliceFilePath + uuid);
        if (file.exists() && file.isDirectory()) {
            // 删除 ts小文件碎片
            FileUtils.deleteDirectory(file);
            return true;
        }
        return false;
    }


}
