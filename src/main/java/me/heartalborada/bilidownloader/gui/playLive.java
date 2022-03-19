package me.heartalborada.bilidownloader.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import me.heartalborada.bilidownloader.media.BilibiliInputStreamMedia;
import me.heartalborada.bilidownloader.utils.time;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.net.URL;
import java.util.*;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static me.heartalborada.bilidownloader.utils.live.*;
import static me.heartalborada.bilidownloader.utils.unicode.unicodeStr2String;
import static me.heartalborada.bilidownloader.utils.video.checkStrIsNum;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class playLive extends Application implements Initializable {
    @FXML
    private ImageView video01;
    @FXML
    private VBox box01;
    @FXML
    private TextField uid_in;
    @FXML
    private ChoiceBox<String> qncb;
    @FXML
    private ChoiceBox<String> servercb;

    public static Stage s;
    private static MediaPlayerFactory factory;
    private static EmbeddedMediaPlayer player;
    private Timeline an = new Timeline(new KeyFrame(Duration.millis(1000), e -> set01()));

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
        an.setCycleCount(Animation.INDEFINITE);
        factory=new MediaPlayerFactory();
        s=stage;
        player=factory.mediaPlayers().newEmbeddedMediaPlayer();
        player.events().addMediaPlayerEventListener(
                new MediaPlayerEventAdapter() {
                    @Override
                    public void playing(MediaPlayer mediaPlayer) {

                    }

                    @Override
                    public void paused(MediaPlayer mediaPlayer) {
                        an.stop();
                    }

                    @Override
                    public void stopped(MediaPlayer mediaPlayer) {
                        an.stop();
                    }

                    @Override
                    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                        an.play();
                    }
                }
        );
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("playLive.fxml")));
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

    public void set01(){
        //s.setTitle("1");
        s.setTitle("正在播放 - "+time.playTime(player.status().time()));
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

    private static long roomID;
    private static long uid;
    private static LinkedHashMap<String, Long> qnmap;
    private static LinkedHashMap<String, String> serverMap;
    public void getQn(){
        qncb.getItems().clear();
        String in=uid_in.getText();
        if(!checkStrIsNum(in)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.titleProperty().set("错误");
            alert.setHeaderText("你所输入的不是正确的UID");
            alert.showAndWait();
            return;
        }
        if(!hasLiveRoom(Long.parseLong(in))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.titleProperty().set("错误");
            alert.setHeaderText("该用户没有直播间");
            alert.showAndWait();
            return;
        }
        if(!isLiving(Long.parseLong(in))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.titleProperty().set("错误");
            alert.setHeaderText("该用户未开播");
            alert.showAndWait();
            return;
        }
        uid=Long.parseLong(in);
        roomID=getRoomId(Long.parseLong(in));
        qnmap = getLiveQn(roomID);
        Iterator<Map.Entry<String, Long>> iterator = qnmap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            qncb.getItems().add(entry.getKey());
        }
        qncb.getSelectionModel().select(0);
    }

    public void getServer(){
        servercb.getItems().clear();
        serverMap = getServerList(roomID,qnmap.get(qncb.getValue()));
        Iterator<Map.Entry<String, String>> iterator = serverMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            servercb.getItems().add(entry.getKey());
        }
        servercb.getSelectionModel().select(0);
    }

    public void play(){
        BilibiliInputStreamMedia r=new BilibiliInputStreamMedia(unicodeStr2String("https://xy222x91x206x247xy.mcdn.bilivideo.cn:4483/upgcxcode/87/98/504929887/504929887_nb2-1-32.flv?e=ig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1647684722&gen=playurlv2&os=mcdn&oi=1987528732&trid=000021cfb7f128fb492e9442e047a6d096d1u&platform=pc&upsig=86d95cb3a9bfffa946a8b875a80f6c08&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,platform&mcdnid=2001017&mid=0&bvc=vod&nettype=0&orderid=0,3&agrr=1&bw=74967&logo=A0000002"));
        //player.media().play(r," --network-caching=2000");
        player.media().play(unicodeStr2String(serverMap.get(servercb.getValue())));
        player.controls().setPosition(0.4f);
    }
}
