package model.domain;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleStateTest {

    @Test
    void randomStateShouldBeSolvable() {
        PuzzleState state = PuzzleState.random(4, new Random(1));

        assertTrue(state.isSolvable());
        assertEquals(4, state.getGridSize());
    }

    @Test
    void clickNeighborShouldMoveAndIncreaseStep() {
        int[][] board = {
                {1, 2},
                {0, 3}
        };
        PuzzleState state = PuzzleState.fromState(2, board, 1, 0, 0);

        boolean moved = state.moveTile(1, 1);

        assertTrue(moved);
        assertArrayEquals(new int[][]{{1, 2}, {3, 0}}, state.getBoardCopy());
        assertEquals(1, state.getStepCount());
    }

    @Test
    void arrowMoveShouldRespectBounds() {
        int[][] board = {
                {1, 2},
                {3, 0}
        };
        PuzzleState state = PuzzleState.fromState(2, board, 1, 1, 0);

        assertFalse(state.moveByKeyCode(37));
        assertEquals(0, state.getStepCount());
    }

    @Test
    void forceSolvedShouldSetSolvedState() {
        int[][] board = {
                {1, 0},
                {3, 2}
        };
        PuzzleState state = PuzzleState.fromState(2, board, 0, 1, 0);

        state.forceSolved();

        assertTrue(state.isSolved());
        assertEquals(1, state.getEmptyX());
        assertEquals(1, state.getEmptyY());
    }
}
