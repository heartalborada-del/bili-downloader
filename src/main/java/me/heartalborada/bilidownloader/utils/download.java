package me.heartalborada.bilidownloader.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class download {
    public void downVideo(String videoUrl, String downloadPath, String fileName, String SuffixName, Label l1, Label L2) {
        File F1= new File(downloadPath);
        if(!F1.exists()){
            F1.mkdirs();
        }
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        String fullPathName = downloadPath+fileName+"."+SuffixName;
        try {
            URL url = new URL(videoUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
            connection.setDoInput(true);
            connection.setRequestProperty("referer","https://www.bilibili.com");
            connection.setRequestProperty("Range", "bytes=0-");
            connection.connect();
            if (connection.getResponseCode() / 100 != 2) {
                System.out.println("连接失败...");
                return;
            }
            inputStream = connection.getInputStream();
            int downloaded = 0;
            int fileSize = connection.getContentLength();
            randomAccessFile = new RandomAccessFile(fullPathName, "rw");
            l1.setText("文件大小: "+(double)fileSize/1000.00/1000.00+"MB");
            while (downloaded < fileSize) {
                byte[] buffer = null;
                int MAX_BUFFER_SIZE=4096;
                if (fileSize - downloaded >= MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[fileSize - downloaded];
                }
                int read = -1;
                int currentDownload = 0;
                long startTime = System.currentTimeMillis();
                while (currentDownload < buffer.length) {
                    read = inputStream.read();
                    buffer[currentDownload++] = (byte) read;
                }
                long endTime = System.currentTimeMillis();
                double speed = 0.0;
                if (endTime - startTime > 0) {
                    speed = currentDownload / 1024.0 / ((double) (endTime - startTime) / 1000);
                }
                randomAccessFile.write(buffer);
                downloaded += currentDownload;
                randomAccessFile.seek(downloaded);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.disconnect();
                inputStream.close();
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.titleProperty().set("信息");
            alert.setHeaderText(fileName+" 下载完毕");
            alert.show();
        }
    }

    public void t01(){

    }
}
