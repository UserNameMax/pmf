package ru.omgtu.matrix.matrix.store

import com.arkivanov.mvikotlin.core.store.Store

interface DangerStore : Store<DangerStore.Intent, DangerStore.State, Nothing> {
    enum class DangerProbability(val str: String, val value: Double) {
        LOW("Минимальная", 1.0 / 5),
        MODERATE("Умеренная", 2.0 / 5),
        ESSENTIAL("Существенная", 3.0 / 5),
        SIGNIFICANT("Значительная", 4.0 / 5),
        HIGH("Очень высокая", 5.0 / 5)
    }

    enum class HumanProbability(val str: String, val value: Double) {
        LOW("Низкий", 1.0 / 5),
        BELOW_AVERAGE("Ниже среднего", 2.0 / 5),
        AVERAGE("Средний", 3.0 / 5),
        HIGHER_AVERAGE("Выше среднего", 4.0 / 5),
        HIGH("Высокий", 5.0 / 5)
    }

    enum class ConsequenceProbability(val str: String, val value: Double) {
        MIN("Минимальная", 1.0 / 5),
        MINOR("Незначительная", 2.0 / 5),
        AVERAGE("Средняя", 3.0 / 5),
        SIGNIFICANT("Значительная", 4.0 / 5),
        CATASTROPHIC("Катастрофическая", 5.0 / 5)
    }

    sealed class DangerValue(val str: String, val value: Double) {
        class High(value: Double) : DangerValue("высокая", value)
        class Average(value: Double) : DangerValue("средняя", value)
        class Moderate(value: Double) : DangerValue("умеренная", value)
        class Low(value: Double) : DangerValue("низкая", value)
        class Insignificant(value: Double) : DangerValue("незначительная", value)
        object Empty : DangerValue("", Double.NaN)

        companion object {
            fun create(value: Double) = when (value) {
                in 0.8..1.0 -> High(value)
                in 0.6..0.8 -> Average(value)
                in 0.4..0.6 -> Moderate(value)
                in 0.2..0.4 -> Low(value)
                in 0.0..0.2 -> Insignificant(value)
                else -> throw IllegalArgumentException("value must in 0.0..1.0")
            }
        }
    }

    data class DangerParameters(
        val dangerProbability: DangerProbability? = null,
        val time: Int? = null,
        val humanProbability: HumanProbability? = null,
        val consequenceProbability: ConsequenceProbability? = null,
        val dangerValueWithHuman: DangerValue? = null,
        val dangerValue: DangerValue? = null
    )

    data class State(
        val parameters: List<Pair<String, DangerParameters>> = listOf(),
        val profession: String
    )

    sealed interface Intent {
        data class OnInputDangerProbability(val dangerProbability: DangerProbability, val dangerName: String) : Intent
        data class OnInputTime(val time: Int, val dangerName: String) : Intent
        data class OnInputHumanProbability(val humanProbability: HumanProbability, val dangerName: String) : Intent
        data class OnInputConsequenceProbability(
            val consequenceProbability: ConsequenceProbability,
            val dangerName: String
        ) : Intent
        object OnBackButtonPress: Intent
    }

}