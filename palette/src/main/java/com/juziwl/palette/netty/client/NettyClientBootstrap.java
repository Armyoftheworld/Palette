package com.juziwl.palette.netty.client;


import com.juziwl.palette.util.Utils;
import com.juziwl.palette.netty.NettyConfig;
import com.juziwl.palette.netty.model.LoginMsg;
import com.juziwl.palette.netty.model.PushMsg;
import com.orhanobut.logger.Logger;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author 徐飞
 * @version 2016/02/25 14:11
 */
public class NettyClientBootstrap {

    private volatile static NettyClientBootstrap instance = null;
    private SocketChannel socketChannel;

    private NettyClientBootstrap() {
    }

    public static NettyClientBootstrap getInstance() {
        if (instance == null) {
            synchronized (NettyClientBootstrap.class) {
                if (instance == null) {
                    instance = new NettyClientBootstrap();
                }
            }
        }
        return instance;
    }

    public void create(String deviceId, OnConnectListener listener) {
        if (socketChannel != null && socketChannel.isOpen()) {
            Logger.d("客户端已连接到服务器");
            listener.onExist();
        } else {
            Logger.d("长链接开始");
            if (startConnect(listener)) {
                LoginMsg loginMsg = new LoginMsg();
                loginMsg.username = "client";
                loginMsg.password = "client";
                loginMsg.clientId = Utils.getLocalIPAddress();
                socketChannel.writeAndFlush(loginMsg);
                Logger.d("客户端成功连接到服务器");
                listener.onSuccess();
            } else {
                Logger.d("连接服务器失败");
                listener.onFailure();
            }
        }
    }

    private Boolean startConnect(final OnConnectListener listener) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(NettyConfig.SERVER_HOST, NettyConfig.PORT);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
                socketChannel.pipeline().addLast(new ObjectEncoder());
                socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                socketChannel.pipeline().addLast(new NettyClientHandler(listener));
            }
        });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(NettyConfig.SERVER_HOST, NettyConfig.PORT)).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
                Logger.d("connect server  成功---------");
                return true;
            } else {
                Logger.d("connect server  失败---------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d(Utils.outputError(e));
        }
        return false;
    }

    /**
     * 关闭通道
     */
    public void closeChannel() {
        Logger.d("-------------------------------client close-----------------------------");
        if (socketChannel != null) {
            socketChannel.closeFuture();
            socketChannel.close();
            socketChannel = null;
        }
    }

    /**
     * @return 返回通道连接状态
     */
    public boolean isOpen() {
        if (socketChannel != null) {
            return socketChannel.isOpen();
        }
        return false;
    }

    public void pushOrder(PushMsg pushMsg) {
        if (socketChannel == null) {
            Logger.d("请先与作为服务器的设备建立连接");
            return;
        }
        if (isOpen()) {
            socketChannel.writeAndFlush(pushMsg);
        } else {
            Logger.d("无法连接到服务器，请检查网络");
        }
    }

    public interface OnConnectListener {

        void onExist();

        void onSuccess();

        void onFailure();

        void receiveData(PushMsg pushMsg);
    }
}