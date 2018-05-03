package com.juziwl.palette.netty.model;


/**
 * 推送消息类型
 *
 * @author 徐飞
 * @version 2016/02/24 19:40
 */
public class PushMsg extends BaseMsg {

    public int eventType = -1;
    public float x = 0, y = 0;

    public PushMsg() {
        super();
        type = MsgType.PUSH;
    }

    @Override
    public String toString() {
        return "PushMsg{" +
                "eventType=" + eventType +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_CANCEL = 3;
}