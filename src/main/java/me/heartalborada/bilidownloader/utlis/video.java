package me.heartalborada.bilidownloader.utlis;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.heartalborada.bilidownloader.main;

import java.util.LinkedHashMap;

public class video {
    public long getCid(long aid,int page){
        String data=null;
        try {
            data=new internet().Eget("https://api.bilibili.com/x/web-interface/view?aid="+aid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json= JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()==0) {
            JsonArray pages=json.get("data").getAsJsonObject().get("pages").getAsJsonArray();
            for (JsonElement o : pages) {
                if (o.getAsJsonObject().get("page").getAsInt() == page) {
                    return o.getAsJsonObject().get("cid").getAsLong();
                }
            }
        }
        return 404;
    }

    public long BVidToAid(String BVid){
        String data=null;
        try {
            data=new internet().Eget("https://api.bilibili.com/x/web-interface/view?BVid="+BVid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json=JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()==0) {
            return json.get("data").getAsJsonObject().get("aid").getAsLong();
        }
        return 404;
    }

    public String getVideoUrl(long aid,long cid,int qn1){
        String data=null;
        try {
            data=new internet().getWithCookie(
                    "https://api.bilibili.com/x/player/playurl?avid="+aid+"&cid="+cid+"&fourk=1&qn="+qn1,
                    "SESSDATA="+ main.SESSDATA+"; bili_jct="+main.bili_jct
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json=JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()==0){
            return json.get("durl").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
        }
        return "404";
    }

    public LinkedHashMap<Integer,String> getQuality(long aid,long cid){
        LinkedHashMap<Integer,String>map= new LinkedHashMap<>();
        String data=null;
        try {
            data=new internet().Eget("https://api.bilibili.com/x/player/playurl?avid="+aid+"&cid="+cid+"&fourk=1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json=JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()==0){
            JsonArray description= json.get("data").getAsJsonObject().get("accept_description").getAsJsonArray();
            JsonArray quality= json.get("data").getAsJsonObject().get("accept_quality").getAsJsonArray();
            for(int i=0;i<description.size();i++){
                map.put(quality.get(i).getAsInt(),description.get(i).getAsString());
            }
        }
        return map;
    }
}
