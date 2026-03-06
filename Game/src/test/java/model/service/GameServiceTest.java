package model.service;

import model.GameSave;
import model.domain.PuzzleState;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void shouldMoveByKeyCodeUsingCurrentState() {
        GameService service = new GameService(new Random(1));
        service.setState(PuzzleState.fromState(2, new int[][]{{1, 2}, {3, 0}}, 1, 1, 0));

        boolean moved = service.moveByKeyCode(37);

        assertFalse(moved);
        assertEquals(0, service.getStepCount());
    }

    @Test
    void shouldBuildSaveFromCurrentState() {
        GameService service = new GameService(new Random(1));
        service.setState(PuzzleState.fromState(2, new int[][]{{1, 2}, {0, 3}}, 1, 0, 5));

        GameSave save = service.buildSave("tester", "image/animal/animal", 1);

        assertEquals("tester", save.getUsername());
        assertEquals(2, save.getGridSize());
        assertEquals(5, save.getStepCount());
        assertEquals("image/animal/animal", save.getImagePath());
    }
}