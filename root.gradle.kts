plugins {
    id("gg.essential.multi-version.root")
    id("gg.essential.loom") version "1.15.48" apply false
}

group = "com.example"
version = "1.0.0"

val bundleJar by tasks.registering(Copy::class) {
    into("$buildDir/libs")
}

subprojects {
    buildscript {
        repositories {
            maven("https://jitpack.io")
        }
    }
    
    val mcVersion = name.split("-")[0].split(".")
    val major = mcVersion[0].toInt()
    val minor = mcVersion[1].toInt()
    val patch = if (mcVersion.size > 2) mcVersion[2].toInt() else 0
    val mcVersionCode = major * 10000 + minor * 100 + patch
    
    extra.set("loom.platform", "fabric")

    afterEvaluate {
        val subProjectBundleJar = project.tasks.findByName("bundleJar")
        if (subProjectBundleJar != null && project.name != "1.21.11") {
            bundleJar.configure {
                dependsOn(subProjectBundleJar)
                from(subProjectBundleJar.outputs.files)
            }
        }
    }
}

defaultTasks("bundleJar")

preprocess {
    strictExtraMappings.set(true)

    val mc26_01_00 = createNode("26.1", 26_01_00, "yarn")
    val mc12111 = createNode("1.21.11", 12111, "yarn")
    val mc12110 = createNode("1.21.10", 12110, "yarn")
    val mc12107 = createNode("1.21.7", 12107, "yarn")
    val mc12105 = createNode("1.21.5", 12105, "yarn")
    val mc12104 = createNode("1.21.4", 12104, "yarn")
    val mc12102 = createNode("1.21.2", 12102, "yarn")
    val mc12100 = createNode("1.21", 12100, "yarn")

    mc26_01_00.link(mc12111, file("versions/mapping-fabric-26.1-1.21.11.txt"))
    mc12111.link(mc12110, file("versions/mapping-fabric-1.21.11-1.21.10.txt"))
    mc12110.link(mc12107, file("versions/mapping-fabric-1.21.10-1.21.7.txt"))
    mc12107.link(mc12105)
    mc12105.link(mc12104, file("versions/mapping-fabric-1.21.5-1.21.4.txt"))
    mc12104.link(mc12102)
    mc12102.link(mc12100)
}
