package controller;

import model.GameSave;
import model.repository.GameSaveRepository;
import model.service.GameService;
import model.service.SaveService;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    @Test
    void shouldSaveAndLoadThroughController() {
        SaveService saveService = new SaveService(new InMemorySaveRepository());
        GameService gameService = new GameService(new Random(1));
        gameService.startNewGame(2);

        GameController controller = new GameController(gameService, saveService, false);
        controller.setImage("image/animal/animal", 1);

        assertTrue(controller.saveCurrentGame("tester"));

        controller.changeDifficulty(4);
        assertTrue(controller.loadGame("tester"));
        assertEquals(2, controller.getGridSize());
        assertEquals("image/animal/animal", controller.getImagePath());
        assertEquals(1, controller.getImageNum());
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
