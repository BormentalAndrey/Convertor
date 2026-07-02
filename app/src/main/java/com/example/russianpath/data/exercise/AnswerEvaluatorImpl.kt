package com.example.russianpath.data.exercise

import com.example.russianpath.core.exercise.*
import com.example.russianpath.core.progress.*
import javax.inject.Inject
import javax.inject.Singleton
import java.time.Instant

@Singleton
class AnswerEvaluatorImpl @Inject constructor() : AnswerEvaluator {

    override fun evaluate(exercise: Exercise, answer: UserAnswer): AnswerResult {
        val isCorrect = when (exercise.correctAnswer) {
            is TextAnswer -> {
                val userText = (answer as? TextUserAnswer)?.text ?: return wrongResult(exercise, answer)
                userText.equals(exercise.correctAnswer.value, ignoreCase = true)
            }
            is ChoiceAnswer -> {
                val userIndex = (answer as? ChoiceUserAnswer)?.index ?: return wrongResult(exercise, answer)
                userIndex == exercise.correctAnswer.index
            }
            else -> false
        }

        return AnswerResult(
            exerciseId = exercise.id,
            userAnswer = answer,
            isCorrect = isCorrect,
            timeSpentMs = 0, // Будет заполнено ViewModel
            timestamp = Instant.now()
        )
    }

    private fun wrongResult(exercise: Exercise, answer: UserAnswer): AnswerResult {
        return AnswerResult(
            exerciseId = exercise.id,
            userAnswer = answer,
            isCorrect = false,
            timeSpentMs = 0,
            timestamp = Instant.now()
        )
    }
}
