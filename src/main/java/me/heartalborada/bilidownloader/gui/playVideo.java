package me.heartalborada.bilidownloader.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class playVideo extends Application implements Initializable {
    @FXML
    private ImageView video01;

    private static MediaPlayerFactory factory;
    private static EmbeddedMediaPlayer player;
    public static void main(String arg[]){
        launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        factory=new MediaPlayerFactory();
        player=factory.mediaPlayers().newEmbeddedMediaPlayer();
        player.events().addMediaPlayerEventListener(
                new MediaPlayerEventAdapter() {
                    @Override
                    public void playing(MediaPlayer mediaPlayer) {
                        System.out.println("playing");
                    }

                    @Override
                    public void paused(MediaPlayer mediaPlayer) {
                        System.out.println("paused");
                    }

                    @Override
                    public void stopped(MediaPlayer mediaPlayer) {
                        System.out.println("stopped");
                    }

                    @Override
                    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                        System.out.println("CHANGE");
                    }
                }
        );
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("playVideo.fxml")));
        stage.setTitle("videoView");
        stage.setScene(new Scene(root));
        stage.show();
        //windows close
        stage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
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
    }

    private static void stopPlay(){
        player.controls().stop();
        player.release();
        factory.release();
    }

    public void play(){
        //player.media().play("D:\\Project\\bili-downloader\\download\\02.flv");
        player.media().play("https://pan.loliurl.club/api/v3/file/source/329/portal3%E5%AE%9E%E6%9C%BA%E6%BC%94%E7%A4%BA.mp4?sign=iqmP6Wlfd8ZQHZs4FURspEGVEDgYZRp_WG55yuVELKc%3D%3A0");
        player.controls().setPosition(0.4f);
    }
}
