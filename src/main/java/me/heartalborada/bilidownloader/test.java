package me.heartalborada.bilidownloader;

import javafx.scene.control.Alert;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

public class test {

    public static void main(String[] args) {
        boolean found = new NativeDiscovery().discover();
        if(!found){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.titleProperty().set("错误");
            alert.headerTextProperty().set("未检测到VLC, 请检查VLC与Java版本是否同为32为或64位版本");
            alert.showAndWait();
            return;
        }
        System.out.println("OK");
    }
}

