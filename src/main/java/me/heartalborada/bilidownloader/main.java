package me.heartalborada.bilidownloader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import me.heartalborada.bilidownloader.gui.login;
import me.heartalborada.bilidownloader.gui.playLive;
import me.heartalborada.bilidownloader.gui.viewVideo;
import me.heartalborada.bilidownloader.utils.file;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static java.lang.System.getProperty;
import static me.heartalborada.bilidownloader.utils.login.checkIsLogin;

public class main {
    //file
    public static final String run_dir = getProperty("user.dir");
    public static final String config_dir = run_dir + "/config/";
    public static final String cookie_file_dir = config_dir + "bili_cookies.json";
    public static final String settings_file_dir = config_dir + "settings.json";
    //setting
    public static String SESSDATA = null;
    public static String bili_jct = null;
    public static String download_path = run_dir + "/download/";
    public static String video_format = "${video_name}_${video_page}";
    public static long max_buffer_size = 2048;

    public static void main(String[] arg) {
        String live = getProperty("live","");
        if(live.equals("1")){
            playLive.main(new String[]{});
            return;
        }
        String java_version = getProperty("java.version").split("\\.")[0];
        if (Integer.parseInt(java_version) < 13) {
            System.err.println("本程序需要在Java 13及以上的环境运行");
            System.exit(0);
        }
        if (getProperty("os.name").equals("Linux")) {
            System.setProperty("java.awt.headless", "true");
        }
        try {
            new main().setParam();
            new main().setConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!checkIsLogin()) {
            login.main(new String[]{""});
            return;
        }
        new Thread(() -> {
            Application.launch(viewVideo.class);
        }).run();
    }

    public void setParam() throws IOException {
        file file = new file();
        File cookieFile = new File(cookie_file_dir);
        if (!cookieFile.exists()) {
            file.write(cookie_file_dir, file.read_res(this.getClass().getResourceAsStream("bili_cookies.json")));
        }
        String data = file.read(cookie_file_dir);
        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        SESSDATA = json.get("SESSDATA").getAsString();
        bili_jct = json.get("bili_jct").getAsString();
    }

    public void setConfig() throws IOException {
        file file = new file();
        File config = new File(settings_file_dir);
        if (!config.exists()) {
            file.write(settings_file_dir, file.read_res(this.getClass().getResourceAsStream("settings.json")));
        }
        String data = file.read(cookie_file_dir);
        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        if (json.has("download_settings")) {
            JsonObject j1 = json.getAsJsonObject("download_settings");
            if (Stream.of("download_path", "file_name_format", "max_buffer_size").allMatch(j1::has)) {
                download_path = j1.get("download_path").getAsString() != "" ? j1.get("download_path").getAsString().replace("{$running_path}" + "/", run_dir) : download_path;
                video_format = j1.get("video_format").getAsString() != "" ? j1.get("video_format").getAsString() : download_path;
                max_buffer_size = j1.get("max_buffer_size").getAsLong() == 0 ? j1.get("max_buffer_size").getAsLong() : max_buffer_size;
                return;
            }
        }
        file.write(settings_file_dir, file.read_res(this.getClass().getResourceAsStream("settings.json")));
    }

}
