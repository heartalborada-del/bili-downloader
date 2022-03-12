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
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import me.heartalborada.bilidownloader.utils.time;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.net.URL;
import java.util.*;

import static me.heartalborada.bilidownloader.utils.live.*;
import static me.heartalborada.bilidownloader.utils.unicode.unicodeStr2String;
import static me.heartalborada.bilidownloader.utils.video.checkStrIsNum;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class playVideo extends Application implements Initializable {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.titleProperty().set("错误");
            alert.setHeaderText("您未安装VLC\n若你已安装VLC请检查版本是否为3.x\n注意: java为64位VLC，需安装64位版本；32位同");
            alert.showAndWait();
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

    public void set01(){
        //s.setTitle("1");
        s.setTitle("正在播放 - "+time.playTime(player.status().time()));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        video01.setPreserveRatio(true);
        player.videoSurface().set(videoSurfaceForImageView(this.video01));
        s.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            box01.setMaxHeight(s.getWidth());
            video01.setFitWidth(s.getWidth());
        });

        s.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            box01.setPrefHeight(newValue.intValue());
            video01.setFitHeight(newValue.intValue()-65);
            System.out.println(newValue);
        });
    }

    private static void stopPlay(){
        player.controls().stop();
        player.release();
        factory.release();
    }

    private static long roomID;
    private static LinkedHashMap<String, Long> qnmap;
    private static LinkedHashMap<String, String> serverMap;
    public void getQn(){
        String in=uid_in.getText();
        if(!checkStrIsNum(in)){
            System.out.println("1");
            return;
        }
        if(!hasLiveRoom(Long.parseLong(in))){
            System.out.println("2");
            return;
        }
        if(!isLiving(Long.parseLong(in))){
            System.out.println("3");
            return;
        }
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
        serverMap = getServerList(roomID,qnmap.get(qncb.getValue()));
        Iterator<Map.Entry<String, String>> iterator = serverMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            servercb.getItems().add(entry.getKey());
        }
        servercb.getSelectionModel().select(0);
    }

    public void play(){
        player.media().play(unicodeStr2String(serverMap.get(servercb.getValue())));
        player.controls().setPosition(0.4f);
    }
}
