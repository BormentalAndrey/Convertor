package com.example.russianpath.data.exercise

import com.example.russianpath.core.exercise.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ExerciseBuilderImpl @Inject constructor(
    private val templateEngine: TemplateEngine
) : ExerciseBuilder {

    override fun build(request: ExerciseRequest): Exercise {
        val fingerprint = ExerciseFingerprint(
            skillCode = request.skillCode,
            wordId = request.analysis.dictionaryWord.id,
            difficulty = request.difficulty,
            seed = Random.nextInt()
        )

        return Exercise(
            id = ExerciseIdFactory.create(fingerprint),
            fingerprint = fingerprint,
            exerciseType = request.exerciseType,
            presentationType = PresentationType.TEXT,
            prompt = templateEngine.buildPrompt(request),
            options = templateEngine.buildOptions(request),
            correctAnswer = templateEngine.buildCorrectAnswer(request),
            hint = templateEngine.buildHint(request)
        )
    }
}
