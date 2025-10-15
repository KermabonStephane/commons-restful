# How to use the commons-restful library

This guide explains how to incorporate the `commons-restful` library into your Maven or Gradle project and configure it
to fetch from the GitHub Packages repository.

## GitHub Packages Repository Configuration

To use this library, you need to configure your build tool to use the GitHub Packages repository associated with this
project.

## Maven Project Setup

### 1. Configure `settings.xml`

Add the following to your `~/.m2/settings.xml` file. This configures Maven to use the GitHub Packages repository and
authenticates using your GitHub username and the read-only token you created.

```xml

<settings>
    <servers>
        <server>
            <id>github</id>
            <username>KermabonStephane</username>
            <!-- This token is use to read the package from GitHub. This right is the only right gives to this token -->
            <password>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</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>github</id>
                    <name>GitHub Kermabon Stéphae Apache Maven Packages</name>
                    <url>https://maven.pkg.github.com/stephanekermabon/commons-restful</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>
</settings>
```

**Note:** Replace `YOUR_GITHUB_USERNAME` with your GitHub username and `YOUR_READ_ONLY_GITHUB_TOKEN` with the token you
generated.

### 2. Add Dependency to `pom.xml`

Add the following dependency to your project's `pom.xml`:

```xml

<dependencies>
    ...
    <dependency>
        <groupId>com.demis27</groupId>
        <artifactId>commons-restful</artifactId>
        <version>1.0.0</version> <!-- Use the desired version -->
    </dependency>
    ...
</dependencies>
```

---

## Gradle Project Setup

### 1. Configure Repository in `build.gradle`

Add the Maven repository to your `build.gradle` (for Groovy) or `build.gradle.kts` (for Kotlin) file. It is recommended
to use environment variables to store your credentials.

**For Groovy (`build.gradle`):**

```groovy
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/stephanekermabon/commons-restful")
        credentials {
            username = System.getenv("KermabonStephane")
            password = System.getenv("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")
        }
    }
}
```

**For Kotlin (`build.gradle.kts`):**

```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/stephanekermabon/commons-restful")
        credentials {
            username = System.getenv("KermabonStephane")
            password = System.getenv("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")
        }
    }
}
```

You will need to set the `GITHUB_ACTOR` ; `KermabonStephane` (your GitHub username) and `GITHUB_TOKEN` ;
`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` (your read-only token) environment variables in your system or CI/CD
environment.

### 2. Add Dependency

Add the library as a dependency in your build file.

**For Groovy (`build.gradle`):**

```groovy
dependencies {
    ...
    implementation 'com.demis27:commons-restful:1.0.0' // Use the desired version
}
```

**For Kotlin (`build.gradle.kts`):**

```kotlin
dependencies {
    ...
    implementation("com.demis27:commons-restful:1.0.0") // Use the desired version
}
```
