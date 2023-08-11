import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "com.happysnaker"
version = "3.4.1-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies{
    implementation("com.alibaba:fastjson:1.2.76")
    implementation(kotlin("stdlib-jdk8"))
    // https://mvnrepository.com/artifact/org.jfree/jfreechart
    implementation("org.jfree:jfreechart:1.0.19")
    // https://mvnrepository.com/artifact/org.reflections/reflections
    implementation("org.reflections:reflections:0.9.11")
    implementation("org.yaml:snakeyaml:1.25")
    implementation("org.jsoup:jsoup:1.11.2")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.quartz-scheduler:quartz:2.2.1")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    implementation("org.apache.httpcomponents:httpclient:4.5.12")
    implementation("org.apache.httpcomponents:httpmime:4.5.12")
    implementation("it.grabz.grabzit:grabzit:3.5.3")
    implementation("cn.hutool:hutool-all:5.8.21")
    implementation("org.htmlunit:htmlunit:3.3.0")
    implementation("org.jfree:jfreechart:1.5.3")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}
