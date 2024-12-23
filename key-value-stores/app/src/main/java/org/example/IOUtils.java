package org.example;

import java.io.File;

public class IOUtils {

    public static long getDirectorySize(File directory) {
        long size = 0;
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }
}
