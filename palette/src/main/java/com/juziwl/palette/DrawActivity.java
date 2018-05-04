package com.juziwl.palette;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.juziwl.palette.config.Global;
import com.juziwl.palette.netty.model.PushMsg;
import com.juziwl.palette.netty.server.NettyServerBootstrap;
import com.juziwl.palette.observer.ObserverableUtils;
import com.juziwl.palette.weight.PaletteView;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Army
 * @version V_1.0.0
 * @date 2018/5/3
 * @description
 */
public class DrawActivity extends AppCompatActivity implements Observer {

    private PaletteView paletteView;
    private Button resetBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        ObserverableUtils.getInstance().addObserver(this);
        paletteView = findViewById(R.id.palette);
        resetBtn = findViewById(R.id.reset_btn);
        if (!Global.isServer) {
            resetBtn.setVisibility(View.INVISIBLE);
            resetBtn.setEnabled(false);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof PushMsg) {
            if (paletteView != null) {
                PushMsg pushMsg = (PushMsg) o;
                paletteView.acceptOrder(pushMsg.eventType, pushMsg.x * Global.widthRate, pushMsg.y * Global.heightRate);
            }
        }

    }

    public void clear(View view) {
        paletteView.clear();
        PushMsg pushMsg = new PushMsg();
        pushMsg.eventType = PushMsg.ACTION_CLEAR;
        NettyServerBootstrap.getInstance().pushAll(pushMsg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObserverableUtils.getInstance().deleteObserver(this);
    }
}
