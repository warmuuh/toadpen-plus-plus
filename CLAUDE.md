# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ToadPen++ is a Java Swing-based text editor written in Java 21. It features a modern docking interface, syntax highlighting, file tree navigation, and integration with macOS system features.

## Build Commands

### Standard Build
```bash
mvn clean package
```

This builds the application and creates platform-specific distributions for Windows, Linux, and macOS with bundled JREs.

### Running the Application
```bash
# Run from JAR
java -jar toadpen-core/target/toadpen-core-1.0-SNAPSHOT.jar

# Or run the main class directly
java -cp toadpen-core/target/toadpen-core-1.0-SNAPSHOT.jar wrm.toadpen.core.Application
```

### Native Image Compilation

The project supports GraalVM native image compilation using Bellsoft Native Image Kit 24.1.1.r23-nik:

```bash
# First run with agent to collect metadata
java -agentlib:native-image-agent=config-output-dir=toadpen-core/src/main/resources wrm.toadpen.core.Application

# Then prepare metadata
mkdir -p toadpen-core/target/native/agent-output/main
cp toadpen-core/src/main/resources/reachability-metadata.json toadpen-core/target/native/agent-output/main

# Compile native image
mvn package -Pnative

# Run native binary
toadpen-core/target/toadpen-core -Dflatlaf.useNativeLibrary=false
```

**Known Issues**: Window resizing throws native exceptions in the native image build.

### Java Version

- **Required**: Java 21 (configured in `.sdkmanrc`)
- SDK Manager users: `sdk env` to auto-configure Java version

## Architecture

### Module Structure

The project is a multi-module Maven build:
- **toadpen-parent**: Parent POM defining properties and repositories
- **toadpen-core**: Core application logic and UI
- **toadpen-dist**: Distribution packaging for Windows, Linux, and macOS

### Core Application Architecture

#### Entry Point
- Main class: `wrm.toadpen.core.Application:9`
- Uses Avaje dependency injection framework
- Entry point initializes macOS settings and starts `ApplicationController`

#### Dependency Injection (Avaje)
The application uses Avaje Inject for dependency injection. Components are annotated with:
- `@Singleton` for singleton components
- `@Component` for general components
- `@PostConstruct` for initialization methods
- `@QualifiedMap` for map-based injection (used in `ToolManager`)

#### Command Pattern
Commands are the primary way to trigger actions in the application:
- **CommandManager** (`wrm.toadpen.core.cmd.CommandManager:17`): Central registry and executor
  - Manages `CommandNoArg` instances (commands without arguments)
  - Manages `CommandExecutor` instances for commands with arguments
  - Commands are registered by ID and can be executed by ID or instance
- Command implementations:
  - `ApplicationCommands`: Application-level commands (settings, exit, etc.)
  - `EditorCommands`: Editor-specific commands (syntax selection, etc.)
  - `FileCommands`: File operations (open, save, etc.)
  - `FileTreeCommands`: File tree operations (create, delete, rename, etc.)

#### Event System
The application uses a custom event system for UI communication:
- **UiEvent** (`wrm.toadpen.core.ui.UiEvent:9`): Events with no arguments
- **UiEvent1<T>** (`wrm.toadpen.core.ui.UiEvent1:10`): Events with one argument
- **BufferedUiEvent1<T>**: Events that buffer values for late subscribers

Events are used throughout the application for loose coupling between components. For example:
- `MainWindow.OnNewEditorAdded` fires when a new editor is created
- `FileTree.OnFileDoubleClicked` fires when a file is double-clicked
- `ApplicationModel.OnProjectDirectoryChanged` fires when the project directory changes

The `ApplicationController` wires these events together during initialization.

#### Main Components

**ApplicationController** (`wrm.toadpen.core.cmd.ApplicationController:21`)
- Central controller that wires together all major components
- Connects UI events to command execution
- Initializes all components via `@PostConstruct`
- Coordinates startup and shutdown behaviors

**MainWindow** (`wrm.toadpen.core.ui.MainWindow:43`)
- Main application window using Modern Docking library
- Manages editor tabs using dockable panels
- Handles theme switching (light/dark mode based on OS)
- Wraps `EditorComponent` instances in `EditorDockingWrapper` for docking support
- Manages editor lifecycle (open, focus, close, dirty state)

