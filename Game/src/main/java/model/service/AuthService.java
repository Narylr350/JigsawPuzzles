package model.service;

import cn.hutool.crypto.SecureUtil;
import model.User;
import model.repository.UserRepository;
import model.repository.json.JsonUserRepository;
import util.GetCodeUtil;

import java.util.ArrayList;
import java.util.List;

// 认证业务服务：处理用户加载、登录校验、注册和验证码生成。
public class AuthService {
    private final UserRepository userRepository;
    private final List<User> userList = new ArrayList<>();

    public AuthService() {
        this(new JsonUserRepository());
    }

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 从仓储加载用户到内存快照。
    public void loadUsers() {
        userList.clear();
        userList.addAll(userRepository.loadAll());
    }

    // 登录校验顺序：空值 -> 验证码 -> 账号密码。
    public String validateLogin(String username, char[] password, String captcha, String actualCode) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (password == null || password.length == 0) {
            return "密码不能为空";
        }
        if (captcha == null || captcha.trim().isEmpty()) {
            return "验证码不能为空";
        }
        if (!captcha.equals(actualCode)) {
            return "验证码错误";
        }

        String encryptedPassword = SecureUtil.md5(new String(password));
        for (User user : userList) {
            if (username.equals(user.getUsername()) && encryptedPassword.equals(user.getPassword())) {
                return null;
            }
        }
        return "用户名或密码错误";
    }

    public boolean userExists(String usernameText) {
        for (User user : userList) {
            if (usernameText.equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    // 存储前统一进行 MD5，保持历史数据兼容。
    public void registerUser(String userName, String passWord) {
        String encryptedPassword = SecureUtil.md5(passWord);
        userList.add(new User(userName, encryptedPassword));
        userRepository.saveAll(userList);
    }

    public String generateCaptcha() {
        return GetCodeUtil.getCode();
    }
}