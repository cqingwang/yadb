/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/9/12
 * Created by chan on 2023/9/12
 */


package com.sogou.yarn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static void writeTo(File file, String text) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(text);
            writer.close();
            System.out.println("layout dumped to:" + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
