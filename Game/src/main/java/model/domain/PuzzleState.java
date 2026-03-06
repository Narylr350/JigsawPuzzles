package model.domain;

import java.util.Random;

// 拼图领域模型：封装棋盘状态、移动规则与可解性判断。
public class PuzzleState {
    private final int gridSize;
    private final int[][] board;
    private int emptyX;
    private int emptyY;
    private int stepCount;

    private PuzzleState(int gridSize, int[][] board, int emptyX, int emptyY, int stepCount) {
        this.gridSize = gridSize;
        this.board = board;
        this.emptyX = emptyX;
        this.emptyY = emptyY;
        this.stepCount = stepCount;
    }

    public static PuzzleState random(int gridSize, Random random) {
        int total = gridSize * gridSize;
        int[] values = new int[total];
        for (int i = 0; i < total; i++) {
            values[i] = i;
        }

        int[][] board;
        int emptyX;
        int emptyY;
        do {
            // Fisher-Yates 洗牌，直到生成可解局面。
            for (int i = total - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                int temp = values[i];
                values[i] = values[index];
                values[index] = temp;
            }

            board = new int[gridSize][gridSize];
            emptyX = 0;
            emptyY = 0;
            for (int i = 0; i < total; i++) {
                board[i / gridSize][i % gridSize] = values[i];
                if (values[i] == 0) {
                    emptyX = i / gridSize;
                    emptyY = i % gridSize;
                }
            }
        } while (!isSolvable(gridSize, board, emptyX));

        return new PuzzleState(gridSize, board, emptyX, emptyY, 0);
    }

    public static PuzzleState fromState(int gridSize, int[][] board, int emptyX, int emptyY, int stepCount) {
        return new PuzzleState(gridSize, copy2d(board), emptyX, emptyY, stepCount);
    }

    public boolean moveTile(int row, int col) {
        // 鼠标点击：仅允许与空白块相邻的方块移动。
        if (!isAdjacent(row, col, emptyX, emptyY)) {
            return false;
        }
        swap(row, col, emptyX, emptyY);
        emptyX = row;
        emptyY = col;
        stepCount++;
        return true;
    }

    public boolean moveByKeyCode(int keyCode) {
        // 键盘方向键语义与原 UI 保持一致。
        return switch (keyCode) {
            case 37 -> moveEmptyTo(emptyX, emptyY + 1); // left
            case 38 -> moveEmptyTo(emptyX + 1, emptyY); // up
            case 39 -> moveEmptyTo(emptyX, emptyY - 1); // right
            case 40 -> moveEmptyTo(emptyX - 1, emptyY); // down
            default -> false;
        };
    }

    public boolean isSolved() {
        int expected = 1;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (i == gridSize - 1 && j == gridSize - 1) {
                    if (board[i][j] != 0) {
                        return false;
                    }
                } else if (board[i][j] != expected++) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSolvable() {
        return isSolvable(gridSize, board, emptyX);
    }

    public void forceSolved() {
        // VIP 一键通关：将棋盘直接重置为目标态。
        int expected = 1;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (i == gridSize - 1 && j == gridSize - 1) {
                    board[i][j] = 0;
                } else {
                    board[i][j] = expected++;
                }
            }
        }
        emptyX = gridSize - 1;
        emptyY = gridSize - 1;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int[][] getBoardCopy() {
        return copy2d(board);
    }

    public int getEmptyX() {
        return emptyX;
    }

    public int getEmptyY() {
        return emptyY;
    }

    private static int[][] copy2d(int[][] src) {
        int[][] copy = new int[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, copy[i], 0, src[i].length);
        }
        return copy;
    }

    private boolean moveEmptyTo(int targetX, int targetY) {
        if (targetX < 0 || targetX >= gridSize || targetY < 0 || targetY >= gridSize) {
            return false;
        }
        swap(emptyX, emptyY, targetX, targetY);
        emptyX = targetX;
        emptyY = targetY;
        stepCount++;
        return true;
    }

    private void swap(int x1, int y1, int x2, int y2) {
        int temp = board[x1][y1];
        board[x1][y1] = board[x2][y2];
        board[x2][y2] = temp;
    }

    private static boolean isAdjacent(int x1, int y1, int x2, int y2) {
        return (Math.abs(x1 - x2) == 1 && y1 == y2) || (Math.abs(y1 - y2) == 1 && x1 == x2);
    }

    private static boolean isSolvable(int gridSize, int[][] board, int emptyX) {
        int total = gridSize * gridSize;
        int[] arr = new int[total];
        int idx = 0;
        for (int[] rows : board) {
            for (int value : rows) {
                arr[idx++] = value;
            }
        }

        int inversions = 0;
        // 逆序数判定：与空白块行号共同决定偶数阶棋盘可解性。
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {
                continue;
            }
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] == 0) {
                    continue;
                }
                if (arr[i] > arr[j]) {
                    inversions++;
                }
            }
        }

        int blankRowFromBottom = gridSize - emptyX;
        if (gridSize % 2 == 1) {
            return inversions % 2 == 0;
        }
        return (inversions + blankRowFromBottom) % 2 == 1;
    }
}
