package ru.omgtu.matrix.repository

interface ProfessionRepository {
    fun getProfessions(): List<String>
}