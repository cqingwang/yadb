/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/9/12
 * Created by chan on 2023/9/12
 */


package com.sogou.yarn.wrappers.layout;

import android.annotation.SuppressLint;
import android.app.IUiAutomationConnection;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;

import com.sogou.yarn.DisplayInfo;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

@SuppressLint("SoonBlockedPrivateApi")
public class InspectSpec {
    private final HandlerThread handler = new HandlerThread("LayoutShellThread");
    private UiAutomation inspectSpec = null;

    public void init_connect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (inspectSpec != null) return;
        if (handler.isAlive()) {
            throw new IllegalStateException("Already connected!");
        }
        handler.start();
        inspectSpec = UiAutomation.class.getConstructor(Looper.class, IUiAutomationConnection.class).newInstance(handler.getLooper(), new UiAutomationConnection());
        UiAutomation.class.getDeclaredMethod("connect").invoke(inspectSpec);
    }

    public void disconnect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!handler.isAlive()) {
            throw new IllegalStateException("Already disconnected!");
        }
        UiAutomation.class.getDeclaredMethod("disconnect").invoke(inspectSpec);
        handler.quit();
    }

    private String formatXml(String xml) {
        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }


    public boolean inspect(File file, DisplayInfo displayInfo) {
        try {
            init_connect();
            AccessibilityNodeInfo info=null;
            long startTime = System.currentTimeMillis();
            do {
                if (System.currentTimeMillis() - startTime >= 5000) {
                    new TimeoutException().printStackTrace();
                    break;
                }
                info = inspectSpec.getRootInActiveWindow();
            } while (info == null);
            String content = AccessibilityNodeInfoDumper.getWindowXMLHierarchy(info, displayInfo);
            String text = formatXml(content);

            FileWriter writer = new FileWriter(file);
            writer.write(text);
            writer.close();
            System.out.println("layout dumped to:" + file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}