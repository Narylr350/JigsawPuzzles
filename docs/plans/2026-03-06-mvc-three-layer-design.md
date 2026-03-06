# MVC Three-Layer Refactor Design (JigsawPuzzles)

## Background
Current architecture mixes UI rendering, puzzle business rules, and persistence logic in `ui/GameFrame.java` and thin controllers. This creates low cohesion, hard testing, and high regression risk.

## Goals
- Enforce three-layer structure: `view` / `controller` / `model`.
- Keep existing gameplay/UI behavior unchanged.
- Keep save JSON structure backward compatible.
- Improve code quality by reducing static mutable state and extracting testable logic.

## Constraints
- Swing UI remains the presentation layer.
- Existing image resource layout and menu interactions must remain available.
- Existing account and save files should continue to work.

## Architecture

### View Layer (`ui`)
Responsibilities:
- Render JFrame/JLabel/JMenu and collect user input events.
- Delegate user actions to controllers.
- Refresh view based on controller-provided state.

Rules:
- No puzzle solvability logic.
- No file read/write.
- No password hashing or persistence logic.

### Controller Layer (`controller`)
Responsibilities:
- Coordinate use cases between view and model.
- Convert UI events into domain operations.
- Return success/failure messages and view-ready state.

Planned controllers:
- `AuthController`: login/register flow orchestration.
- `GameController`: gameplay orchestration (move/restart/change difficulty/change theme/save/load).
- `SaveController`: thin facade over save service for compatibility.

### Model Layer (`model`)
Responsibilities:
- Domain entities/state and business rules.
- Persistence repositories for users and saves.

Planned model packages:
- `model.domain`: `User`, `GameSave`, `PuzzleState` and enums/value objects.
- `model.service`: `AuthService`, `GameService`, `SaveService`.
- `model.repository`: repository interfaces.
- `model.repository.json`: JSON implementations using Hutool.

## Component Split
- Move puzzle randomization, solvability check, movement, win-check from `GameFrame` into `PuzzleState` + `GameService`.
- Move user data load/exists/register/validate from `AuthController` into `AuthService` + `UserRepository`.
- Move save/load/delete from static `SaveController` operations into `SaveService` + `GameSaveRepository`.

## Data Flow
1. UI receives input (`LoginFrame`, `RegisterFrame`, `GameFrame`).
2. UI calls corresponding controller method.
3. Controller calls model service and repository.
4. Controller returns result DTO/value/message.
5. UI updates components from returned state.

## Error Handling
- Service layer returns deterministic messages for validation errors.
- Repository exceptions are caught and wrapped into user-facing failure messages.
- Controllers avoid throwing unchecked exceptions to UI threads.

## Testing Strategy
- Add unit tests for:
  - `PuzzleState` solvability and move rules.
  - `GameService` state transitions (restart, difficulty change, save/load mapping).
  - `AuthService` validation and hashing path via in-memory or temp-file repository.
- Keep existing auth tests but remove dependence on hardcoded external user data.

## Code Quality Focus
- Remove static mutable user list from auth path.
- Make save flow instance-based and injectable for tests.
- Reduce method length and responsibilities in `GameFrame`.
- Prefer explicit model API over UI-driven mutation of 2D arrays.

## Acceptance Criteria
- `GameFrame` no longer owns puzzle algorithm and save/load business logic.
- Auth and save logic no longer directly coupled to UI components.
- Test suite passes (`mvn test`) and package builds (`mvn -DskipTests package`).
- Existing user data and save files can still be read.
