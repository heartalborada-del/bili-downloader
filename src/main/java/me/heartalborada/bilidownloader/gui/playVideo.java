package me.heartalborada.bilidownloader.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.heartalborada.bilidownloader.media.BilibiliInputStreamMedia;
import me.heartalborada.bilidownloader.utils.video;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static me.heartalborada.bilidownloader.utils.unicode.unicodeStr2String;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class playVideo extends Application implements Initializable {
    @FXML
    private ImageView video01;
    @FXML
    private VBox box01;

    public static Stage s;
    private static MediaPlayerFactory factory;
    private static EmbeddedMediaPlayer player;

    public static void main(String arg[]){
        launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        if(!(new NativeDiscovery().discover())){
            Alert alert = new Alert(CONFIRMATION);
            alert.setTitle("错误");
            alert.setHeaderText("您未安装VLC\n若你已安装VLC请检查版本是否为3.x\n注意: java为64位VLC，需安装64位版本；32位同");
            alert.setContentText("点击\"下载\"开始下载VLC\n点击\"取消\"取消启动");
            ButtonType download = new ButtonType("下载");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(download, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == download) {
                if (java.awt.Desktop.isDesktopSupported()) {
                    try {
                        // 创建一个URI实例
                        java.net.URI uri = java.net.URI.create("https://www.videolan.org/vlc/");
                        // 获取当前系统桌面扩展
                        java.awt.Desktop dp = java.awt.Desktop.getDesktop() ;
                        // 判断系统桌面是否支持要执行的功能
                        if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                            // 获取系统默认浏览器打开链接
                            dp.browse( uri ) ;
                        } else {
                            System.out.println("无法拉起浏览器，你可以到\"https://www.videolan.org/vlc/\"去下载最新版本的VLC");
                        }
                    } catch (Exception e) {
                        e.printStackTrace() ;
                    }
                }
            } else {
                System.out.println("你可以到\"https://www.videolan.org/vlc/\"去下载最新版本的VLC");
            }
            return;
        }
        factory=new MediaPlayerFactory();
        s=stage;
        player=factory.mediaPlayers().newEmbeddedMediaPlayer();
        player.events().addMediaPlayerEventListener(
                new MediaPlayerEventAdapter() {
                    @Override
                    public void playing(MediaPlayer mediaPlayer) {
                        System.out.print("play");
                        System.out.println(mediaPlayer.status().length()+"--"+mediaPlayer.status().time());
                    }

                    @Override
                    public void paused(MediaPlayer mediaPlayer) {
                        System.out.println("pause");
                    }

                    @Override
                    public void stopped(MediaPlayer mediaPlayer) {
                        System.out.print("stop");
                        System.out.println(mediaPlayer.status().length()+"--"+mediaPlayer.status().time());
                    }

                    @Override
                    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                        System.out.print("change");
                        System.out.println(mediaPlayer.status().length()+"--"+mediaPlayer.status().time());
                    }
                }
        );
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("playVideo.fxml")));
        stage.setTitle("videoView");
        stage.setScene(new Scene(root));
        stage.show();
        //windows close
        stage.setOnCloseRequest(
                new EventHandler<>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {
                        stopPlay();
                    }
                }
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        video01.setPreserveRatio(true);
        player.videoSurface().set(videoSurfaceForImageView(this.video01));
        s.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            box01.setPrefWidth(s.getWidth()-15);
            video01.setFitWidth(s.getWidth()-15);
        });

        s.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            box01.setPrefHeight(newValue.intValue());
            video01.setFitHeight(newValue.intValue()-60);
            System.out.println(newValue);
        });
    }

    private static void stopPlay(){
        player.controls().stop();
        player.release();
        factory.release();
    }

    public void play() throws IOException {
        String s= new video().getVideoUrl(String.valueOf(636483599),504929887,64);
        System.out.println(unicodeStr2String(s));
        BilibiliInputStreamMedia r=new BilibiliInputStreamMedia(unicodeStr2String(s),4096);
        player.media().play(r," --network-caching=300");
        //player.media().play("D:\\Project\\bili-downloader\\download\\02.flv");
        //player.controls().setPosition(0.4f);
    }
}
