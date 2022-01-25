package me.heartalborada.bilidownloader.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class login extends Application implements Initializable{
    protected static String gt,challenge,validate,seccode,key;
    protected static String acc,pw_none;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        primaryStage.setTitle("登录bilibili");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    private TextField acc_input;
    @FXML
    private PasswordField pw_input;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void bt_click(ActionEvent event){
        acc=acc_input.getText();
        pw_none=pw_input.getText();
        try {
            new captcha().start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
