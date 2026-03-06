# MVC Three-Layer Refactor Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Refactor JigsawPuzzles into strict MVC three layers while preserving existing UI behavior and save compatibility.

**Architecture:** Keep Swing classes as view-only, route actions through controllers, and move puzzle/auth/save rules and persistence into model domain/service/repository classes. Apply TDD for extracted behaviors before production code changes.

**Tech Stack:** Java 21, Swing, Maven, Hutool JSON/Crypto, JUnit 5.

---

### Task 1: Add Testable Puzzle Domain Model

**Files:**
- Create: `Game/src/main/java/model/domain/PuzzleState.java`
- Create: `Game/src/test/java/model/domain/PuzzleStateTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldGenerateSolvableBoardFor4x4() {
    PuzzleState state = PuzzleState.random(4, new Random(1));
    assertTrue(state.isSolvable());
}
```

**Step 2: Run test to verify it fails**

Run: `cd Game; mvn -Dtest=model.domain.PuzzleStateTest#shouldGenerateSolvableBoardFor4x4 test`
Expected: FAIL (class/method missing).

**Step 3: Write minimal implementation**

```java
public final class PuzzleState {
    public static PuzzleState random(int gridSize, Random random) { ... }
    public boolean isSolvable() { ... }
}
```

**Step 4: Run test to verify it passes**

Run: `cd Game; mvn -Dtest=model.domain.PuzzleStateTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add Game/src/main/java/model/domain/PuzzleState.java Game/src/test/java/model/domain/PuzzleStateTest.java
git commit -m "test+feat: add puzzle state domain model"
```

### Task 2: Add Game Service for Moves and Difficulty

**Files:**
- Create: `Game/src/main/java/model/service/GameService.java`
- Create: `Game/src/test/java/model/service/GameServiceTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldMoveLeftWhenBlankHasRightNeighbor() {
    GameService service = new GameService(new Random(1));
    service.newGame(4);
    // inject known board then move left
    assertTrue(service.moveLeft());
}
```

**Step 2: Run test to verify it fails**

Run: `cd Game; mvn -Dtest=model.service.GameServiceTest#shouldMoveLeftWhenBlankHasRightNeighbor test`
Expected: FAIL (class/method missing).

**Step 3: Write minimal implementation**

```java
public class GameService {
    public void newGame(int gridSize) { ... }
    public boolean moveLeft() { ... }
    public int[][] getBoard() { ... }
}
```

**Step 4: Run test to verify it passes**

Run: `cd Game; mvn -Dtest=model.service.GameServiceTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add Game/src/main/java/model/service/GameService.java Game/src/test/java/model/service/GameServiceTest.java
git commit -m "test+feat: add game service operations"
```

### Task 3: Extract Repository + Auth Service

**Files:**
- Create: `Game/src/main/java/model/repository/UserRepository.java`
- Create: `Game/src/main/java/model/repository/json/JsonUserRepository.java`
- Create: `Game/src/main/java/model/service/AuthService.java`
- Modify: `Game/src/main/java/controller/AuthController.java`
- Create: `Game/src/test/java/model/service/AuthServiceTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldRejectWrongCaptcha() {
    AuthService authService = new AuthService(new InMemoryUserRepository());
    String msg = authService.validateLogin("u", "p".toCharArray(), "x", "y");
    assertEquals("验证码错误", msg);
}
```

**Step 2: Run test to verify it fails**

Run: `cd Game; mvn -Dtest=model.service.AuthServiceTest#shouldRejectWrongCaptcha test`
Expected: FAIL.

**Step 3: Write minimal implementation**

```java
public class AuthService {
    public String validateLogin(...) { ... }
    public boolean userExists(String username) { ... }
    public void registerUser(String userName, String passWord) { ... }
}
```

**Step 4: Run test to verify it passes**

Run: `cd Game; mvn -Dtest=model.service.AuthServiceTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add Game/src/main/java/model/repository Game/src/main/java/model/service/AuthService.java Game/src/main/java/controller/AuthController.java Game/src/test/java/model/service/AuthServiceTest.java
git commit -m "refactor(auth): move auth logic into model service and repository"
```

### Task 4: Extract Save Repository + Save Service

**Files:**
- Create: `Game/src/main/java/model/repository/GameSaveRepository.java`
- Create: `Game/src/main/java/model/repository/json/JsonGameSaveRepository.java`
- Create: `Game/src/main/java/model/service/SaveService.java`
- Modify: `Game/src/main/java/controller/SaveController.java`
- Create: `Game/src/test/java/model/service/SaveServiceTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldSaveAndLoadGameByUsername() {
    SaveService service = new SaveService(new InMemoryGameSaveRepository());
    assertTrue(service.saveGame(...));
    assertNotNull(service.loadGame("user"));
}
```

**Step 2: Run test to verify it fails**

Run: `cd Game; mvn -Dtest=model.service.SaveServiceTest#shouldSaveAndLoadGameByUsername test`
Expected: FAIL.

**Step 3: Write minimal implementation**

```java
public class SaveService {
    public boolean saveGame(...) { ... }
    public GameSave loadGame(String username) { ... }
}
```

**Step 4: Run test to verify it passes**

Run: `cd Game; mvn -Dtest=model.service.SaveServiceTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add Game/src/main/java/model/repository Game/src/main/java/model/service/SaveService.java Game/src/main/java/controller/SaveController.java Game/src/test/java/model/service/SaveServiceTest.java
git commit -m "refactor(save): move persistence logic into model layer"
```

### Task 5: Introduce Game Controller and Slim GameFrame

**Files:**
- Create: `Game/src/main/java/controller/GameController.java`
- Modify: `Game/src/main/java/ui/GameFrame.java`

**Step 1: Write the failing test**

```java
@Test
void shouldRestoreGameFromSaveThroughController() {
    GameController controller = new GameController(...);
    assertTrue(controller.loadGame("tester").isPresent());
}
```

**Step 2: Run test to verify it fails**

Run: `cd Game; mvn -Dtest=controller.GameControllerTest#shouldRestoreGameFromSaveThroughController test`
Expected: FAIL.

**Step 3: Write minimal implementation**

```java
public class GameController {
    public int[][] getBoard() { ... }
    public boolean moveByKeyCode(int keyCode) { ... }
    public boolean saveCurrentGame(String username) { ... }
    public boolean loadGame(String username) { ... }
}
```

**Step 4: Run test to verify it passes**

Run: `cd Game; mvn -Dtest=controller.GameControllerTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add Game/src/main/java/controller/GameController.java Game/src/main/java/ui/GameFrame.java Game/src/test/java/controller/GameControllerTest.java
git commit -m "refactor(ui): delegate gameplay logic from GameFrame to GameController"
```

### Task 6: Full Verification and Quality Check

**Files:**
- Modify (if needed): `Game/src/test/java/controller/AuthControllerTest.java`
- Review: all changed files

**Step 1: Write/adjust final regression tests (if failing due architecture changes)**

```java
@Test
void shouldKeepOriginalLoginValidationMessages() {
    ...
}
```

**Step 2: Run full tests**

Run: `cd Game; mvn test`
Expected: all tests pass.

**Step 3: Run package build**

Run: `cd Game; mvn -DskipTests package`
Expected: build success.

**Step 4: Code quality checklist**
- No UI class directly reads/writes JSON files.
- No UI class performs puzzle solvability algorithm.
- Controllers depend on services, services depend on repositories.

**Step 5: Commit**

```bash
git add Game/src/main/java Game/src/test/java docs/plans
git commit -m "refactor: enforce MVC three-layer architecture and quality checks"
```
