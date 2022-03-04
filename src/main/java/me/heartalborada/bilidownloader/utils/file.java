package me.heartalborada.bilidownloader.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class file {
    public void write(String path, String data) throws IOException {
        File f = new File(path.substring(0, path.lastIndexOf("/") + 1));
        if (!f.exists()) {
            f.mkdirs();
        }
        File f1 = new File(path);
        if (!f1.exists()) {
            f1.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(f1, false);
        fos.write(data.getBytes(StandardCharsets.UTF_8));
        fos.flush();
        fos.close();
    }

    public String read(String path) throws IOException {
        File f = new File(path.substring(0, path.lastIndexOf("/") + 1));
        if (!f.exists()) {
            f.mkdirs();
        }
        File f1 = new File(path);
        if (!f1.exists()) {
            f1.createNewFile();
        }
        File file = new File(path);
        StringBuilder stringBuilder = new StringBuilder();
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            //process the line
            stringBuilder.append(line);
            stringBuilder.append("\n\r");
        }
        br.close();
        return stringBuilder.toString();
    }

    public String read_res(String path) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream(path))));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public String read_res(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}

