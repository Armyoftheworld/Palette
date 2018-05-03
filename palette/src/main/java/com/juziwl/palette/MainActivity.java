package com.juziwl.palette;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.juziwl.palette.netty.NettyConfig;
import com.juziwl.palette.netty.client.NettyClientBootstrap;
import com.juziwl.palette.netty.model.BaseMsg;
import com.juziwl.palette.netty.model.LoginMsg;
import com.juziwl.palette.netty.model.MsgType;
import com.juziwl.palette.netty.model.PushMsg;
import com.juziwl.palette.netty.server.NettyServerBootstrap;
import com.juziwl.palette.util.DisplayUtils;
import com.juziwl.palette.util.ThreadExecutor;
import com.juziwl.palette.util.ToastUtils;
import com.juziwl.palette.util.Utils;

public class MainActivity extends AppCompatActivity {

    private TextView tvIpaddr, etIpaddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvIpaddr = findViewById(R.id.tv_ipaddr);
        String localIPAddress = Utils.getLocalIPAddress();
        tvIpaddr.setText(String.format("本机IP：%s", localIPAddress));
        etIpaddr = findViewById(R.id.et_ipaddr);
        etIpaddr.setText(localIPAddress);
    }

    public void beServer(View view) {
        ThreadExecutor.runInThreadPool(() -> NettyServerBootstrap.getInstance().create(new NettyServerBootstrap.OnStartListener() {
            @Override
            public void onSuccess() {
                ToastUtils.showToast(getApplicationContext(), "服务器启动成功");

            }

            @Override
            public void onFailure() {
                ToastUtils.showToast(getApplicationContext(), "服务器启动失败");
            }

            @Override
            public void receiveData(PushMsg pushMsg) {

            }

            @Override
            public void onClientConnect(String clientId) {
                LoginMsg loginMsg = new LoginMsg();
                loginMsg.screenWidth = DisplayUtils.getScreenWidth();
                loginMsg.screenHeight = DisplayUtils.getScreenHeight();
                NettyServerBootstrap.getInstance().push(clientId, loginMsg);
            }
        }));
    }

    public void beClient(View view) {
        ThreadExecutor.runInThreadPool(() -> {
            NettyConfig.SERVER_HOST = etIpaddr.getText().toString();
            NettyClientBootstrap.getInstance().closeChannel();
            NettyClientBootstrap.getInstance().create("", new NettyClientBootstrap.OnConnectListener() {
                @Override
                public void onExist() {
                    ToastUtils.showToast(getApplicationContext(), "已连接到服务器");
                }

                @Override
                public void onSuccess() {
                    ToastUtils.showToast(getApplicationContext(), "连接服务器成功");
                }

                @Override
                public void onFailure() {
                    ToastUtils.showToast(getApplicationContext(), "连接服务器失败");
                }

                @Override
                public void receiveData(BaseMsg baseMsg) {
                    if (baseMsg.type == MsgType.LOGIN) {
                        LoginMsg loginMsg = (LoginMsg) baseMsg;
                        Global.serverScreenHeight = loginMsg.screenHeight;
                        Global.serverScreenWidth = loginMsg.screenWidth;
                        Global.widthRate = DisplayUtils.getScreenWidth() * 1f / loginMsg.screenWidth;
                        Global.heightRate = DisplayUtils.getScreenHeight() * 1f / loginMsg.screenHeight;
                    }
                }
            });
        });
    }
}
