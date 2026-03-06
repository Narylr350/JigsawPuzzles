package model.repository.json;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import model.GameSave;
import model.repository.GameSaveRepository;
import util.ConfigUtil;

import java.io.File;

// 存档 JSON 仓储实现：每个用户对应一个存档文件。
public class JsonGameSaveRepository implements GameSaveRepository {
    private final String saveDir;

    public JsonGameSaveRepository() {
        this(ConfigUtil.getSaveDir());
    }

    public JsonGameSaveRepository(String saveDir) {
        this.saveDir = saveDir;
    }

    @Override
    public boolean save(GameSave save) {
        try {
            FileUtil.mkParentDirs(saveFile(save.getUsername()));
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(save), saveFile(save.getUsername()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public GameSave load(String username) {
        try {
            File file = new File(saveFile(username));
            if (!file.exists()) {
                return null;
            }
            String json = FileUtil.readUtf8String(file);
            if (StrUtil.isBlank(json)) {
                return null;
            }
            return JSONUtil.toBean(json, GameSave.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean hasSave(String username) {
        return new File(saveFile(username)).exists();
    }

    @Override
    public boolean delete(String username) {
        try {
            return FileUtil.del(saveFile(username));
        } catch (Exception e) {
            return false;
        }
    }

    private String saveFile(String username) {
        return saveDir + File.separator + username + ".json";
    }
}
