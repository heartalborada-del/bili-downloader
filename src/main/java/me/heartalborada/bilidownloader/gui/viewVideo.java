package me.heartalborada.bilidownloader.gui;

import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.heartalborada.bilidownloader.main;
import me.heartalborada.bilidownloader.utils.download;
import me.heartalborada.bilidownloader.utils.internet;
import me.heartalborada.bilidownloader.utils.video;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static me.heartalborada.bilidownloader.utils.video.checkStrIsNum;
import static me.heartalborada.bilidownloader.utils.video.videoIsExist;

public class viewVideo extends Application implements Initializable {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("viewVideo.fxml")));
        primaryStage.setTitle("bilibili-video");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    @FXML
    private ImageView pic;
    @FXML
    private TextField id_input;
    @FXML
    private Label Vsize,VDspeed,title;
    @FXML
    private ChoiceBox vpl,video_page;
    @FXML
    private Button bt2,bt3;
    private static LinkedHashMap<String, Long> videoPagesMap;
    private static LinkedHashMap<String, Integer> videoQnMap;
    private static String videoid;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void clear(){
        bt2.setDisable(true);
        bt3.setDisable(true);
        vpl.getItems().clear();
        video_page.getItems().clear();
        title.setText("null");
        pic.imageProperty().set(null);
    }

    public void showSize(){
        /*
        String url= new video().getVideoUrl(videoid,videoPagesMap.get(video_page.getValue().toString()),videoQnMap.get(vpl.getValue().toString()));
        Vsize.setText("文件大小: "+(double)internet.getFileSize(url)/1024.0/1024.0+"MB");
        */
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
        title.setText(json.getAsJsonObject("data").get("title").getAsString());
        bt2.setDisable(false);
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
        bt3.setDisable(false);
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

    private static Thread t;
    public void download(){
        String url= new video().getVideoUrl(videoid,videoPagesMap.get(video_page.getValue().toString()),videoQnMap.get(vpl.getValue().toString()));
        //download.downVideo(url, "D:/", "TEST", "flv", Vsize, VDspeed);
        String sn="flv";
        if(16==videoQnMap.get(vpl.getValue().toString()))
            sn="mp4";
        String finalSn = sn;
        t=new Thread(() -> new download().downVideo(url,
                main.download_path,
                main.video_format.replace("${video_page}",video_page.getValue().toString()).replace("${video_name}",title.getText().split(" ")[0]),
                finalSn,

                Vsize,
                VDspeed));
        t.run();
    }
}
