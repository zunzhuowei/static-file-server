package com.hbs.server;

import com.hbs.server.statics.HttpStaticFileServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by zun.wei on 2022/4/25.
 */
@SpringBootApplication
public class FileServer {

    public static ConfigurableApplicationContext context = null;

    public static void main(String[] args) throws Exception {
        context = SpringApplication.run(FileServer.class, args);
        HttpStaticFileServer.main(args);
    }


}
