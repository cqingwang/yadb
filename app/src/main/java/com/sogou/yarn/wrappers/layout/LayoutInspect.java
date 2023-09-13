package com.sogou.yarn.wrappers.layout;

import android.annotation.SuppressLint;

import com.sogou.yarn.DisplayInfo;

import java.io.File;
import java.net.ServerSocket;

@SuppressLint("SoonBlockedPrivateApi")
public class LayoutInspect {
    public static void inspect(File file, DisplayInfo displayInfo) {
        InspectSpec shell = new InspectSpec();
        shell.inspect(file, displayInfo);
    }

    public static void listen(File file, DisplayInfo displayInfo) throws Exception {
        assertTask(); //确保只启动一个,使用端口占用的方式占坑
        while (true) {
            new InspectSpec().inspect(file, displayInfo);
            sleep(100);
        }
    }

    private static void assertTask() throws Exception {
        int port = 6550;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Listen:" + port + ", 启动成功");
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
