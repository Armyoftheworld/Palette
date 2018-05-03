package com.juziwl.palette.netty.server;


import com.juziwl.palette.netty.NettyConfig;
import com.juziwl.palette.netty.model.BaseMsg;
import com.juziwl.palette.netty.model.PushMsg;
import com.orhanobut.logger.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author 徐飞
 * @version 2016/02/24 19:43
 */
public class NettyServerBootstrap {

    private volatile static NettyServerBootstrap instance = null;
    private NioServerSocketChannel channel = null;

    private NettyServerBootstrap() {
    }

    public static NettyServerBootstrap getInstance() {
        if (instance == null) {
            synchronized (NettyServerBootstrap.class) {
                if (instance == null) {
                    instance = new NettyServerBootstrap();
                }
            }
        }
        return instance;
    }

    public void create(final OnStartListener onStartListener) {
        if (channel != null) {
            return;
        }
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        //保持长连接状态
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new ObjectEncoder());
                p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                p.addLast(new NettyServerHandler(onStartListener));
            }
        });
        try {
            ChannelFuture f = bootstrap.bind(NettyConfig.PORT).sync();
            channel = (NioServerSocketChannel) f.channel();
            if (f.isSuccess()) {
                onStartListener.onSuccess();
            }
            channel.closeFuture().sync();//相当于在这里阻塞，直到serverchannel关闭
        } catch (Exception e) {
            e.printStackTrace();
            onStartListener.onFailure();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public void close() {
        Logger.d("-------------------------------server close-----------------------------");
        if (channel != null) {
            channel.closeFuture();
            channel.close();
            channel = null;
        }
    }

    public void pushAll(BaseMsg pushMsg) {
        for (SocketChannel channel : NettyChannelMap.getAll()) {
            if (channel != null) {
                channel.writeAndFlush(pushMsg);
            }
        }
    }

    public void push(String clientId, BaseMsg pushMsg) {
        SocketChannel channel = (SocketChannel) NettyChannelMap.get(clientId);
        if (channel != null) {
            channel.writeAndFlush(pushMsg);
        }
    }

    public interface OnStartListener {

        void onSuccess();

        void onFailure();

        void receiveData(PushMsg pushMsg);

        void onClientConnect(String clientId);
    }
}