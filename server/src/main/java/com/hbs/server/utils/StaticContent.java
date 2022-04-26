package com.hbs.server.utils;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by zun.wei on 2022/4/25.
 */
public class StaticContent {


    public static String getClasspathString(String fileName) {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        byte[] bytes = null;
        try (InputStream inputStream = classPathResource.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            IOUtils.copy(inputStream, outputStream);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String getStaticFileString(String fileName) {
        return getClasspathString("static/" + fileName);
    }

    public static String getTemplate(String fileName) {
        return getClasspathString("templates/" + fileName);
    }


}
