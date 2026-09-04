plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
    id("fabric-loom") version "1.17-SNAPSHOT"
}

val minecraftVersion = "1.21.11"
val loaderVersion = "0.19.3"
val fabricApiVersion = "0.141.4+1.21.11"
val minecraftDependency = ">=1.21.11 <1.22"
val targetFamily = "1.21.11"

val modVersion: String by project
val mavenGroup: String by project
val modId: String by project
group = mavenGroup
version = modVersion

repositories {
    maven("https://api.modrinth.com/maven")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://maven.izzel.io/releases/")
    maven("https://jitpack.io/")
}

// shadowImplementation carries libraries that must be bundled into the mod jar and relocated.
val shadowImplementation = configurations.create("shadowImplementation")
configurations {
    named("shadow").get().extendsFrom(shadowImplementation)
    named("implementation").get().extendsFrom(shadowImplementation)
}

configurations.named("minecraftRuntimeLibraries") {
    exclude(group = "org.lwjgl")
    exclude(group = "com.mojang", module = "jtracy")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // fabric-loom's dependency methods are dynamic; add() + the `loom` extension accessor are the
    // reliable forms in Kotlin DSL.
    add("minecraft", "com.mojang:minecraft:$minecraftVersion")
    add("mappings", loom.officialMojangMappings())
    add("modImplementation", "net.fabricmc:fabric-loader:$loaderVersion")
    add("modImplementation", "net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    add("modCompileOnly", "maven.modrinth:jei:pw6C92V4")
    add("modCompileOnly", "maven.modrinth:jade:AMBKaYce")

    // AllMusic fetches audio over HTTP; bundle Apache HttpComponents 5 (shadowImplementation) and
    // relocate it so it does not clash with anything else on the client.
    add("shadowImplementation", "org.apache.httpcomponents.client5:httpclient5:5.6.1")
    add("shadowImplementation", "org.apache.httpcomponents.core5:httpcore5:5.4.2")
    add("shadowImplementation", "org.apache.httpcomponents.core5:httpcore5-h2:5.4.2")

    add("compileOnly", "icyllis.modernui:ModernUI-Fabric:26.1.2-3.13.0.4")
    add("compileOnly", "com.google.code.gson:gson:2.14.0")
    add("compileOnly", "org.apache.logging.log4j:log4j-core:2.25.4")
    add("compileOnly", "org.jspecify:jspecify:1.0.0")
}

sourceSets {
    main {
        java.srcDir("../protocol/src/main/java")
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    test {
        failOnNoDiscoveredTests.set(false)
    }
    processResources {
        filteringCharset = "UTF-8"
        val props = mapOf(
            "mod_id" to modId,
            "mod_version" to modVersion,
            "minecraft_version" to minecraftVersion,
            "minecraft_dependency" to minecraftDependency,
            "bridge_target" to targetFamily,
            "java_version" to 21,
            "loader_version" to loaderVersion
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") {
            expand(props)
        }
        filesMatching("bridge-target.properties") {
            expand(props)
        }
    }
    shadowJar {
        archiveClassifier.set("shadow")
        relocate("org.apache.hc.core5", "com.coloryr.allmusic.libs.org.apache.hc.core5")
        relocate("org.apache.hc.client5", "com.coloryr.allmusic.libs.org.apache.hc.client5")
        relocate("org.slf4j", "com.coloryr.allmusic.libs.org.slf4j")
        configurations = listOf(shadowImplementation)
    }
    jar {
        archiveBaseName.set("$modId-1.21.11")
        archiveClassifier.set("")
    }
    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveFileName.set("$modId-1.21.11-$modVersion.jar")
    }
    build {
        dependsOn(remapJar)
    }
    register<JavaExec>("channelTest") {
        dependsOn(testClasses)
        classpath = sourceSets.test.get().runtimeClasspath
        mainClass.set("com.ceclientmod.net.ChannelIdentifierTest")
    }
}
