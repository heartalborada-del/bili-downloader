package me.heartalborada.bilidownloader.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.LinkedHashMap;

public class live {
    public static long getRoomId(Long uid){
        String data=new String();
        try {
            data=new internet().Eget("http://api.live.bilibili.com/live_user/v1/Master/info?uid="+uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json= JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()!=0){
            return 0;
        }
        return json.getAsJsonObject("data").get("room_id").getAsLong();
    }

    public static LinkedHashMap<String,Long> getLiveQn(Long room_id){
        LinkedHashMap<String, Long> map= new LinkedHashMap<>();
        String data=new String();
        try {
            data=new internet().Eget("http://api.live.bilibili.com/room/v1/Room/playUrl?cid="+room_id+"&qn=10000&platform=h5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonArray json= JsonParser.parseString(data).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("quality_description");
        for(int i=0;i<json.size();i++){
            JsonObject qn= json.get(i).getAsJsonObject();
            map.put(qn.get("desc").getAsString(),qn.get("qn").getAsLong());
        }
        return  map;
    }

    public static LinkedHashMap<String,String> getServerList(long room_id,long qn){
        LinkedHashMap<String, String> map= new LinkedHashMap<>();
        String data=new String();
        try {
            data=new internet().Eget("http://api.live.bilibili.com/room/v1/Room/playUrl?cid="+room_id+"&qn="+qn+"&platform=h5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonArray json= JsonParser.parseString(data).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("durl");
        for(int i=0;i<json.size();i++){
            JsonObject server= json.get(i).getAsJsonObject();
            map.put(
                    "服务器 - "+server.get("order").getAsLong(),
                    server.get("url").getAsString()
            );
        }
        return map;
    }

    public static boolean isLiving(long uid) {
        String data = "";
        try {
            data=new internet().Eget("https://api.bilibili.com/x/space/acc/info?mid="+uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json=JsonParser.parseString(data).getAsJsonObject();
        if(json.get("code").getAsInt()!=0||json.getAsJsonObject("data").getAsJsonObject("live_room").get("liveStatus").getAsInt()==0){
            return false;
        }
        return true;
    }

    public static boolean hasLiveRoom(long uid){
        String data = "";
        try {
            data=new internet().Eget("https://api.bilibili.com/x/space/acc/info?mid="+uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json=JsonParser.parseString(data).getAsJsonObject();
        if(json.getAsJsonObject("data").getAsJsonObject("live_room").get("roomStatus").getAsInt()==0){
            return false;
        }
        return true;
    }
}
