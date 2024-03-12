package ru.omgtu.matrix.repository

class ResourceDangersNameRepository: DangersNameRepository {
    override fun getDangers(): List<String> {
        return this.javaClass.classLoader.getResource("dangers.txt").readText().split("\n")
    }
}