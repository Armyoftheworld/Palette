package com.juziwl.palette.netty.server;


import com.juziwl.palette.util.Utils;
import com.juziwl.palette.netty.model.BaseMsg;
import com.juziwl.palette.netty.model.LoginMsg;
import com.juziwl.palette.netty.model.MsgType;
import com.juziwl.palette.netty.model.PushMsg;
import com.orhanobut.logger.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author 徐飞
 * @version 2016/02/25 12:00
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

    private NettyServerBootstrap.OnStartListener onStartListener;

    public NettyServerHandler(NettyServerBootstrap.OnStartListener onStartListener) {
        this.onStartListener = onStartListener;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //channel失效，从Map中移除
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Logger.d("serverError\r" + Utils.outputError(cause));
    }

    private boolean isLogin(LoginMsg loginMsg) {
        String[] userNames = new String[]{"client"};
        String[] passwords = new String[]{"client"};
        for (int i = 0; i < userNames.length; i++) {
            try {
                if (userNames[i].equals(loginMsg.username) && passwords[i].equals(loginMsg.password)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //这里是从客户端过来的消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        if (MsgType.LOGIN.equals(baseMsg.type)) {
            LoginMsg loginMsg = (LoginMsg) baseMsg;
            if (isLogin(loginMsg)) {
                NettyChannelMap.remove(loginMsg.clientId);
                //登录成功,把channel存到服务端的map中
                NettyChannelMap.add(loginMsg.clientId, (SocketChannel) ctx.channel());
                Logger.d("用户 " + loginMsg.clientId + " 登录成功");
                if (onStartListener != null) {
                    onStartListener.onClientConnect(loginMsg.clientId);
                }
            }
        } else if (MsgType.PUSH.equals(baseMsg.type)) {
            PushMsg pushMsg = (PushMsg) baseMsg;
            if (onStartListener != null) {
                onStartListener.receiveData(pushMsg);
            }
        }
        ReferenceCountUtil.release(baseMsg);
    }
}