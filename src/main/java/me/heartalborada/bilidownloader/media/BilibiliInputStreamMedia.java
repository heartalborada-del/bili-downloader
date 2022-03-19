package me.heartalborada.bilidownloader.media;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import uk.co.caprica.vlcj.media.callback.nonseekable.NonSeekableInputStreamMedia;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class BilibiliInputStreamMedia extends NonSeekableInputStreamMedia {
    private final String url;
    private static HttpURLConnection connection = null;
    private static long size;

    public BilibiliInputStreamMedia(String url) {
        this.url = url;
    }

    public BilibiliInputStreamMedia(String url, int ioBufferSize){
        super(ioBufferSize);
        this.url = url;
    }

    @Override
    protected InputStream onOpenStream() throws IOException {
        List<? extends org.apache.http.Header> defaultHeaders = List.of(
                new BasicHeader("referer", "https://www.bilibili.com")
        );
        CloseableHttpClient client = HttpClients.custom().setDefaultHeaders(defaultHeaders).build();
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        size=entity.getContentLength();
        return entity.getContent();
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
        inputStream.close();
        //connection.disconnect();
    }

    @Override
    protected long onGetSize() {
        return size;
    }
}
