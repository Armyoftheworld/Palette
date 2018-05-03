package com.juziwl.palette.netty.client;


import com.juziwl.palette.netty.model.BaseMsg;
import com.juziwl.palette.util.Utils;
import com.orhanobut.logger.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author 徐飞
 * @version 2016/02/25 14:11
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<BaseMsg> {

    private NettyClientBootstrap.OnConnectListener listener;

    public NettyClientHandler(NettyClientBootstrap.OnConnectListener listener) {
        this.listener = listener;
    }

    //这里是断线要进行的操作
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Logger.d("与服务器断开连接");
    }

    //这里是出现异常的话要进行的操作
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Logger.d("clientError\t" + Utils.outputError(cause));
    }

    //这里是接受服务端发送过来的消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        if (listener != null) {
            listener.receiveData(baseMsg);
        }
        ReferenceCountUtil.release(baseMsg);
    }
}