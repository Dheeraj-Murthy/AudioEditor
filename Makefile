# Audio Editor Makefile
# Integrates Maven builds, native compilation, testing, and deployment
# Supports cross-platform development with automatic platform detection

# ============================================================================
# PROJECT VARIABLES
# ============================================================================

PROJECT_NAME := AudioEditor
MAIN_CLASS := com.meenigam.Main
NATIVE_DIR := native
TARGET_DIR := target
SCRIPTS_DIR := scripts
REPORT_DIR := test-reports

# Java variables
JAVA_VERSION := 22
MAVEN_OPTS := 
MAVEN := mvn

# Native compilation variables
CXX := g++
CXXFLAGS := -shared -fPIC -O2 -std=c++11
LDFLAGS := 

# Platform detection
UNAME_S := $(shell uname -s)
UNAME_M := $(shell uname -m)

ifeq ($(UNAME_S),Linux)
    NATIVE_LIB := $(NATIVE_DIR)/libnative.so
    JNI_INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
    PLATFORM := linux
endif
ifeq ($(UNAME_S),Darwin)
    NATIVE_LIB := $(NATIVE_DIR)/libnative.dylib
    JNI_INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/darwin
    PLATFORM := macos
endif
ifeq ($(OS),Windows_NT)
    NATIVE_LIB := $(NATIVE_DIR)/native.dll
    JNI_INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/win32
    PLATFORM := windows
    CXX := $(shell where g++ 2>nul || echo g++)
endif

# Source files
JAVA_SOURCES := $(shell find src/main/java -name "*.java" 2>/dev/null)
CPP_SOURCES := $(shell find $(NATIVE_DIR) -name "*.cpp" 2>/dev/null)
TEST_SOURCES := $(shell find src/test/java -name "*.java" 2>/dev/null)

