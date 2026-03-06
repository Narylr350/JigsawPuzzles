package model.repository;

import model.GameSave;

// 游戏存档仓储接口：按用户名读写与删除存档。
public interface GameSaveRepository {
    boolean save(GameSave save);

    GameSave load(String username);

    boolean hasSave(String username);

    boolean delete(String username);
}
