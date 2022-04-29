package com.hbs.server.utils;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * Created by zun.wei on 2022/4/29.
 */
public class YamlUtils {


    public static String getProperty(String key) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("application.yml");
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        final List<PropertySource<?>> load = yamlPropertySourceLoader.load("pro", classPathResource);
        return load.get(0).getProperty(key).toString();
    }


}
