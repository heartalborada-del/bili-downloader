package me.heartalborada.bilidownloader.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.UnaryOperator;

import static me.heartalborada.bilidownloader.utlis.login.sms.getSmsLocationMap;

public class login extends Application implements Initializable{
    protected static String gt=null,challenge=null,validate=null,seccode,key=null;
    //sms begin
    protected static String captcha_key;
    protected static LinkedHashMap<String, Integer> map;
    //sms end
    private static Stage stage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        primaryStage.setTitle("登录bilibili");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        stage= primaryStage;
    }

    @FXML
    private TextField acc_input,phone_number,sms_code;
    @FXML
    private PasswordField pw_input;
    @FXML
    private ChoiceBox<String> sms_choice;
    @FXML
    private VBox password,sms,qr;
    public static int flag=0;//flag 为判断登录方式变量

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        map= getSmsLocationMap();
        Iterator<Map.Entry<String, Integer>> iterator= map.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<? extends String, ? extends Integer> entry = iterator.next();
            //System.out.println(entry.getKey()+":"+entry.getValue());
            sms_choice.getItems().add(entry.getKey().toString());
        }
        sms_choice.getSelectionModel().select(0);
        phone_number.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("[0-9]*")) {
                long value = Long.parseLong(newValue);
            } else {
                phone_number.setText(oldValue);
            }
        });
        sms_code.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) {
                long value = Long.parseLong(newValue);
            } else {
                sms_code.setText(oldValue);
            }
        });
    }

    public void getCaptcha(ActionEvent event){
        try {
            new captcha().start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSmsCode(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.titleProperty().set("错误");
        if(login.key == null || login.challenge == null || login.validate == null || login.seccode == null) {
            alert.setHeaderText("未获取人机验证验证码/获取失败");
            alert.showAndWait();
            return;
        }
        int cid= map.get(sms_choice.getValue());
        long pn=Long.parseLong(phone_number.getText());
        String tmp = me.heartalborada.bilidownloader.utlis.login.sms.SendSmsCaptcha(
                pn,
                cid,
                new String[]{
                    login.key,
                    login.challenge,
                    login.validate,
                    login.seccode
        });
        if(!tmp.equals("")){
            captcha_key=tmp;
        }
    }

    public void changeLoginMode(ActionEvent event){
        String bu_id = ((Button) event.getSource()).getId();
        if(bu_id.equals("pw_login")){
            flag=0;
            password.setVisible(true);
            sms.setVisible(false);
            qr.setVisible(false);
        } else if(bu_id.equals("sms_login")){
            flag=1;
            password.setVisible(false);
            sms.setVisible(true);
            qr.setVisible(false);
        } else{
            flag=2;
            password.setVisible(false);
            sms.setVisible(false);
            qr.setVisible(true);
        }
    }

    public static void close(){
        stage.close();
    }

    public void doLogin(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.titleProperty().set("错误");
        if(login.key == null || login.challenge == null || login.validate == null || login.seccode == null) {
            alert.setHeaderText("未获取人机验证验证码/获取失败");
            alert.showAndWait();
            return;
        }
        if(flag==0) {
            String acc=null,pw_none=null;
            acc=acc_input.getText();
            pw_none=pw_input.getText();
            if(pw_none.equals("")){
                alert.setHeaderText("你还没有填写密码");
                alert.show();
                return;
            }
            if(acc.equals("")){
                alert.setHeaderText("你还没有填写手机号/邮箱");
                alert.show();
                return;
            }
            String pw = me.heartalborada.bilidownloader.utlis.login.password.getPw(pw_none);
            me.heartalborada.bilidownloader.utlis.login.password.doLogin(
                    acc,
                    pw,
                    new String[]{
                            me.heartalborada.bilidownloader.gui.login.key,
                            me.heartalborada.bilidownloader.gui.login.challenge,
                            me.heartalborada.bilidownloader.gui.login.validate,
                            me.heartalborada.bilidownloader.gui.login.seccode
                    });
        } else if (flag==1){
            if(phone_number.getText().equals("")){
                alert.setHeaderText("您还没输入手机号");
                alert.showAndWait();
                return;
            }
            if(sms_code.getText().equals("")){
                alert.setHeaderText("您还没输入短信验证码");
                alert.showAndWait();
                return;
            }
            int cid= map.get(sms_choice.getValue());
            long pn=Long.parseLong(phone_number.getText());
            long code=Long.parseLong(sms_code.getText());
            me.heartalborada.bilidownloader.utlis.login.sms.doLogin(cid,pn,code,captcha_key);
        }
    }
}
