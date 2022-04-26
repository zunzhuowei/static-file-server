package com.hbs.server.statics;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.SystemPropertyUtil;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.util.List;

public final class HttpStaticFileServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    //static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));
    static String filePath = SystemPropertyUtil.get("user.dir");


    public static void main(String[] args) throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("application.yml");
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        final List<PropertySource<?>> load = yamlPropertySourceLoader.load("pro", classPathResource);
        String fp = load.get(0).getProperty("filePath").toString();
        if (StringUtils.hasLength(fp)) {
            filePath = fp;
        }
        System.out.println("filePath = " + filePath);
        int PORT = Integer.parseInt(load.get(0).getProperty("serverPort").toString());

        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .sslProvider(SslProvider.JDK).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpStaticFileServerInitializer(sslCtx));

            Channel ch = b.bind(PORT).sync().channel();

            System.err.println("Open your web browser and navigate to " +
                    (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}