import net.fabricmc.loom.task.RemapJarTask
import com.replaymod.gradle.preprocess.PreprocessTask

plugins {
    java
    id("gg.essential.multi-version")
    id("gg.essential.defaults.repo")
    id("gg.essential.defaults.java")
    id("gg.essential.defaults.loom")
}

val mcVersion = platform.mcVersion

val mcRange = when (project.name) {
    "1.21" -> "1.21-1.21.1"
    "1.21.2" -> "1.21.2-1.21.3"
    "1.21.4" -> "1.21.4"
    "1.21.5" -> "1.21.5"
    "1.21.7" -> "1.21.6-1.21.8"
    "1.21.10" -> "1.21.9-1.21.10"
    "26.1" -> "26.1.x"
    "1.20.6" -> "1.20.5-1.20.6"
    "1.20.4" -> "1.20.3-1.20.4"
    "1.20.2" -> "1.20.2"
    "1.20.1" -> "1.20-1.20.1"
    else -> project.name
}
val modVersion = "1.1.0"

version = "${mcRange}-${modVersion}"
base.archivesName.set("AutoMarker")


if (!platform.isUnobfuscated) {
    loom.mixin.useLegacyMixinAp = true
    loom.mixin.defaultRefmapName.set("automarker.refmap.json")
}

repositories {
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    val fabricApiVersion = when {
        mcVersion >= 26_01_00 -> "0.144.3+26.1"
        mcVersion >= 12110 -> "0.135.0+1.21.10"
        mcVersion >= 12107 -> "0.128.1+1.21.7"
        mcVersion >= 12105 -> "0.119.9+1.21.5"
        mcVersion >= 12104 -> "0.111.0+1.21.4"
        mcVersion >= 12102 -> "0.106.1+1.21.2"
        mcVersion >= 12100 -> "0.100.3+1.21"
        mcVersion >= 12006 -> "0.100.8+1.20.6"
        mcVersion >= 12004 -> "0.97.3+1.20.4"
        mcVersion >= 12002 -> "0.91.6+1.20.2"
        mcVersion >= 12000 -> "0.92.6+1.20.1"
        else -> throw UnsupportedOperationException("Unsupported MC version: $mcVersion")
    }

    val fabricApiModules = mutableListOf(
        "api-base",
        "networking-api-v1",
        "resource-loader-v0",
        "screen-api-v1",
        "lifecycle-events-v1"
    )
    if (mcVersion >= 26_01_00) {
        fabricApiModules.add("key-mapping-api-v1")
    } else {
        fabricApiModules.add("key-binding-api-v1")
    }
    if (mcVersion >= 12109) {
        fabricApiModules.add("resource-loader-v1")
    }

    for (module in fabricApiModules) {
        val dep = fabricApi.module("fabric-$module", fabricApiVersion)
        modImplementation(dep)
    }

    val modMenuVersion = when {
        mcVersion >= 26_01_00 -> "18.0.0-alpha.8"
        mcVersion >= 12110 -> "16.0.0-rc.1"
        mcVersion >= 12107 -> "15.0.0-beta.3"
        mcVersion >= 12105 -> "14.0.0-rc.2"
        mcVersion >= 12104 -> "13.0.0-beta.1"
        mcVersion >= 12102 -> "12.0.0-beta.1"
        mcVersion >= 12100 -> "11.0.0-rc.4"
        mcVersion >= 12006 -> "10.0.0-beta.1"
        mcVersion >= 12004 -> "9.0.0"
        mcVersion >= 12002 -> "8.0.1"
        mcVersion >= 12000 -> "7.2.2"
        else -> null
    }
    if (modMenuVersion != null) {
        modCompileOnly("com.terraformersmc:modmenu:$modMenuVersion")
    }
}

preprocess {
    keywords.set(mapOf(
        ".java" to PreprocessTask.DEFAULT_KEYWORDS,
        ".kt" to PreprocessTask.DEFAULT_KEYWORDS,
        ".json" to PreprocessTask.DEFAULT_KEYWORDS,
        ".mcmeta" to PreprocessTask.DEFAULT_KEYWORDS,
        ".cfg" to PreprocessTask.CFG_KEYWORDS,
        ".vert" to PreprocessTask.DEFAULT_KEYWORDS,
        ".frag" to PreprocessTask.DEFAULT_KEYWORDS
    ))
}

tasks.jar {
    archiveClassifier.set("raw")
    exclude("com/replaymod/**")
}

val bundleJar by tasks.registering(Copy::class) {
    val jarTask = if (platform.isUnobfuscated) tasks.jar else tasks.named<RemapJarTask>("remapJar")
    dependsOn(jarTask)
    
    from(jarTask.flatMap { it.archiveFile })
    into(project.layout.buildDirectory.dir("bundled"))
    
    rename { _ ->
        "AutoMarker-${mcRange}-${modVersion}.jar"
    }
}

java {
    val javaVersion = when {
        mcVersion >= 26_01_00 -> JavaVersion.VERSION_25
        mcVersion >= 12005 -> JavaVersion.VERSION_21
        else -> JavaVersion.VERSION_17
    }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    val javaVersion = when {
        mcVersion >= 26_01_00 -> 25
        mcVersion >= 12005 -> 21
        else -> 17
    }
    options.release.set(javaVersion)
}

tasks.register("printSourceSets") {
    doLast {
        sourceSets.main.get().allSource.srcDirs.forEach {
            println("srcDir: $it")
        }
    }
}

tasks.processResources {
    val formattedName = "AutoMarker $mcRange"
    val fullVersion = "$mcRange-$modVersion"
    val mcDependency = when {
        mcRange.contains("-") -> {
            val parts = mcRange.split("-")
            ">=${parts[0]} <=${parts[1]}"
        }
        else -> mcRange
    }
    val javaDependency = when {
        mcVersion >= 26_01_00 -> "25"
        mcVersion >= 12005 -> "21"
        else -> "17"
    }

    inputs.property("name", formattedName)
    inputs.property("version", fullVersion)
    inputs.property("minecraftDependency", mcDependency)
    inputs.property("javaDependency", javaDependency)

    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "name" to formattedName,
            "version" to fullVersion,
            "minecraftDependency" to mcDependency,
            "javaDependency" to javaDependency
        ))
    }
}
