package model.repository.json;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import model.User;
import model.repository.UserRepository;
import util.ConfigUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// 用户 JSON 仓储实现：将用户列表持久化到配置路径。
public class JsonUserRepository implements UserRepository {
    private final String userInfoPath;

    public JsonUserRepository() {
        this(ConfigUtil.getUserInfoPath());
    }

    public JsonUserRepository(String userInfoPath) {
        this.userInfoPath = userInfoPath;
    }

    @Override
    public List<User> loadAll() {
        ensureFile();
        String json = FileUtil.readUtf8String(userInfoPath);
        if (StrUtil.isBlank(json)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(JSONUtil.toList(json, User.class));
    }

    @Override
    public void saveAll(List<User> users) {
        ensureFile();
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(users), userInfoPath);
    }

    private void ensureFile() {
        // 首次运行时自动创建父目录与空文件。
        File file = new File(userInfoPath);
        FileUtil.mkParentDirs(file);
        if (!file.exists()) {
            FileUtil.touch(file);
        }
    }
}