**EditorComponent** (`wrm.toadpen.core.ui.editor.EditorComponent`)
- Text editor component using RSyntaxTextArea
- Supports syntax highlighting, line numbers, code folding
- Features multi-caret editing capability
- Manages file associations and dirty state tracking

**FileTree** (`wrm.toadpen.core.ui.filetree.FileTree`)
- File system browser tree component
- Fires events for file operations (open, create, delete, rename)
- Integrates with file watcher for automatic refresh

**ApplicationModel** (`wrm.toadpen.core.model.ApplicationModel:9`)
- Holds application-wide state
- Currently manages project directory
- Uses events to notify listeners of state changes

#### Key Supporting Systems

**File Watching** (`wrm.toadpen.core.watchdog.FileWatchDog`)
- Monitors open files for external changes
- Integrates with editors to prompt for reload when files change

**Search System** (`wrm.toadpen.core.search.*`)
- Two implementations: `SimpleSearchService` and `LuceneSearchService`
- Provides full-text search across project files
- UI component: `SearchResultDialog`

**Tool System** (`wrm.toadpen.core.tools.ToolManager:15`)
- Extensible system for text manipulation tools
- Tools operate on selected text or entire editor content
- Uses `@QualifiedMap` injection to discover and register tools
- Tools implement `TextTool` interface with `execute(TextContext)` method

**Keyboard Bindings** (`wrm.toadpen.core.key.KeySetBinding`)
- Manages application-wide keyboard shortcuts
- Binds to commands via CommandManager

#### UI Layer Structure
- `wrm.toadpen.core.ui.*`: Main UI components
- `wrm.toadpen.core.ui.editor.*`: Editor-related components
- `wrm.toadpen.core.ui.filetree.*`: File tree navigation
- `wrm.toadpen.core.ui.menu.*`: Menu system (ApplicationMenu)
- `wrm.toadpen.core.ui.dialogs.*`: Dialog windows
- `wrm.toadpen.core.ui.options.*`: Settings/options panels
- `wrm.toadpen.core.ui.os.*`: OS-specific handlers (OsHandler)
- `wrm.toadpen.core.ui.macos.*`: macOS-specific integrations

### Key Dependencies
- **FlatLaf**: Modern look and feel for Swing
- **RSyntaxTextArea**: Syntax highlighting text editor component
- **Modern Docking**: Docking framework for editor tabs
- **Avaje Inject**: Lightweight dependency injection
- **JediTerm**: Terminal emulator component
- **Lombok**: Reduces boilerplate code
- **GlazedLists**: List transformations for UI
- **llama**: LLM integration library
- **Commons IO & Configuration**: File and configuration utilities

## Adding New Features

### Adding a New Command

1. Create command in appropriate `*Commands` class:
```java
public static final String MY_COMMAND_ID = "my.command";
```

2. Register in the commands class constructor:
```java
new CommandNoArg(MY_COMMAND_ID, "Description", "icon-name", () -> {
    // command implementation
})
```

3. The command will be automatically registered in `CommandManager`

### Adding a New Tool

1. Create a class annotated with `@Named("tool-id")` and `@Singleton`
2. Implement `ToolManager.TextTool` interface
3. Inject it with `@QualifiedMap` in ToolManager
4. The tool will be automatically discovered and added to the menu

### Adding a New Event Listener

Events follow the pattern:
```java
component.OnEventName.addListener(data -> {
    // handle event
});
```

Wire event listeners in `ApplicationController.init()` to maintain centralized event flow.

### Working with Editors

To get the active editor:
```java
EditorComponent editor = mainWindow.getActiveEditor();
```

To open a new file in an editor, use the `FileCommands.OpenFileCommand` via the CommandManager.

## Code Conventions

- Uses Lombok annotations (`@RequiredArgsConstructor`, `@Getter`, `@Value`, etc.) extensively
- Dependency injection via Avaje annotations
- Event-driven architecture for UI communication
- Command pattern for user actions
- All UI operations should be performed on the Swing EDT via `SwingUtilities.invokeLater()`
