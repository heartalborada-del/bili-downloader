package me.heartalborada.bilidownloader.utils;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.heartalborada.bilidownloader.main;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class video {
    public static LinkedHashMap<String, Long> getCidList(String aid){
        LinkedHashMap<String,Long> map=new LinkedHashMap<String,Long>();
        String data=null;
        try {
                data=new internet().Eget("https://api.bilibili.com/x/web-interface/view?aid="+aid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json= JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()==0) {
            JsonArray pages=json.get("data").getAsJsonObject().get("pages").getAsJsonArray();
            int i=0;
            for (JsonElement o : pages) {
                map.put("P"+i+" "+o.getAsJsonObject().get("part").getAsString(),o.getAsJsonObject().get("cid").getAsLong());
                i++;
            }
        }
        return map;
    }

    public static String BVidToAid(String BVid){
        String data=null;
        try {
            data=new internet().Eget("https://api.bilibili.com/x/web-interface/view?bvid="+BVid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json=JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()==0) {
            return json.get("data").getAsJsonObject().get("aid").getAsString();
        }
        return "404";
    }

    private static JsonObject json;

    public String getVideoUrl(String aid, long cid, int qn1){
        String data=null;
        try {
            data=new internet().getWithCookie(
                    "https://api.bilibili.com/x/player/playurl?avid="+aid+"&cid="+cid+"&fourk=1&qn="+qn1,
                    "SESSDATA="+ main.SESSDATA+"; bili_jct="+main.bili_jct
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
         json=JsonParser.parseString(data).getAsJsonObject();
        System.out.println(json);
        if(json.get("code").getAsInt()==0){
            return json.getAsJsonObject("data").get("durl").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
        }
        return "404";
    }

    public static LinkedHashMap<String,Integer> getQuality(String aid, long cid){
        LinkedHashMap<String,Integer>map= new LinkedHashMap<>();
        String data=null;
        try {
            data=new internet().getWithCookie(
                    "https://api.bilibili.com/x/player/playurl?avid="+aid+"&cid="+cid+"&fourk=1&qn=120",
                    "SESSDATA="+ main.SESSDATA+"; bili_jct="+main.bili_jct
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json=JsonParser.parseString(data).getAsJsonObject();
        System.out.println(data);
        if(json.get("code").getAsInt()==0){
            JsonArray description= json.get("data").getAsJsonObject().get("accept_description").getAsJsonArray();
            JsonArray quality= json.get("data").getAsJsonObject().get("accept_quality").getAsJsonArray();
            int max=json.getAsJsonObject("data").get("quality").getAsInt();
            for(int i=0;i<description.size();i++){
                if(!(max<quality.get(i).getAsInt()))
                    try{
                        map.put(new String(description.get(i).getAsString().getBytes("ISO_8859_1"),"UTF-8")
                                ,quality.get(i).getAsInt());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    
            }
        }
        return map;
    }

    public static JsonObject getVideoJson(String aid){
        String data=null;
        try {
            data = new internet().Eget("https://api.bilibili.com/x/web-interface/view?aid=" + aid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonParser.parseString(data).getAsJsonObject();
    }

    public static String getVideoPic(JsonObject json){
        return json.getAsJsonObject("data").get("pic").getAsString();
    }

    public static boolean checkStrIsNum(String str) {
        Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try {
            /** 先将str转成BigDecimal，然后在转成String */
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            /** 如果转换数字失败，说明该str并非全部为数字 */
            return false;
        }
        Matcher isNum = NUMBER_PATTERN.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean videoIsExist(JsonObject json){
        return json.get("code").getAsInt()==0;
    }
}
