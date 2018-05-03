package com.juziwl.palette.observer;

import com.juziwl.palette.netty.model.BaseMsg;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Army
 * @version V_1.0.0
 * @date 2018/5/3
 * @description
 */
public class ObserverableUtils extends Observable {


    private ObserverableUtils() {
    }

    private static class SingletonHolder {
        private static ObserverableUtils instance = new ObserverableUtils();
    }

    public static ObserverableUtils getInstance() {
        return SingletonHolder.instance;
    }

    public void notifyObserversChanged(BaseMsg baseMsg) {
        setChanged();
        notifyObservers(baseMsg);
    }

}
