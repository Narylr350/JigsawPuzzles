package model.service;

import model.GameSave;
import model.repository.GameSaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SaveServiceTest {

    private InMemorySaveRepository repository;
    private SaveService saveService;

    @BeforeEach
    void setUp() {
        repository = new InMemorySaveRepository();
        saveService = new SaveService(repository);
    }

    @Test
    void shouldSaveAndLoadGame() {
        int[][] board = {{1, 2}, {3, 0}};
        boolean saved = saveService.saveGame("tester", 2, board, 1, 1, 10, "image/animal/animal", 1);

        assertTrue(saved);
        GameSave loaded = saveService.loadGame("tester");
        assertNotNull(loaded);
        assertEquals(2, loaded.getGridSize());
        assertEquals(10, loaded.getStepCount());
    }

    @Test
    void shouldDeleteSave() {
        saveService.saveGame("tester", 2, new int[][]{{1, 2}, {3, 0}}, 1, 1, 2, "image/animal/animal", 1);

        assertTrue(saveService.hasSave("tester"));
        assertTrue(saveService.deleteSave("tester"));
        assertFalse(saveService.hasSave("tester"));
    }

    private static class InMemorySaveRepository implements GameSaveRepository {
        private final Map<String, GameSave> data = new HashMap<>();

        @Override
        public boolean save(GameSave save) {
            data.put(save.getUsername(), save);
            return true;
        }

        @Override
        public GameSave load(String username) {
            return data.get(username);
        }

        @Override
        public boolean hasSave(String username) {
            return data.containsKey(username);
        }

        @Override
        public boolean delete(String username) {
            return data.remove(username) != null;
        }
    }
}