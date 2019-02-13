package com.example.demo;

import java.util.Random;

/**
 * Project: ProjectBaseDiffAdapter
 * Author: LiShen
 * Time: 2019/2/13 12:07
 */
public class DemoUtil {
    private static volatile int ID = 10999;
    private static volatile int ICON_INDEX = 19;

    private static final String[] RANDOM_CHARS = new String[]{
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
            "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "!", "@", "#", "$", "%", "^", "&", "*"};

    private static final int[] RANDOM_ICONS = new int[]{
            R.mipmap.pic_girl_1,
            R.mipmap.pic_girl_2,
            R.mipmap.pic_girl_3,
            R.mipmap.pic_girl_4,
            R.mipmap.pic_girl_5,
            R.mipmap.pic_girl_6,
            R.mipmap.pic_girl_7,
            R.mipmap.pic_girl_8,
            R.mipmap.pic_girl_9,
            R.mipmap.pic_girl_10,
            R.mipmap.pic_girl_11,
            R.mipmap.pic_girl_12,
            R.mipmap.pic_girl_13,
            R.mipmap.pic_girl_14,
            R.mipmap.pic_girl_15,
            R.mipmap.pic_girl_17,
            R.mipmap.pic_girl_18,
            R.mipmap.pic_girl_19,
            R.mipmap.pic_girl_20
    };

    public static String generateRandomString() {
        Random random = new Random();
        int length = random.nextInt(100);
        StringBuilder fileNameBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            fileNameBuilder.append(
                    RANDOM_CHARS[random.nextInt(RANDOM_CHARS.length - 1)]);
        }
        return fileNameBuilder.toString();
    }

    public synchronized static int generateRandomIcon() {
        ICON_INDEX++;
        return RANDOM_ICONS[ICON_INDEX % 19];
    }

    public synchronized static int generateId() {
        ID--;
        return ID;
    }
}