package com.juziwl.palette.netty.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 徐飞
 * @version 2016/02/24 19:48
 */
public class NettyChannelMap {
    private static Map<String, SocketChannel> map = new ConcurrentHashMap<>();

    public static void add(String clientId, SocketChannel socketChannel) {
        map.put(clientId, socketChannel);
    }

    public static Channel get(String clientId) {
        return map.get(clientId);
    }

    public static List<SocketChannel> getAll() {
        List<SocketChannel> channelList = new ArrayList<>();
        for (Map.Entry entry : map.entrySet()) {
            channelList.add((SocketChannel) entry.getValue());
        }
        return channelList;
    }

    public static void remove(String clientId) {
        System.out.println("通道" + clientId + "已被移除。");
        map.remove(clientId);
    }

    public static void remove(SocketChannel socketChannel) {

        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() == socketChannel) {
                String key = (String) entry.getKey();
                System.out.println("通道" + key + "已被移除。");
                map.remove(key);
            }
        }
    }

}
