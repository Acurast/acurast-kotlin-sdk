import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Build.Kotlin.version
    `maven-publish`
    jacoco
    id("org.barfuin.gradle.jacocolog") version "2.0.0"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")

    if (isBuildable) {
        apply(plugin = "maven-publish")
        apply(plugin = "jacoco")

        group = Project.group
        version = Project.version

        java {
            sourceCompatibility = Build.Java.compatibility
            targetCompatibility = Build.Java.compatibility
        }

        kotlin {
            explicitApiWarning()
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = Build.Kotlin.jvmTarget
                freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
            }
        }

        tasks.jacocoTestReport {
            dependsOn(tasks.test)

            reports {
                xml.required.set(true)
                csv.required.set(true)
                html.required.set(true)
            }
        }

        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components.getByName("java"))

                    artifactId = fullName
                }
            }
        }
    }
}

// -- jars --

tasks.register<Copy>("copyJars") {
    dependsOn(tasks.assemble)

    delete("$buildDir/libs/subprojects")

    subprojects.filterBuildable().forEach {
        copy {
            from("${it.buildDir}/libs/${it.name}-${it.version}.jar")
            rename("^.+-(.+)$", "${it.fullName}-$1")
            into("$buildDir/libs/subprojects")
        }
    }
}

tasks.register<Zip>("zipJars") {
    dependsOn("copyJars")

    archiveFileName.set("jars.zip")
    destinationDirectory.set(layout.projectDirectory)

    from("$buildDir/libs/subprojects")
}

// -- tests --

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, subprojects.filterBuildable().map { it.tasks.jacocoTestReport })

    additionalSourceDirs.setFrom(subprojects.filterBuildable().map { it.sourceSets.main.get().allSource.srcDirs })
    sourceDirectories.setFrom(files(subprojects.filterBuildable().map { it.sourceSets.main.get().allSource.srcDirs }))
    classDirectories.setFrom(files(subprojects.filterBuildable().map { it.sourceSets.main.get().output }))
    executionData.setFrom(project.fileTree(".") { include("**/build/jacoco/test.exec") } )

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}

tasks.register<Copy>("copyTestReports") {
    dependsOn(tasks.test)

    subprojects.filterBuildable().forEach {
        sync {
            from("${it.buildDir}/reports/tests")
            into("$buildDir/reports/tests/subprojects/${it.fullName}")
        }
        sync {
            from("${it.buildDir}/reports/jacoco/test")
            into("$buildDir/reports/tests/subprojects/${it.fullName}/coverage")
        }
    }
}

tasks.register<Zip>("zipTestReports") {
    dependsOn("copyTestReports")

    archiveFileName.set("test-reports.zip")
    destinationDirectory.set(layout.projectDirectory)

    from("$buildDir/reports/tests/subprojects")
    from("$buildDir/reports/jacoco/test") {
        into("coverage")
    }
}

fun MutableSet<org.gradle.api.Project>.filterBuildable(): List<org.gradle.api.Project> = filter { it.isBuildable }

val org.gradle.api.Project.isBuildable: Boolean
    get() = childProjects.isEmpty() && !name.contains("samples")

val org.gradle.api.Project.fullName: String
    get() = path.removePrefix(":").replace(":", "-")
