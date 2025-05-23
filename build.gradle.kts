// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.dagger.hilt.android") version "2.55" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("com.android.application") version "8.1.4" apply false
}

ext {
    // 从local.properties读取github用户名及personal access token (classic)
    val properties = java.util.Properties()
    val inputStream = project.rootProject.file("local.properties").inputStream()
    properties.load(inputStream)

    set("githubUser", properties.getProperty("gpr.user"))
    set("githubPassword", properties.getProperty("gpr.key"))
}
