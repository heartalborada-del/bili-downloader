package me.heartalborada.bilidownloader.gui;

import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.heartalborada.bilidownloader.utlis.download;
import me.heartalborada.bilidownloader.utlis.video;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static me.heartalborada.bilidownloader.utlis.video.checkStrIsNum;
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
    private Label Vsize,VDspeed;
    @FXML
    private ChoiceBox vpl,video_page;

    private static LinkedHashMap<String, Long> videoPagesMap;
    private static LinkedHashMap<String, Integer> videoQnMap;
    private static String videoid;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void a(){
        videoid=id_input.getText();
        JsonObject json;
        if(videoid.startsWith("BV")||videoid.startsWith("bv")){
            if(videoid.substring(2).equals("")||videoid.substring(2).length()<10){
                return;
            }
            videoid=video.BVidToAid(videoid);
        } else if(!checkStrIsNum(videoid)){
            return;
        }
        json=video.getVideoJson(videoid);
        if(!videoIsExist(json)){
            return;
        }
        pic.setImage(new Image(video.getVideoPic(json)));
        showVideoPages(videoid);
    }

    public void showVideoRes(){
        vpl.getItems().clear();
        videoQnMap=video.getQuality(videoid,videoPagesMap.get(video_page.getValue().toString()));
        Iterator<Map.Entry<String, Integer>> iterator= videoQnMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<String, Integer> entry = iterator.next();
            vpl.getItems().add(entry.getKey());
        }
        vpl.getSelectionModel().select(0);
    }

    public void showVideoPages(String aid){
        videoPagesMap=video.getCidList(aid);
        Iterator<Map.Entry<String, Long>> iterator= videoPagesMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<String, Long> entry = iterator.next();
            video_page.getItems().add(entry.getKey());
        }
        video_page.getSelectionModel().select(0);
    }

    public void download(){
        String url= new video().getVideoUrl(videoid,videoPagesMap.get(video_page.getValue().toString()),videoQnMap.get(vpl.getValue().toString()));
        download.downVideo(url, "D:/", "TEST", "flv", Vsize, VDspeed);
    }
}
