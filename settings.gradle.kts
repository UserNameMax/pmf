rootProject.name = "pmf"
include(":PlanMonitorFunction")
include(":calculator")
include(":Matrix")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
