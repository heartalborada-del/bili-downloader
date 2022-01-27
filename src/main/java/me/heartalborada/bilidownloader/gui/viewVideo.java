package me.heartalborada.bilidownloader.gui;

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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void a(){
        String tmp=id_input.getText();
        if(tmp.startsWith("BV")||tmp.startsWith("bv")){
            if(tmp.substring(2).equals("")){
                error_l.setText("错误信息: BV号输入错误");
                return;
            }
            tmp = String.valueOf(video.BVidToAid(tmp));
        }
        if(tmp.equals("404")){
            error_l.setText("错误信息: 此视频不存在");
            return;
        }
        if(video.checkStrIsNum(tmp)) {
            if(!videoIsExist(Long.parseLong(tmp))){
                error_l.setText("错误信息: 此视频不存在");
                return;
            }
            pic.setImage(new Image(video.getVideoPic(Long.parseLong(tmp))));
        }
    }
}
