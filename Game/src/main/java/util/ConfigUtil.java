package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

// 配置工具：统一读取 dev/prod 配置并解析路径。
public class ConfigUtil {
    private static final Properties props;
    private static final String RUN_DIR;
    private static final String APP_DATA_DIR;

    static {
        props = new Properties();

        String mode = "dev";
        try (InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                Properties mainProps = new Properties();
                mainProps.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                mode = mainProps.getProperty("mode", "dev");
            }
        } catch (IOException e) {
            System.err.println("加载主配置失败: " + e.getMessage());
        }

        String configFile = "config-" + mode + ".properties";
        try (InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream(configFile)) {
            if (is != null) {
                props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                System.out.println("[配置] 加载模式: " + mode);
            } else {
                System.err.println("配置文件不存在: " + configFile);
            }
        } catch (IOException e) {
            System.err.println("加载配置文件失败: " + e.getMessage());
        }

        RUN_DIR = System.getProperty("user.dir");

        String appData = System.getenv("APPDATA");
        if (appData != null) {
            APP_DATA_DIR = appData + File.separator + "JigsawPuzzles";
            new File(APP_DATA_DIR).mkdirs();
        } else {
            APP_DATA_DIR = RUN_DIR;
        }
    }

    public static String get(String key) {
        String value = props.getProperty(key, "");
        if (value.isEmpty()) {
            return "";
        }

        if (value.startsWith("${APPDATA}")) {
            value = value.replace("${APPDATA}", APP_DATA_DIR);
            return new File(value).getAbsolutePath();
        }

        String normalizedValue = value.replace("/", File.separator).replace("\\", File.separator);
        String gamePrefix = "Game" + File.separator;

        // 兼容两种运行目录：
        // 1) 仓库根目录运行（user.dir=.../JigsawPuzzles）
        // 2) Game 模块目录运行（user.dir=.../JigsawPuzzles/Game）
        if (RUN_DIR.endsWith(File.separator + "Game") && normalizedValue.startsWith(gamePrefix)) {
            return new File(RUN_DIR, normalizedValue.substring(gamePrefix.length())).getAbsolutePath();
        }

        return new File(RUN_DIR, normalizedValue).getAbsolutePath();
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static String getUserInfoPath() {
        return get("data.userinfo");
    }

    public static String getSaveDir() {
        return get("data.save.dir");
    }

    public static String getImageDir() {
        return get("resources.image.dir");
    }
}
