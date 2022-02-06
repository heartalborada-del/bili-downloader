package me.heartalborada.bilidownloader.gui;

import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import me.heartalborada.bilidownloader.utlis.video;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static me.heartalborada.bilidownloader.utlis.video.videoIsExist;

public class viewVideo extends Application implements Initializable {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("viewVideo.fxml")));
        primaryStage.setTitle("登录bilibili");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    @FXML
    private ImageView pic;
    @FXML
    private TextField id_input;
    @FXML
    private Label error_l;
    @FXML
    private MediaView videop;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void a(){
        String in=id_input.getText();
        JsonObject json;
        if(in.startsWith("BV")||in.startsWith("bv")){
            if(in.substring(2).equals("")||in.substring(2).length()<10){
                error_l.setText("错误信息: 视频ID输入错误");
                return;
            }
            json=video.getVideoJson(null,in);
        } else if(video.checkStrIsNum(in)){
            json=video.getVideoJson(in,null);
        } else {
            error_l.setText("错误信息: 视频ID输入错误");
            return;
        }
        if(!videoIsExist(json)){
            error_l.setText("ID:"+in+" 错误信息: 此视频不存在");
            return;
        }
        pic.setImage(new Image(video.getVideoPic(json)));
        MediaPlayer mp=new MediaPlayer(new Media("file:///D:/test.flv"));
        videop.setMediaPlayer(mp);
    }
}
