package com.juziwl.palette.netty.model;


/**
 * 登录验证消息类型
 *
 * @author 徐飞
 * @version 2016/02/24 19:40
 */
public class LoginMsg extends BaseMsg {
    public String username = "";
    public String password = "";
    public int screenWidth = 0, screenHeight = 0;

    public LoginMsg() {
        super();
        type = MsgType.LOGIN;
    }

    @Override
    public String toString() {
        return "LoginMsg{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                '}';
    }
}