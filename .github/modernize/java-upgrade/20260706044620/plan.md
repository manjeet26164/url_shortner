# Upgrade Plan: url-shortener (20260706044620)

- **Generated**: 2026-07-06 00:46:20
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

## Available Tools

**JDKs**
- JDK 17: not available (baseline will be skipped)
- JDK 25: **<TO_BE_INSTALLED>** (required by Step 1 and Step 4)

**Build Tools**
- Maven 3.9.15: **<TO_BE_INSTALLED>** (required by Step 1, no wrapper present)

## Guidelines

- Upgrade the runtime to the latest Java LTS target requested by the user.
- Keep changes minimal and focused on build/runtime compatibility.
- No version control is available in this workspace; changes will remain uncommitted.

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260706044620
- Run tests before and after the upgrade: true

## Upgrade Goals

- Java 25

## Technology Stack

| Technology/Dependency | Current | Min Compatible Version | Why Incompatible |
| --- | --- | --- | --- |
| Java | 17 | 25 | User requested latest LTS runtime |
| Spring Boot starter parent | 3.3.1 | 4.1.0 | Boot 3.3.1 packaging plugin failed on Java 25 class files during validation |
| Maven | unavailable | 3.9.15 | Required to build and validate the project in this environment |

## Derived Upgrades

- Maven 3.9.15: needed because no Maven installation is available and the project has no wrapper.
- JDK 25: required to compile and validate the upgraded runtime target.
- Spring Boot 4.1.0: required because Spring Boot 3.3.1 failed during repackaging on Java 25.

## Impact Analysis

### Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
| --- | --- | --- | --- | --- | --- |
| pom.xml | java.version | 17 | upgrade | 25 | Raise the compiler/runtime target to Java 25 |
| pom.xml | spring-boot-starter-parent | 3.3.1 | upgrade | 4.1.0 | Spring Boot 3.3.1 failed during repackage on Java 25 |

### Source Code Changes

| File | Location | Current | Required Change | Reason |
| --- | --- | --- | --- | --- |
| pom.xml | properties/java.version | 17 | Set to 25 | Align build with the target JDK |

### Configuration Changes

None identified.

### CI/CD Changes

None identified in the workspace.

### Risks & Warnings

- **Java 25 runtime validation**: The project has no local tests, so a successful compile may miss runtime-only incompatibilities. **Mitigation**: run the full Maven test phase under JDK 25 and treat any failures as upgrade blockers.
- **Spring Boot parent compatibility**: The project currently uses Spring Boot 3.3.1. It may compile cleanly on Java 25, but if validation surfaces compatibility issues, the minimal fix is to adjust the Boot parent only as far as required.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Install the exact toolchain needed to execute the upgrade and validations.
  - **Changes to Make**: Install JDK 25 and Maven 3.9.15.
  - **Verification**: Check installed JDKs and Maven paths are available after installation.

- Step 2: Setup Baseline
  - **Rationale**: The current JDK 17 is not available in this environment, so a baseline run cannot be performed.
  - **Changes to Make**: No code changes.
  - **Verification**: Mark the step skipped and proceed directly to the upgrade.

- Step 3: Raise the Java Target to 25
  - **Rationale**: This is the functional change that moves the runtime/build target to the requested LTS.
  - **Changes to Make**: Update the Java version property in [pom.xml](pom.xml).
  - **Verification**: Run `mvn clean test-compile -q` with JDK 25.

- Step 4: Upgrade Spring Boot Parent for Java 25 Compatibility
  - **Rationale**: Final validation showed Spring Boot 3.3.1 could not repackage Java 25 class files.
  - **Changes to Make**: Upgrade the Spring Boot parent in [pom.xml](pom.xml) to 4.1.0.
  - **Verification**: Run `mvn clean verify -Djacoco.skip=false` with JDK 25.

- Step 5: Final Validation
  - **Rationale**: Confirm the upgraded build remains healthy under the new runtime target.
  - **Changes to Make**: No code changes unless validation surfaces a blocker.
  - **Verification**: Run `mvn clean test -q` with JDK 25.
