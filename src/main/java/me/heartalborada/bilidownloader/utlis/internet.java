package me.heartalborada.bilidownloader.utlis;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class internet {
    public static class Url {
        public String doGet(String URL) {
            HttpURLConnection conn = null;
            InputStream is = null;
            BufferedReader br = null;
            StringBuilder result = new StringBuilder();
            try {
                //创建远程url连接对象
                URL url = new URL(URL);
                //通过远程url连接对象打开一个连接，强转成HTTPURLConnection类
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //设置连接超时时间和读取超时时间
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
                conn.setRequestProperty("Accept", "application/json");
                //发送请求
                conn.connect();
                //通过conn取得输入流，并使用Reader读取
                if (200 == conn.getResponseCode()) {
                    is = conn.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                        return line;
                    }
                } else {
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                conn.disconnect();
            }
            return result.toString();
        }
    }

    public static Object[] sendPost(String url, Map<String,Object> params) {
        BasicCookieStore cookieStore= new BasicCookieStore();
        String response = null;
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (String key : params.keySet()) {
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            CloseableHttpClient httpclient = null;
            CloseableHttpResponse httpresponse = null;
            try {
                httpclient = HttpClients.custom()
                        .setDefaultCookieStore(cookieStore)
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                                .build())
                        .build();
                HttpPost httppost = new HttpPost(url);
                // StringEntity stringentity = new StringEntity(data);
                if (pairs != null && pairs.size() > 0) {
                    httppost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
                }
                httpresponse = httpclient.execute(httppost);
                response = EntityUtils
                        .toString(httpresponse.getEntity());
            } finally {
                if (httpclient != null) {
                    httpclient.close();
                }
                if (httpresponse != null) {
                    httpresponse.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[]{response,cookieStore.getCookies()};
    }

    public String Eget(String uri) throws Exception{//Easy do get
        URL url = new URL(uri);
        URLConnection conn = url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
        conn.setDoInput(true);
        conn.setRequestProperty("Contect-Type","charset=UTF-8");
        conn.setRequestProperty("referer","https://www.bilibili.com");
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public String getWithCookie(String uri,String cookie) throws IOException {
        URL url = new URL(uri);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Cookie", cookie);
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
        conn.setDoInput(true);
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static void downVideo(String videoUrl, String downloadPath,String fileName, String SuffixName) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        //路径名加上文件名加上后缀名 = 整个文件下载路径
        String fullPathName = downloadPath+fileName+"."+SuffixName;

        try {
            // 1.获取连接对象
            URL url = new URL(videoUrl);
            // 获取链接对象，就是靠这个对象来获取流
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
            connection.setDoInput(true);
            connection.setRequestProperty("referer","https://www.bilibili.com");
            // Range代表读取的范围，bytes=0-代表从0字节到最大字节，意味着读取所有资源
            connection.setRequestProperty("Range", "bytes=0-");
            // 与网页建立链接，链接成功后就可以获得流；
            connection.connect();
            // 如果建立链接返回的相应代码是200到300间就为成功，否则链接失败,结束函数
            if (connection.getResponseCode() / 100 != 2) {
                System.out.println("连接失败...");
                return;
            }
            // 2.获取连接对象的流
            inputStream = connection.getInputStream();
            // 已下载的大小 下载进度
            int downloaded = 0;
            // 总文件的大小
            int fileSize = connection.getContentLength();
            // getFile获取此URL的文件名。返回的文件部分将与getPath（）相同,具体视频链接的文件名字视情况而定
            // String fileName = url.getFile();
            // fileName = fileName.substring(fileName.lastIndexOf("/") + 1);//特殊需要截取文件名字
            // 3.把资源写入文件
            randomAccessFile = new RandomAccessFile(fullPathName, "rw");
            while (downloaded < fileSize) {
                // 3.1设置缓存流的大小
                //判断当前剩余的下载大小是否大于缓存之，如果不大于就把缓存的大小设为剩余的。
                byte[] buffer = null;
                int MAX_BUFFER_SIZE=2048;
                if (fileSize - downloaded >= MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[fileSize - downloaded];
                }
                // 3.2把每一次缓存的数据写入文件
                int read = -1;
                int currentDownload = 0;
                long startTime = System.currentTimeMillis();
                // 这段代码是按照缓存的大小，读写该大小的字节。然后循环依次写入缓存的大小，直至结束。
                // 这样的优势在于，不用让硬件频繁的写入，可以提高效率和保护硬盘吧
                while (currentDownload < buffer.length) {
                    read = inputStream.read();
                    buffer[currentDownload++] = (byte) read;
                }
                long endTime = System.currentTimeMillis();
                double speed = 0.0; //下载速度
                if (endTime - startTime > 0) {
                    speed = currentDownload / 1024.0 / ((double) (endTime - startTime) / 1000);
                }
                randomAccessFile.write(buffer);
                downloaded += currentDownload;
                randomAccessFile.seek(downloaded);
                System.out.printf(fullPathName+"下载了进度:%.2f%%,下载速度：%.1fkb/s(%.1fM/s)%n", downloaded * 1.0 / fileSize * 10000 / 100,
                        speed, speed / 1000);
            }

        } catch (MalformedURLException e) {// 具体的异常放到前面
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭资源、连接
                connection.disconnect();
                inputStream.close();
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
