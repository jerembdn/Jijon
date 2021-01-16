package com.onruntime.jijon.util;

import java.io.FileFilter;

public class Files {

    public static FileFilter getConfigFilter() {
        return file -> {
            if(file.isDirectory()) return false;
            return file.getName().endsWith(".yml");
        };
    }
}
