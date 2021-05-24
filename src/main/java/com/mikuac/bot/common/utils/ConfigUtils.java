package com.mikuac.bot.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mikuac.bot.bean.ConfigBean;
import com.mikuac.bot.config.Global;
import com.sun.nio.file.SensitivityWatchEventModifier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/5/24.
 *
 * @author Zero
 */
@Slf4j
@Component
public class ConfigUtils {

    private static String readConfigFile() throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream("config.json"), StandardCharsets.UTF_8);
        int ch = 0;
        StringBuilder sb = new StringBuilder();
        while ((ch = isr.read()) != -1) {
            sb.append((char) ch);
        }
        isr.close();
        return sb.toString();
    }

    private static void checkConfigFileExists() throws IOException {
        File file = new File("config.json");
        if (file.isFile() && file.exists()) {
            log.info("检测到配置文件已存在，将解析现有配置文件");
            return;
        }
        log.error("配置文件不存在，即将生成默认配置文件");
        ConfigBean configBean = new ConfigBean();

        ConfigBean.Server server = new ConfigBean.Server();
        server.setAddress("127.0.0.1");
        server.setPort(5000);
        configBean.setServer(server);

        ConfigBean.Bot bot = new ConfigBean.Bot();
        bot.setBotName("悠里");
        bot.setAdminId(0);
        bot.setSelfId(0);
        configBean.setBot(bot);

        ConfigBean.Telegram telegram = new ConfigBean.Telegram();
        telegram.setBotName("Bot Name Value");
        telegram.setBotToken("Bot Token Value");
        telegram.setProxyHost("127.0.0.1");
        telegram.setProxyPort(1080);
        configBean.setTelegram(telegram);

        ConfigBean.Prefix prefix = new ConfigBean.Prefix();
        prefix.setPrefix(".");
        configBean.setPrefix(prefix);

        ConfigBean.Maintenance maintenance = new ConfigBean.Maintenance();
        maintenance.setMaintenance(false);
        maintenance.setMaintenanceMsg("yuri维护中,请稍后再试~");
        configBean.setMaintenance(maintenance);

        ConfigBean.Repeat repeat = new ConfigBean.Repeat();
        repeat.setRandomCountSize(5);
        configBean.setRepeat(repeat);

        ConfigBean.Hitokoto hitokoto = new ConfigBean.Hitokoto();
        hitokoto.setCdTime(10);
        configBean.setHitokoto(hitokoto);

        ConfigBean.Setu setu = new ConfigBean.Setu();
        setu.setApiKey("Api Key Value");
        setu.setCdTime(120);
        setu.setDelTime(30);
        setu.setMaxGet(15);
        configBean.setSetu(setu);

        ConfigBean.SauceNao sauceNao = new ConfigBean.SauceNao();
        sauceNao.setApiKey("Api Key Value");
        configBean.setSauceNao(sauceNao);

        ConfigBean.BanUtils banUtils = new ConfigBean.BanUtils();
        banUtils.setLimitTime(30);
        banUtils.setLimitCount(10);
        configBean.setBanUtils(banUtils);

        // Obj转为json
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(configBean);
        // 写入Cache File
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("config.json"), StandardCharsets.UTF_8);
        osw.write(jsonObject.toString());
        osw.flush();
        osw.close();
        log.info("默认配置文件生成完毕，请按需修改配置文件");
    }

    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void watchFile() {
        new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                // 构造监听服务
                WatchService watcher = FileSystems.getDefault().newWatchService();
                //监听注册，监听实体的创建、修改、删除事件，并以高频率(每隔2秒一次，默认是10秒)监听
                Paths.get("./").register(watcher,
                        new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_MODIFY,
                                StandardWatchEventKinds.ENTRY_DELETE},
                        SensitivityWatchEventModifier.HIGH);
                while (true) {
                    // 每隔3秒拉取监听key
                    WatchKey key = watcher.poll(3, TimeUnit.SECONDS);
                    // 监听key为null,则跳过
                    if (key == null) {
                        continue;
                    }
                    //获取监听事件
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (StandardWatchEventKinds.ENTRY_MODIFY == event.kind() && "config.json".equals(event.context().toString())) {
                            log.info("检测到配置文件修改，即将重载配置文件");
                            init();
                        }
                    }
                    //处理监听key后(即处理监听事件后)，监听key需要复位，便于下次监听
                    key.reset();
                }
            }
        }.start();

    }

    @PostConstruct
    public static ConfigBean init() {
        try {
            checkConfigFileExists();
            ConfigBean configBean = JSON.parseObject(readConfigFile(), ConfigBean.class);
            log.info("配置文件解析完毕");
            Global.config = configBean;
            return configBean;
        } catch (Exception e) {
            log.error("配置文件解析失败且生成默认配置文件失败，即将退出: {}", e.getMessage());
            System.exit(0);
            return null;
        }
    }

}