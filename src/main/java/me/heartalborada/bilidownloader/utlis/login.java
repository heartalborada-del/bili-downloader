package me.heartalborada.bilidownloader.utlis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.LinkedHashMap;
import java.util.List;

import static me.heartalborada.bilidownloader.utlis.internet.sendPost;

public class login {

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

    public static class password{
        private static String key="";

        private static String encrypt(String str) {
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

        private static String getHashAndKey(){
            String data = null;
            try {
                data=new internet().Eget("http://passport.bilibili.com/login?act=getkey");
            } catch (Exception e) {
                e.printStackTrace();
            }
            key=JsonParser.parseString(data).getAsJsonObject().get("key").getAsString().replace("-----BEGIN PUBLIC KEY-----","").replace("-----END PUBLIC KEY-----","").replace("\n","");
            return JsonParser.parseString(data).getAsJsonObject().get("hash").getAsString();
        }

        public static String[] getCaptcha(){
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

        public static String getPw(String pw){
            String pw1= getHashAndKey()+pw;
            String tmp=null;
            try {
                tmp=encrypt(pw1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tmp;
        }

        public static void doLogin(String acc, String pw, String[] captcha) {
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
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.titleProperty().set("信息");
                        alert.setHeaderText("登录成功");
                        alert.showAndWait();
                        me.heartalborada.bilidownloader.gui.login.close();
                        me.heartalborada.bilidownloader.gui.captcha.close();
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
                                alert.setContentText("验证链接: "+JsonParser.parseString(String.valueOf(data[0])).getAsJsonObject().get("data").getAsString());
                                break;
                            case -2001:
                                alert.setHeaderText("错误代码: "+code+"-缺少必要的的参数\n请联系开发者");
                                break;
                            case -2110:
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
                        alert.showAndWait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class sms{
        public static LinkedHashMap<String,Integer> getSmsLocationMap(){
            String data= null;
            try {
                data = new internet().Eget("http://passport.bilibili.com/web/generic/country/list");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObject json= JsonParser.parseString(data).getAsJsonObject().get("data").getAsJsonObject();
            LinkedHashMap<String,Integer> map=new LinkedHashMap<>();
            JsonArray array=json.get("common").getAsJsonArray();
            for(JsonElement o:array){
                JsonObject json1= o.getAsJsonObject();
                map.put(json1.get("cname").getAsString(),json1.get("country_id").getAsInt());
            }
            array=json.get("others").getAsJsonArray();
            for(JsonElement o:array){
                JsonObject json1= o.getAsJsonObject();
                map.put(json1.get("cname").getAsString(),json1.get("country_id").getAsInt());
            }
            return map;
        }

        public static String[] getCaptcha(){
            String data = null;
            try {
                data=new internet().Eget("https://passport.bilibili.com/x/passport-login/captcha?source=main_web");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObject json = JsonParser.parseString(data).getAsJsonObject();
            if(json.get("code").getAsInt()==0) {
                JsonObject res=json.getAsJsonObject("data");
                return new String[]{
                        res.getAsJsonObject("geetest").get("gt").getAsString(),
                        res.getAsJsonObject("geetest").get("challenge").getAsString(),
                        res.get("token").getAsString()
                };
            }
            return null;
        }

        public static String SendSmsCaptcha(long phone_num, int cid, String[] captcha){
            HashMap<String,Object> map=new HashMap<>();
            map.put("tel",phone_num);
            map.put("cid",cid);
            map.put("source","main_web");
            map.put("token",captcha[0]);
            map.put("challenge",captcha[1]);
            map.put("validate",captcha[2]);
            map.put("seccode",captcha[3]);
            Object[] data=sendPost("http://passport.bilibili.com/x/passport-login/web/sms/send",map);
            JsonObject json= JsonParser.parseString(String.valueOf(data[0])).getAsJsonObject();
            if(json.get("code").getAsInt()==0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.titleProperty().set("信息");
                alert.headerTextProperty().set("短信验证码已发送");
                alert.showAndWait();
                return json.getAsJsonObject("data").get("captcha_key").getAsString();
            } else {
                int code=json.get("code").getAsInt();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.titleProperty().set("错误");
                switch (code){
                    case -400:
                        alert.setHeaderText("错误代码: "+code+" -请求错误\n请联系开发者");
                        break;
                    case 1002:
                        alert.setHeaderText("错误代码: "+code+" -手机号格式错误\n请检查你的手机号和国家或区域是否填写正确");
                        break;
                    case 86203:
                        alert.setHeaderText("错误代码: "+code+" -短信发送次数已达上限\n请稍后重试");
                        break;
                    case 1003:
                        alert.setHeaderText("错误代码: "+code+" -验证码已经发送\n请稍后重试");
                        break;
                    case 1025:
                        alert.setHeaderText("错误代码: "+code+" -该手机号在哔哩哔哩有过永久封禁记录, 无法再次注册或绑定新账号\n请联系哔哩哔哩");
                        break;
                    case 2400:
                        alert.setHeaderText("错误代码: "+code+" -登录秘钥错误\n请联系开发者");
                        break;
                    case 2404:
                        alert.setHeaderText("错误代码: "+code+" -验证极验服务出错\n请检查\"validate\"和\"seccode\"是否填写正确");
                        break;
                    default:
                        alert.setHeaderText("错误代码: "+code+" -未知错误\n请联系开发者");
                }
                alert.showAndWait();
            }
            return "";
        }

        public static void doLogin(int cid, long phone_num, long sms_code,String captcha_key){
            HashMap<String,Object> map=new HashMap<>();
            map.put("cid",cid);
            map.put("tel",phone_num);
            map.put("code",sms_code);
            map.put("captcha_key",captcha_key);
            map.put("source","main_web");
            try {
                Object[] data=internet.sendPost("https://passport.bilibili.com/x/passport-login/web/login/sms",map);
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
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.titleProperty().set("信息");
                        alert.setHeaderText("登录成功");
                        alert.showAndWait();
                        me.heartalborada.bilidownloader.gui.login.close();
                        me.heartalborada.bilidownloader.gui.captcha.close();
                    } else {
                        int code=JsonParser.parseString(String.valueOf(data[0])).getAsJsonObject().get("code").getAsInt();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.titleProperty().set("错误");
                        switch (code){
                            case -400:
                                alert.setHeaderText("错误代码: "+code+" 请求错误\n请联系开发者");
                                break;
                            case 1006:
                                alert.setHeaderText("错误代码: "+code+" 请输入正确的短信验证码\n请检查短信验证码是否填写正确");
                                break;
                            case 1007:
                                alert.setHeaderText("错误代码: "+code+" 短信验证码已过期\n请重新发送验证码");
                                break;
                            default:
                                alert.setHeaderText("错误代码: "+code+"-未知错误\n请联系开发者");
                        }
                        alert.showAndWait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
