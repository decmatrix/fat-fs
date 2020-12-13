package com.bsokolovskyi;

import com.bsokolovskyi.controller.Manager;
import com.bsokolovskyi.fs.FAT16;

public class Startup {
    public static void main(String[] args) {
        FAT16 fs = new FAT16(50, 3);

        Manager.createManager(fs).run();
    }
}
