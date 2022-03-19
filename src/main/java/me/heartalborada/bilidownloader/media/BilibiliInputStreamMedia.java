package me.heartalborada.bilidownloader.media;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import uk.co.caprica.vlcj.media.callback.nonseekable.NonSeekableInputStreamMedia;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BilibiliInputStreamMedia extends NonSeekableInputStreamMedia {
    private static HttpURLConnection connection;
    private CloseableHttpResponse response = null;
    private CloseableHttpClient client = null;
    //private final HttpEntity entity;

    public BilibiliInputStreamMedia(String url) {
        try {
            URL uri = new URL(url);
            connection = (HttpURLConnection) uri.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
            connection.setDoInput(true);
            connection.setRequestProperty("referer", "https://www.bilibili.com");
            connection.setRequestProperty("Range", "bytes=0-");
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        List<? extends org.apache.http.Header> defaultHeaders = List.of(
                new BasicHeader("referer", "https://www.bilibili.com"),
                new BasicHeader("Range", "bytes=0-")
        );
        client = HttpClients.custom().setDefaultHeaders(defaultHeaders).build();
        HttpGet get = new HttpGet(url);
        try {
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        entity = response.getEntity();
         */
    }

    public BilibiliInputStreamMedia(String url, int ioBufferSize){
        super(ioBufferSize);
        /*
        List<? extends org.apache.http.Header> defaultHeaders = List.of(
                new BasicHeader("referer", "https://www.bilibili.com"),
                new BasicHeader("Range", "bytes=0-")
        );
        client = HttpClients.custom().setDefaultHeaders(defaultHeaders).build();
        HttpGet get = new HttpGet(url);
        try {
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        entity = response.getEntity();
         */
        try {
            URL uri = new URL(url);
            connection = (HttpURLConnection) uri.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
            connection.setDoInput(true);
            connection.setRequestProperty("referer", "https://www.bilibili.com");
            connection.setRequestProperty("Range", "bytes=0-");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected InputStream onOpenStream() throws IOException {
        System.out.println("start");
        return connection.getInputStream();
        //return entity.getContent();
        /*
        InputStream inputStream = null;
        URL url = new URL(this.url);
        connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
        connection.setDoInput(true);
        connection.setRequestProperty("referer", "https://www.bilibili.com");
        connection.setRequestProperty("Range", "bytes=0-");
        connection.connect();
        size = connection.getContentLength();
        inputStream = connection.getInputStream();
        return inputStream;*/
    }

    @Override
    protected void onCloseStream(InputStream inputStream) throws IOException {
        System.out.println("end");
        inputStream.close();
        connection.disconnect();
        //connection.disconnect();
    }

    @Override
    protected long onGetSize() {
        return connection.getContentLengthLong();
        //System.out.println(entity.getContentLength());
        //return entity.getContentLength();
    }
}
