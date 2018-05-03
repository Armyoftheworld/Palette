package com.juziwl.palette.netty.model;

import java.io.Serializable;

/**
 * 消息基类
 * 必须实现序列，serialVersionUID 一定要有
 *
 * @author 徐飞
 * @version 2016/02/24 19:40
 */
public abstract class BaseMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public MsgType type;
    public String clientId = "";

    //初始化客户端id
    public BaseMsg() {
    }
}
