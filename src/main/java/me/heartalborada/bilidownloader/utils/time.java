package me.heartalborada.bilidownloader.utils;

import java.text.SimpleDateFormat;

public class time {
    public static String playTime(Long playtime){
        SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss"); //设置格式
        String timeText=format.format(playtime+57600000);
        return timeText;
    };
}
