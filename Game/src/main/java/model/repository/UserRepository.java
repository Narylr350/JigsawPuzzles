package model.repository;

import model.User;

import java.util.List;

// 用户仓储接口：抽象用户读取与批量保存能力。
public interface UserRepository {
    List<User> loadAll();

    void saveAll(List<User> users);
}
