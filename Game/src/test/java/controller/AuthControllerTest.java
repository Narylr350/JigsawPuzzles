package controller;

import model.User;
import model.repository.UserRepository;
import model.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerTest {

    private AuthController authController;

    @BeforeEach
    void setUp() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        authController = new AuthController(new AuthService(repository));
        authController.loadUsers();
    }

    @Test
    @DisplayName("用户名为空时应返回错误")
    void testValidateLogin_EmptyUsername() {
        String result = authController.validateLogin("", "123".toCharArray(), "abc", "abc");
        assertEquals("用户名不能为空", result);
    }

    @Test
    @DisplayName("密码为空时应返回错误")
    void testValidateLogin_EmptyPassword() {
        String result = authController.validateLogin("test", new char[0], "abc", "abc");
        assertEquals("密码不能为空", result);
    }

    @Test
    @DisplayName("验证码为空时应返回错误")
    void testValidateLogin_EmptyCaptcha() {
        String result = authController.validateLogin("test", "123".toCharArray(), "", "abc");
        assertEquals("验证码不能为空", result);
    }

    @Test
    @DisplayName("验证码错误时应返回错误")
    void testValidateLogin_WrongCaptcha() {
        String result = authController.validateLogin("test", "123".toCharArray(), "wrong", "abc");
        assertEquals("验证码错误", result);
    }

    @Test
    @DisplayName("用户名或密码错误时应返回错误")
    void testValidateLogin_WrongCredentials() {
        String result = authController.validateLogin("notexist", "wrong".toCharArray(), "abc", "abc");
        assertEquals("用户名或密码错误", result);
    }

    @Test
    @DisplayName("注册后应能查到用户")
    void testUserExists_ExistingUser() {
        String username = "test_user_" + System.nanoTime();
        authController.registerUser(username, "Abcd1234");
        assertTrue(authController.userExists(username));
    }

    @Test
    @DisplayName("不存在用户应返回false")
    void testUserExists_NonExistingUser() {
        assertFalse(authController.userExists("不存在的用户12345"));
    }

    @Test
    @DisplayName("验证码长度应为5")
    void testGenerateCaptcha_Length() {
        String captcha = authController.generateCaptcha();
        assertEquals(5, captcha.length());
    }

    @Test
    @DisplayName("验证码应具备随机性")
    void testGenerateCaptcha_Random() {
        String captcha1 = authController.generateCaptcha();
        String captcha2 = authController.generateCaptcha();
        assertNotEquals(captcha1, captcha2);
    }

    private static class InMemoryUserRepository implements UserRepository {
        private final List<User> users = new ArrayList<>();

        @Override
        public List<User> loadAll() {
            return new ArrayList<>(users);
        }

        @Override
        public void saveAll(List<User> users) {
            this.users.clear();
            this.users.addAll(users);
        }
    }
}