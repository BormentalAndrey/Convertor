package com.example.russianpath.data.exercise

import com.example.russianpath.core.exercise.*
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

@Singleton
class ExerciseBuilderImpl @Inject constructor(
    private val templateEngine: TemplateEngine,
    private val distractorGenerator: DistractorGenerator
) : ExerciseBuilder {

    override fun build(request: ExerciseRequest): Exercise {
        val prompt = templateEngine.buildPrompt(request)
        val options = templateEngine.buildOptions(request)
        val correctAnswer = templateEngine.buildCorrectAnswer(request)
        val hint = templateEngine.buildHint(request)

        return Exercise(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            options = options,
            correctAnswer = correctAnswer,
            hint = hint,
            metadata = ExerciseMetadata(
                skillCode = request.skillCode,
                exerciseType = request.exerciseType,
                presentationType = PresentationType.TEXT,
                difficulty = request.difficulty,
                dictionaryWordId = request.analysis.dictionaryWord.id
            )
        )
    }
}
