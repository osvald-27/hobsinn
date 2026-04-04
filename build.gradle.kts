import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")                         version "1.9.22" apply false
    kotlin("plugin.spring")               version "1.9.22" apply false
    kotlin("plugin.jpa")                  version "1.9.22" apply false
    id("org.springframework.boot")        version "3.2.3"  apply false
    id("io.spring.dependency-management") version "1.1.4"  apply false
}

val javaVersion = JavaVersion.VERSION_21

allprojects {
    group   = "com.hobsinnovations.hobsinn"
    version = "1.0.0-SNAPSHOT"
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    configure<JavaPluginExtension> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget        = "21"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading")
    }
}
