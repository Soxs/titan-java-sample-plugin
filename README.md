# Titan Java Sample Plugin

Runnable sample plugin and starter project for TitanClient's Java SDK.

This repository demonstrates:

- `@PluginDescriptor` metadata.
- Guice injection with `@Inject`.
- RuneLite-style config interfaces.
- Event subscriptions through `@Subscribe`.
- Direct `Client` accessors such as `client.tick()` and `client.localPlayer()`.

## Use This As Your Plugin Starter

Copy or fork this repository for a new Java plugin. Then:

1. Rename the `net.titan.sample` package and classes.
2. Update `@PluginDescriptor` with your plugin ID, name, author, and version.
3. Update `SamplePluginConfig` or replace it with your own config interface.
4. Change the output JAR name in `build.gradle`.

## Requirements

- Java 11 or newer.
- TitanClient installed with the Java worker runtime.
- A TitanClient account with the `feature.debug_mode` entitlement.

The sample uses the published SDK dependency:

```gradle
repositories {
    maven { url = uri('https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases') }
    mavenCentral()
}

compileOnly 'net.titan:titan-plugin-api:0.1.0'
```

The sample already points at the public Titan SDK Maven repository. Pass
`-PtitanMavenRepositoryUrl=...` or set `TITAN_MAVEN_REPOSITORY_URL` only when
testing another repository.

For local SDK work, publish from the SDK checkout first:

```powershell
cd path\to\titan-public-sdk\java
.\gradlew.bat publishToMavenLocal
```

This project checks Maven Local before remote repositories.

## Build

```powershell
.\gradlew.bat clean build
```

The plugin JAR is written to:

```text
build\libs\titan-sample-plugin.jar
```

## Run In TitanClient

Edit the included `gradle.properties` file and set `titanClientRoot` to the
folder that directly contains `controller.exe`:

```properties
titanClientRoot=C:/Program Files/TitanClient
```

That TitanClient folder must include the Java worker runtime at:

```text
java/titan-java-worker.jar
```

Then run either task:

```powershell
.\gradlew.bat runViaTitan
```

`runTitanClient`/`runViaTitan` will also try common TitanClient install folders
and local source-build output folders if `titanClientRoot` is left blank.

You can still use a one-off Gradle property:

```powershell
.\gradlew.bat runViaTitan "-PtitanClientRoot=C:\Program Files\TitanClient"
```

Or set it for the current PowerShell session:

```powershell
$env:TITAN_CLIENT_ROOT = "C:\Program Files\TitanClient"
.\gradlew.bat runViaTitan
```

For a local TitanClient source build, build `controller` first and use the
build output folder. Make sure Java runtime staging is enabled:

```powershell
cmake -S . -B build -DTITAN_BUILD_JAVA_RUNTIME=ON
cmake --build build --config Release --target controller
.\gradlew.bat runViaTitan "-PtitanClientRoot=C:\path\to\SoxClientOSRS\build\controller\Release"
```

`runViaTitan` is an alias for `runTitanClient`.

The task builds the sample, copies it to
`%USERPROFILE%\.titanclient\plugins`, and launches:

```text
controller.exe --dev-mode --launch-new-client
```

Set `TITAN_PLUGIN_DIR` or pass `-PtitanPluginDir=...` to override the staging
directory.

## API Packages

- `net.titan.api` for `Client`, `Player`, and `Logger`.
- `net.titan.api.plugins` for `Plugin` and `PluginDescriptor`.
- `net.titan.api.config` for config annotations.
- `net.titan.api.events` and `net.titan.api.eventbus` for subscriptions.
