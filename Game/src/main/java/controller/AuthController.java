package controller;

import model.service.AuthService;

// 认证控制器：供 UI 调用，内部委托认证服务。
public class AuthController {
    private final AuthService authService;

    public AuthController() {
        this(new AuthService());
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void loadUsers() {
        authService.loadUsers();
    }

    public String validateLogin(String username, char[] password, String captcha, String actualCode) {
        return authService.validateLogin(username, password, captcha, actualCode);
    }

    public String generateCaptcha() {
        return authService.generateCaptcha();
    }

    public boolean userExists(String usernameText) {
        return authService.userExists(usernameText);
    }

    public void registerUser(String userName, String passWord) {
        authService.registerUser(userName, passWord);
    }
}
