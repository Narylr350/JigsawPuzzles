package model.service;

import cn.hutool.crypto.SecureUtil;
import model.User;
import model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private InMemoryUserRepository repository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        repository.users.add(new User("tester", SecureUtil.md5("Pass1234")));
        authService = new AuthService(repository);
        authService.loadUsers();
    }

    @Test
    void shouldRejectWrongCaptcha() {
        String msg = authService.validateLogin("tester", "Pass1234".toCharArray(), "abcd", "wxyz");
        assertEquals("验证码错误", msg);
    }

    @Test
    void shouldRegisterAndFindUser() {
        authService.registerUser("new_user", "Abcd1234");

        assertTrue(authService.userExists("new_user"));
        assertEquals(2, repository.users.size());
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