package com.hbs.server.utils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zun.wei on 2020/1/3 10:11.
 * Description:
 */
public class VideoUtils {


    /** 点播缓存时长 */
    public static final int VOD_CACHE_TIME = 1000 * 60;
    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    private static final String HTTP_MIME_TYPE__VIDEO_MP2T = "video/MP2T";
    private static final String HTTP_MIME_TYPE__TEXT = "text/html";
    private static final String HTTP_MIME_TYPE__MPEG_URL_APPLE = "application/vnd.apple.mpegurl";

    private static Map<String, String> DEFAULT_MIME_TYPES;

    static {
        Map<String, String> mimeTypes = new HashMap<String, String>();
        mimeTypes.put("mp3", HTTP_MIME_TYPE__VIDEO_MP2T);
        mimeTypes.put("ts", HTTP_MIME_TYPE__VIDEO_MP2T);
        mimeTypes.put("m3u8", HTTP_MIME_TYPE__MPEG_URL_APPLE);
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("csv", "text/csv");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("xhtml", "application/xhtml+xml");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("zip", "application/zip");
        mimeTypes.put("tar", "application/x-tar");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("jpg", "image/jpg");
        mimeTypes.put("tiff", "image/tiff");
        mimeTypes.put("tif", "image/tif");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("ico", "image/vnd.microsoft.icon");
        DEFAULT_MIME_TYPES = Collections.unmodifiableMap(mimeTypes);
    }

    /**
     *  获取时间字符串
     * @param timemillis 毫秒数
     * @return
     */
    public static String getDateString(long timemillis) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));
        return dateFormatter.format(new Date(timemillis));
    }

    /**
     *  获取文件拓展名 对应的类型
     * @param filename 文件名
     */
    public static String getMimeType(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return HTTP_MIME_TYPE__TEXT;
        }
        String mimeType = DEFAULT_MIME_TYPES.get(filename.substring(lastDot + 1).toLowerCase());
        if (mimeType == null)
            return HTTP_MIME_TYPE__TEXT;
        else
            return mimeType;
    }

}
