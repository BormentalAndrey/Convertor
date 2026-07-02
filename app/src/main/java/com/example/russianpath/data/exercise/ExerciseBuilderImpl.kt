package com.example.russianpath.data.exercise

import com.example.russianpath.core.exercise.AnswerProvider
import com.example.russianpath.core.exercise.ChoiceAnswer
import com.example.russianpath.core.exercise.CorrectAnswer
import com.example.russianpath.core.exercise.Exercise
import com.example.russianpath.core.exercise.ExerciseBuilder
import com.example.russianpath.core.exercise.ExerciseFingerprint
import com.example.russianpath.core.exercise.ExerciseIdFactory
import com.example.russianpath.core.exercise.ExerciseOption
import com.example.russianpath.core.exercise.ExerciseRequest
import com.example.russianpath.core.exercise.OptionId
import com.example.russianpath.core.exercise.PresentationType
import com.example.russianpath.core.exercise.TextAnswer
import com.example.russianpath.core.exercise.TextOption
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseBuilderImpl @Inject constructor(
    private val templateEngine: TemplateEngine,
    private val distractorGenerator: DistractorGenerator,
    private val answerProvider: AnswerProvider
) : ExerciseBuilder {

    override fun build(request: ExerciseRequest): Exercise {
        val fingerprint = ExerciseFingerprint(
            skillCode = request.skillCode,
            wordId = request.analysis.dictionaryWord.id,
            difficulty = request.difficulty,
            seed = request.analysis.dictionaryWord.id.hashCode() xor request.skillCode.code
        )

        val correctAnswer = answerProvider.getCorrectAnswer(request.skillCode, request.analysis)
        val correctOptionId = OptionId(UUID.randomUUID().toString())
        val options = buildOptions(correctAnswer, correctOptionId, request)

        return Exercise(
            id = ExerciseIdFactory.create(fingerprint),
            fingerprint = fingerprint,
            exerciseType = request.exerciseType,
            presentationType = PresentationType.TEXT,
            prompt = templateEngine.buildPrompt(request),
            options = options,
            correctOptionId = correctOptionId,
            correctAnswer = correctAnswer,
            hint = templateEngine.buildHint(request)
        )
    }

    private fun buildOptions(
        correctAnswer: CorrectAnswer,
        correctOptionId: OptionId,
        request: ExerciseRequest
    ): List<ExerciseOption> {
        val correctValue = when (correctAnswer) {
            is TextAnswer -> correctAnswer.value
            is ChoiceAnswer -> correctAnswer.value
        }

        val correctOption = TextOption(id = correctOptionId, text = correctValue)

        val distractors = distractorGenerator
            .generate(correct = correctAnswer, count = 3, skillCode = request.skillCode)
            .filter { (it as? TextOption)?.text != correctValue }
            .take(3)

        return (listOf(correctOption) + distractors).shuffled()
    }
}
