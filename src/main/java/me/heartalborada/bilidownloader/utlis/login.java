package me.heartalborada.bilidownloader.utlis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;
import me.heartalborada.bilidownloader.main;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.cookie.Cookie;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;

public class login {
    private static String key="";

    public static String encrypt(String str) {
        //base64编码的公钥
        byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(key);
        RSAPublicKey pubKey = null;
        String outStr = null;

        try {
            pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //RSA加密
        return outStr;
    }

    private String getHashAndKey(){
        String data = null;
        try {
            data=new internet().Eget("http://passport.bilibili.com/login?act=getkey");
        } catch (Exception e) {
            e.printStackTrace();
        }
        key=JsonParser.parseString(data).getAsJsonObject().get("key").getAsString().replace("-----BEGIN PUBLIC KEY-----","").replace("-----END PUBLIC KEY-----","").replace("\n","");
        return JsonParser.parseString(data).getAsJsonObject().get("hash").getAsString();
    }

    public String getPw(String pw){
        String pw1= getHashAndKey()+pw;
        String tmp=null;
        try {
            tmp=encrypt(pw1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public String[] getCaptcha(){
        String data = null;
        try {
            data=new internet().Eget("https://passport.bilibili.com/web/captcha/combine?plat=6");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()==0) {
            json=json.getAsJsonObject("data").getAsJsonObject("result");
            return new String[]{
                    json.get("gt").getAsString(),
                    json.get("challenge").getAsString(),
                    json.get("key").getAsString()
            };
        }
        return null;
    }

    public static boolean checkIsLogin(){
        String data= null;
        try {
            data = new internet().getWithCookie(
                    "http://api.bilibili.com/x/web-interface/nav",
                    "SESSDATA="+ main.SESSDATA+"; bili_jct="+main.bili_jct
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data != null) {
            if(JsonParser.parseString(data).getAsJsonObject().get("code").getAsInt()==0){
                return true;
            }
        }
        return false;
    }

    public void doLogin(String acc,String pw,String[] captcha) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("captchaType",6);
        map.put("username",acc.toString());
        map.put("password",pw.toString());
        map.put("keep",true);
        map.put("key",captcha[0]);
        map.put("challenge",captcha[1]);
        map.put("validate",captcha[2]);
        map.put("seccode",captcha[3]);
        try {
            Object[] data=new internet().sendPost("http://passport.bilibili.com/web/login/v2",map);
            if (data != null) {
                if(JsonParser.parseString(String.valueOf(data[0])).getAsJsonObject().get("code").getAsInt()==0){
                    JsonObject json=new JsonObject();
                    @SuppressWarnings("unchecked")
                    List<Cookie> list= (List<Cookie>) data[1];
                    for(Cookie cookie:list){
                        if(cookie.getName().equals("SESSDATA")){
                            json.addProperty("SESSDATA",cookie.getValue());
                        }
                        if(cookie.getName().equals("bili_jct")){
                            json.addProperty("bili_jct",cookie.getValue());
                        }
                    }
                    new file().write(main.cookie_file_dir,json.toString());
                } else {
                    int code=JsonParser.parseString(String.valueOf(data[0])).getAsJsonObject().get("code").getAsInt();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.titleProperty().set("错误");
                    switch (code){
                        case -400:
                            alert.setHeaderText("错误代码: "+code+" 请求错误\n请联系开发者");
                            break;
                        case -629:
                            alert.setHeaderText("错误代码: "+code+"-账号或密码错误\n请检查你的账号或密码");
                            break;
                        case -653:
                            alert.setHeaderText("错误代码: "+code+"-用户名或密码不能为空\n请检查你的账号或密码");
                            break;
                        case -662:
                            alert.setHeaderText("错误代码: "+code+"-提交超时,请重新提交\n请重新打开验证窗口并提交");
                            break;
                        case -2001:
                            alert.setHeaderText("错误代码: "+code+"-缺少必要的的参数\n请联系开发者");
                            break;
                        case -2100:
                            alert.setHeaderText("错误代码: "+code+"-需验证手机号或邮箱\n请使用验证码登录或扫码登陆");
                            break;
                        case 2400:
                            alert.setHeaderText("错误代码: "+code+"-登录秘钥错误\n请联系开发者");
                            break;
                        case 2406:
                            alert.setHeaderText("错误代码: "+code+"-验证极验服务出错\n请检查\"validate\"和\"seccode\"是否填写正确");
                            break;
                        case 86000:
                            alert.setHeaderText("错误代码: "+code+"-RSA解密失败\n请联系开发者");
                            break;
                        default:
                            alert.setHeaderText("错误代码: "+code+"-未知错误\n请联系开发者");
                    }
                    me.heartalborada.bilidownloader.gui.captcha.close();
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
