module me.heartalborada.bilibili.downloader.bilidownloader {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires org.apache.commons.codec;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.natives;

    exports me.heartalborada.bilidownloader.gui;
    opens me.heartalborada.bilidownloader.gui to javafx.fxml;
}