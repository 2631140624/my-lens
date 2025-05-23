import java.net.URI

include(":myapplication")


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    // 从local.properties读取github用户名及personal access token (classic)
    val propsFile = File(rootProject.projectDir.path + "/local.properties")
    val properties = java.util.Properties()
    properties.load(propsFile.inputStream())
    // 设置到extra，会先于buildSrc执行，但是这里设置的extra没办法在project/module的gradle里面用。。。。
    extra.set("githubUser", properties.getProperty("gpr.user"))
    extra.set("githubPassword", properties.getProperty("gpr.key"))
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI("https://jitpack.io")
        }
        maven {
            url = URI("https://maven.pkg.github.com/2631140624/my-lens")
            credentials {
                username = extra["githubUser"].toString()
                password = extra["githubPassword"].toString()
            }
        }
    }
}

rootProject.name = "opencv"
include(":app")
include (":opencv_sdk")
include (":open_cv_java490")
include (":opencv410")
 