package ru.omgtu.matrix.repository

class ResourceProfessionRepository:ProfessionRepository {
    override fun getProfessions(): List<String> {
        return this.javaClass.classLoader.getResource("professions.txt").readText().split("\n")
    }

}