# Colors for output
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[0;33m
BLUE := \033[0;34m
PURPLE := \033[0;35m
CYAN := \033[0;36m
NC := \033[0m # No Color

# ============================================================================
# DEFAULT TARGET
# ============================================================================

.PHONY: all clean build test run native help dev ci

# Default target - build everything
all: build

# ============================================================================
# MAIN BUILD TARGETS
# ============================================================================

# Complete build - native library + Java compilation
build: native-lib java-compile
	@echo "$(GREEN)✓ Build completed successfully!$(NC)"
	@echo "$(BLUE)Native Library: $(NATIVE_LIB)$(NC)"
	@echo "$(BLUE)Java Classes: $(TARGET_DIR)/classes$(NC)"

# Native library compilation
native-lib: $(NATIVE_LIB)

$(NATIVE_LIB): $(CPP_SOURCES)
	@echo "$(CYAN)Building native library for $(PLATFORM)...$(NC)"
	@mkdir -p $(NATIVE_DIR)
	@echo "$(YELLOW)Compiler: $(CXX)$(NC)"
	@echo "$(YELLOW)Flags: $(CXXFLAGS)$(NC)"
	@echo "$(YELLOW)Sources: $(CPP_SOURCES)$(NC)"
	$(CXX) $(CXXFLAGS) $(JNI_INCLUDES) $(LDFLAGS) -o $@ $(CPP_SOURCES)
	@chmod 755 $@
	@echo "$(GREEN)✓ Native library built: $@$(NC)"
	@if command -v file >/dev/null 2>&1; then \
		echo "$(BLUE)Library type: $$(file $@)$(NC)"; \
	fi
	@if command -v du >/dev/null 2>&1; then \
		echo "$(BLUE)Library size: $$(du -h $@ | cut -f1)$(NC)"; \
	fi

# Java compilation using Maven
java-compile:
	@echo "$(CYAN)Compiling Java project...$(NC)"
	@if [ ! -f "pom.xml" ]; then \
		echo "$(RED)Error: pom.xml not found. This is not a Maven project.$(NC)"; \
		exit 1; \
	fi
	$(MAVEN) clean compile $(MAVEN_OPTS) -B
	@echo "$(GREEN)✓ Java project compiled successfully$(NC)"

# Test compilation
test-compile: java-compile native-lib
	@echo "$(CYAN)Compiling tests...$(NC)"
	$(MAVEN) test-compile $(MAVEN_OPTS) -B
	@echo "$(GREEN)✓ Tests compiled successfully$(NC)"

# ============================================================================
# TESTING TARGETS
# ============================================================================

# Run all tests
test: test-compile
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(PURPLE)     COMPLETE TEST SUITE EXECUTION     $(NC)"
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(CYAN)Running all test categories...$(NC)"
	@echo "$(BLUE)• Unit Tests - Testing individual components in isolation$(NC)"
	@echo "$(BLUE)• Integration Tests - Testing component interactions$(NC)"
	@echo "$(BLUE)• System Tests - Testing complete workflows$(NC)"
	@echo "$(BLUE)• Acceptance Tests - Testing user scenarios$(NC)"
	@echo "$(PURPLE)----------------------------------------$(NC)"
	@mkdir -p $(REPORT_DIR)
	$(MAVEN) test $(MAVEN_OPTS) -DargLine="-Djava.library.path=$(NATIVE_DIR)" -B
	@echo "$(PURPLE)----------------------------------------$(NC)"
	@echo "$(GREEN)✓ All tests completed successfully$(NC)"

# Run specific test categories
test-unit: test-compile
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(PURPLE)        UNIT TESTS EXECUTION           $(NC)"
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(CYAN)Testing individual components in isolation...$(NC)"
	@echo "$(BLUE)• TestAudioPlayer - Audio playback functionality$(NC)"
	@echo "$(BLUE)• TestCallNative - JNI native library integration$(NC)"
	@echo "$(BLUE)• TestClip - Audio clip management$(NC)"
	@echo "$(BLUE)• TestManager - Application state management$(NC)"
	@echo "$(PURPLE)----------------------------------------$(NC)"
	$(MAVEN) test -Dtest="**/unit/**" $(MAVEN_OPTS) -DargLine="-Djava.library.path=$(NATIVE_DIR)" -B
	@echo "$(GREEN)✓ Unit tests completed$(NC)"

test-integration: test-compile
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(PURPLE)     INTEGRATION TESTS EXECUTION      $(NC)"
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(CYAN)Testing component interactions...$(NC)"
	@echo "$(BLUE)• TestAudioPlayback - Audio system integration$(NC)"
	@echo "$(BLUE)• TestJNIIntegration - Java-Native interface$(NC)"
	@echo "$(PURPLE)----------------------------------------$(NC)"
	$(MAVEN) test -Dtest="**/integration/**" $(MAVEN_OPTS) -DargLine="-Djava.library.path=$(NATIVE_DIR)" -B
	@echo "$(GREEN)✓ Integration tests completed$(NC)"

test-system: test-compile
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(PURPLE)      SYSTEM TESTS EXECUTION           $(NC)"
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(CYAN)Testing complete application workflows...$(NC)"
	@echo "$(BLUE)• TestFullWorkflow - End-to-end audio editing$(NC)"
	@echo "$(PURPLE)----------------------------------------$(NC)"
	$(MAVEN) test -Dtest="**/system/**" $(MAVEN_OPTS) -DargLine="-Djava.library.path=$(NATIVE_DIR)" -B
	@echo "$(GREEN)✓ System tests completed$(NC)"

test-acceptance: test-compile
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(PURPLE)    ACCEPTANCE TESTS EXECUTION         $(NC)"
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(CYAN)Testing user scenarios and requirements...$(NC)"
	@echo "$(BLUE)• TestUserScenarios - Real-world usage patterns$(NC)"
	@echo "$(PURPLE)----------------------------------------$(NC)"
	$(MAVEN) test -Dtest="**/acceptance/**" $(MAVEN_OPTS) -DargLine="-Djava.library.path=$(NATIVE_DIR)" -B
	@echo "$(GREEN)✓ Acceptance tests completed$(NC)"

# Quick test (unit + integration only)
test-quick: test-compile
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(PURPLE)       QUICK TESTS EXECUTION           $(NC)"
	@echo "$(PURPLE)========================================$(NC)"
	@echo "$(CYAN)Running core tests (unit + integration)...$(NC)"
	@echo "$(BLUE)• Unit Tests - Component isolation testing$(NC)"
	@echo "$(BLUE)• Integration Tests - Component interaction testing$(NC)"
	@echo "$(YELLOW)Skipping system and acceptance tests for faster execution$(NC)"
	@echo "$(PURPLE)----------------------------------------$(NC)"
	$(MAVEN) test -Dtest="**/unit/**,**/integration/**" $(MAVEN_OPTS) -DargLine="-Djava.library.path=$(NATIVE_DIR)" -B
	@echo "$(GREEN)✓ Quick tests completed$(NC)"

# ============================================================================
# EXECUTION TARGETS
# ============================================================================

# Run the application
run: build
	@echo "$(CYAN)Starting Audio Editor...$(NC)"
	@if [ ! -f "$(NATIVE_LIB)" ]; then \
		echo "$(RED)Error: Native library not found at $(NATIVE_LIB)$(NC)"; \
		echo "$(YELLOW)Run 'make native-lib' first.$(NC)"; \
		exit 1; \
	fi
	@if [ ! -d "$(TARGET_DIR)/classes" ]; then \
		echo "$(RED)Error: Java classes not found. Run 'make java-compile' first.$(NC)"; \
		exit 1; \
	fi
	@echo "$(BLUE)Java: $$(java -version 2>&1 | head -n1)$(NC)"
	@echo "$(BLUE)Library Path: $(NATIVE_DIR)$(NC)"
	@echo "$(BLUE)Class Path: $(TARGET_DIR)/classes$(NC)"
	java -Djava.library.path=$(NATIVE_DIR) -cp $(TARGET_DIR)/classes $(MAIN_CLASS)

# Debug mode
debug: build
	@echo "$(CYAN)Starting Audio Editor in debug mode...$(NC)"
	@echo "$(YELLOW)Debug JVM will listen on port 5005$(NC)"
	java -Djava.library.path=$(NATIVE_DIR) -Ddebug=true -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -cp $(TARGET_DIR)/classes $(MAIN_CLASS)

# Profile mode
profile: build
	@echo "$(CYAN)Starting Audio Editor with profiling...$(NC)"
	java -Djava.library.path=$(NATIVE_DIR) -Xprof -cp $(TARGET_DIR)/classes $(MAIN_CLASS)

# ============================================================================
# QUALITY AND ANALYSIS TARGETS
# ============================================================================



# Run code quality checks
quality:
	@echo "$(CYAN)Running code quality checks...$(NC)"
	@if command -v pmd >/dev/null 2>&1; then \
		echo "$(YELLOW)Running PMD analysis...$(NC)"; \
		$(MAVEN) pmd:pmd || true; \
	fi
	@if command -v spotbugs >/dev/null 2>&1; then \
		echo "$(YELLOW)Running SpotBugs analysis...$(NC)"; \
		$(MAVEN) spotbugs:check || true; \
	fi
	@echo "$(GREEN)✓ Code quality checks completed$(NC)"

# Generate documentation
docs:
	@echo "$(CYAN)Generating documentation...$(NC)"
	$(MAVEN) javadoc:javadoc
	@echo "$(GREEN)✓ Documentation generated$(NC)"
	@echo "$(BLUE)Javadoc: target/site/apidocs/index.html$(NC)"

# ============================================================================
# MAINTENANCE TARGETS
# ============================================================================

# Clean build artifacts
clean:
	@echo "$(CYAN)Cleaning build artifacts...$(NC)"
	$(MAVEN) clean $(MAVEN_OPTS) || true
	@rm -f $(NATIVE_DIR)/*.so $(NATIVE_DIR)/*.dylib $(NATIVE_DIR)/*.dll $(NATIVE_DIR)/*.o
	@rm -rf $(TARGET_DIR)
	@rm -rf $(REPORT_DIR)
	@echo "$(GREEN)✓ Build artifacts cleaned$(NC)"

# Clean everything (including dependencies)
clean-all: clean
	@echo "$(CYAN)Cleaning all generated files...$(NC)"
	$(MAVEN) clean $(MAVEN_OPTS) || true
	@rm -rf $(TARGET_DIR) $(REPORT_DIR) .mvn dependency-reduced-pom.xml
	@find . -name "*.class" -delete 2>/dev/null || true
	@find . -name "*.jar" -delete 2>/dev/null || true
	@echo "$(GREEN)✓ All generated files cleaned$(NC)"

# Install/update dependencies
deps:
	@echo "$(CYAN)Installing/updating dependencies...$(NC)"
	$(MAVEN) dependency:resolve
	$(MAVEN) dependency:copy-dependencies
	@echo "$(GREEN)✓ Dependencies resolved$(NC)"

# Check for required tools
check-tools:
	@echo "$(CYAN)Checking required tools...$(NC)"
	@command -v java >/dev/null 2>&1 || (echo "$(RED)✗ Java not found$(NC)" && exit 1)
	@echo "$(GREEN)✓ Java: $$(java -version 2>&1 | head -n1)$(NC)"
	@command -v $(MAVEN) >/dev/null 2>&1 || (echo "$(RED)✗ Maven not found$(NC)" && exit 1)
	@echo "$(GREEN)✓ Maven: $$(mvn -version 2>&1 | head -n1)$(NC)"
	@command -v $(CXX) >/dev/null 2>&1 || (echo "$(RED)✗ C++ compiler not found$(NC)" && exit 1)
	@echo "$(GREEN)✓ C++ Compiler: $(CXX)$(NC)"
	@if [ -n "$(JAVA_HOME)" ]; then \
		echo "$(GREEN)✓ JAVA_HOME: $(JAVA_HOME)$(NC)"; \
	else \
		echo "$(YELLOW)⚠ JAVA_HOME not set$(NC)"; \
	fi
	@echo "$(GREEN)✓ All required tools available$(NC)"

# ============================================================================
# PACKAGING AND DEPLOYMENT TARGETS
# ============================================================================

# Package application
package: build test
	@echo "$(CYAN)Packaging application...$(NC)"
	$(MAVEN) package $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Application packaged$(NC)"
	@echo "$(BLUE)JAR: target/$(PROJECT_NAME)-1.0-SNAPSHOT.jar$(NC)"

# Create distribution package
dist: clean build test docs package
	@echo "$(CYAN)Creating distribution package...$(NC)"
	@mkdir -p dist
	@cp -r $(NATIVE_LIB) dist/
	@cp target/$(PROJECT_NAME)-1.0-SNAPSHOT.jar dist/
	@cp -r scripts dist/
	@cp README.md dist/
	@cp -r docs dist/
	@tar -czf $(PROJECT_NAME)-$(PLATFORM)-dist.tar.gz -C dist .
	@echo "$(GREEN)✓ Distribution package created$(NC)"
	@echo "$(BLUE)Package: $(PROJECT_NAME)-$(PLATFORM)-dist.tar.gz$(NC)"

# ============================================================================
# DEVELOPMENT WORKFLOWS
# ============================================================================

# Development workflow (clean, build, run)
dev: clean build run

# Continuous integration workflow
ci: clean check-tools build test coverage package
	@echo "$(GREEN)✓ CI pipeline completed successfully$(NC)"

# Quick development cycle (for rapid iteration)
dev-quick: java-compile run

# ============================================================================
# LEGACY SCRIPT INTEGRATION
# ============================================================================

# Run existing shell scripts for compatibility
run-native-script:
	@echo "$(CYAN)Running native build script...$(NC)"
	@chmod +x $(SCRIPTS_DIR)/build_native.sh
	@$(SCRIPTS_DIR)/build_native.sh

run-project-script:
	@echo "$(CYAN)Running project script...$(NC)"
	@chmod +x $(SCRIPTS_DIR)/run_project.sh
	@$(SCRIPTS_DIR)/run_project.sh

run-tests-script:
	@echo "$(CYAN)Running tests script...$(NC)"
	@chmod +x $(SCRIPTS_DIR)/run_all_tests.sh
	@$(SCRIPTS_DIR)/run_all_tests.sh

# ============================================================================
# HELP TARGET
# ============================================================================

help:
	@echo "$(PURPLE)Audio Editor Makefile$(NC)"
	@echo "$(PURPLE)=====================$(NC)"
	@echo ""
	@echo "$(CYAN)Build Targets:$(NC)"
	@echo "  $(GREEN)all$(NC)          - Build everything (default)"
	@echo "  $(GREEN)build$(NC)        - Build native library and Java code"
	@echo "  $(GREEN)native-lib$(NC)   - Build C++ native library only"
	@echo "  $(GREEN)java-compile$(NC) - Compile Java code only"
	@echo "  $(GREEN)test-compile$(NC) - Compile tests only"
	@echo ""
	@echo "$(CYAN)Testing Targets:$(NC)"
	@echo "  $(GREEN)test$(NC)         - Run all tests"
	@echo "  $(GREEN)test-unit$(NC)    - Run unit tests only"
	@echo "  $(GREEN)test-integration$(NC) - Run integration tests only"
	@echo "  $(GREEN)test-system$(NC)  - Run system tests only"
	@echo "  $(GREEN)test-acceptance$(NC) - Run acceptance tests only"
	@echo "  $(GREEN)test-quick$(NC)   - Run unit + integration tests"

	@echo ""
	@echo "$(CYAN)Execution Targets:$(NC)"
	@echo "  $(GREEN)run$(NC)          - Run the application"
	@echo "  $(GREEN)debug$(NC)        - Run in debug mode (port 5005)"
	@echo "  $(GREEN)profile$(NC)       - Run with profiling enabled"
	@echo ""
	@echo "$(CYAN)Quality Targets:$(NC)"
	@echo "  $(GREEN)quality$(NC)       - Run code quality checks"
	@echo "  $(GREEN)docs$(NC)          - Generate documentation"
	@echo "  $(GREEN)check-tools$(NC)   - Verify required tools are installed"
	@echo ""
	@echo "$(CYAN)Maintenance Targets:$(NC)"
	@echo "  $(GREEN)clean$(NC)        - Clean build artifacts"
	@echo "  $(GREEN)clean-all$(NC)    - Clean all generated files"
	@echo "  $(GREEN)deps$(NC)         - Install/update dependencies"
	@echo ""
	@echo "$(CYAN)Packaging Targets:$(NC)"
	@echo "  $(GREEN)package$(NC)       - Package application for distribution"
	@echo "  $(GREEN)dist$(NC)          - Create complete distribution package"
	@echo ""
	@echo "$(CYAN)Development Workflows:$(NC)"
	@echo "  $(GREEN)dev$(NC)           - Development workflow (clean, build, run)"
	@echo "  $(GREEN)dev-quick$(NC)     - Quick development cycle"
	@echo "  $(GREEN)ci$(NC)            - Continuous integration workflow"
	@echo ""
	@echo "$(CYAN)Legacy Script Integration:$(NC)"
	@echo "  $(GREEN)run-native-script$(NC)    - Run native build script"
	@echo "  $(GREEN)run-project-script$(NC)   - Run project runner script"
	@echo "  $(GREEN)run-tests-script$(NC)     - Run test suite script"
	@echo ""
	@echo "$(YELLOW)Examples:$(NC)"
	@echo "  make                    # Build everything"
	@echo "  make run                # Build and run application"
	@echo "  make test               # Run all tests"
	@echo "  make test-unit          # Run unit tests only"

	@echo "  make dev                # Clean, build, and run"
	@echo "  make debug              # Run in debug mode"
	@echo "  make clean              # Clean build artifacts"
	@echo ""
	@echo "$(BLUE)Platform detected: $(PLATFORM)$(NC)"
	@echo "$(BLUE)Native library: $(NATIVE_LIB)$(NC)"
	@echo "$(BLUE)Main class: $(MAIN_CLASS)$(NC)"

# ============================================================================
# PHONY TARGETS DECLARATION
# ============================================================================

.PHONY: all build test run clean deps docs coverage quality package dist check-tools
.PHONY: native-lib java-compile test-compile test-unit test-integration test-system test-acceptance test-quick
.PHONY: debug profile clean-all dev ci dev-quick
.PHONY: run-native-script run-project-script run-tests-script
.PHONY: help

# ============================================================================
# DEPENDENCY RULES
# ============================================================================

# Ensure native library is built before running
run: $(NATIVE_LIB)

# Ensure tests are compiled before running
test: test-compile
test-unit: test-compile
test-integration: test-compile
test-system: test-compile
test-acceptance: test-compile
test-quick: test-compile

# Ensure Java is compiled before packaging
package: java-compile

# Ensure everything is built for distribution
dist: build test docs package

# ============================================================================
# SPECIAL TARGETS
# ============================================================================

# Show project information
info:
	@echo "$(PURPLE)Audio Editor Project Information$(NC)"
	@echo "$(PURPLE)=============================$(NC)"
	@echo "$(CYAN)Project Name:$(NC) $(PROJECT_NAME)"
	@echo "$(CYAN)Main Class:$(NC) $(MAIN_CLASS)"
	@echo "$(CYAN)Platform:$(NC) $(PLATFORM)"
	@echo "$(CYAN)Architecture:$(NC) $(UNAME_M)"
	@echo "$(CYAN)Java Version:$(NC) $$(java -version 2>&1 | head -n1)"
	@echo "$(CYAN)Maven Version:$(NC) $$(mvn -version 2>&1 | head -n1)"
	@echo "$(CYAN)C++ Compiler:$(NC) $(CXX)"
	@echo "$(CYAN)Native Library:$(NC) $(NATIVE_LIB)"
	@echo ""
	@echo "$(CYAN)Source Files:$(NC)"
	@echo "  Java: $$(find src/main/java -name "*.java" 2>/dev/null | wc -l | tr -d ' ')"
	@echo "  C++: $$(find $(NATIVE_DIR) -name "*.cpp" 2>/dev/null | wc -l | tr -d ' ')"
	@echo "  Tests: $$(find src/test/java -name "*.java" 2>/dev/null | wc -l | tr -d ' ')"

# Watch for changes and rebuild (requires inotify-tools)
watch:
	@echo "$(CYAN)Watching for changes...$(NC)"
	@echo "$(YELLOW)Note: Requires 'inotify-tools' package$(NC)"
	@while inotifywait -r -e modify,create,delete src/main/java $(NATIVE_DIR); do \
		echo "$(GREEN)Changes detected, rebuilding...$(NC)"; \
		make build; \
	done

# Backup project
backup:
	@echo "$(CYAN)Creating project backup...$(NC)"
	@BACKUP_DIR="backup-$$(date +%Y%m%d_%H%M%S)"; \
	mkdir -p ../$$BACKUP_DIR; \
	cp -r . ../$$BACKUP_DIR/; \
	echo "$(GREEN)✓ Backup created: ../$$BACKUP_DIR$(NC)"

# Restore from backup
restore:
	@echo "$(CYAN)Available backups:$(NC)"
	@ls -1d ../backup-* 2>/dev/null || echo "$(YELLOW)No backups found$(NC)"
	@echo "$(YELLOW)Usage: make restore BACKUP=backup-YYYYMMDD_HHMMSS$(NC)"