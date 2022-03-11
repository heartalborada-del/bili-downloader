package me.heartalborada.bilidownloader.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.heartalborada.bilidownloader.utils.login;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static me.heartalborada.bilidownloader.gui.login.flag;
import static me.heartalborada.bilidownloader.gui.login.key;

public class captcha extends Application implements Initializable {

    private static Stage stage;
    @FXML
    private WebView web;
    @FXML
    private TextField gt, challenge, seccode, validate;

    public static void main(String[] args) {
        launch(args);
    }

    public static void close() {
        stage.close();
    }

    @Override
    public void start(Stage stage1) throws IOException {
        String[] tmp = new String[3];
        if (flag == 0) {
            tmp = login.password.getCaptcha();
        } else if (flag == 1) {
            tmp = login.sms.getCaptcha();
        }
        me.heartalborada.bilidownloader.gui.login.gt = tmp[0];
        me.heartalborada.bilidownloader.gui.login.challenge = tmp[1];
        key = tmp[2];
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("captcha.fxml")));
        stage = stage1;
        stage.setScene(new Scene(root));
        stage.setTitle("加载页面中...");
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WebEngine engine = web.getEngine();
        engine.getLoadWorker().stateProperty()
                .addListener(new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            stage.setTitle("加载完毕");
                        }
                    }
                });
        engine.load("https://www.scraft.top/gtv/");
        gt.setText(me.heartalborada.bilidownloader.gui.login.gt);
        challenge.setText(me.heartalborada.bilidownloader.gui.login.challenge);
    }

    public void bt_click(ActionEvent event) {
        if (!(validate.getText().equals("") || seccode.getText().equals(""))) {
            me.heartalborada.bilidownloader.gui.login.validate = validate.getText();
            me.heartalborada.bilidownloader.gui.login.seccode = seccode.getText();
            close();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.titleProperty().set("警告");
            alert.headerTextProperty().set("请填写\"validate\"和\"seccode\"");
            alert.showAndWait();
        }
    }
}
