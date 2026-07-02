package com.example.russianpath.exercise

import com.example.russianpath.core.analysis.LetterAnalysis
import com.example.russianpath.core.analysis.SyllableAnalysis
import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.common.Difficulty
import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.dictionary.WordId
import com.example.russianpath.core.exercise.ExerciseBuilder
import com.example.russianpath.core.exercise.ExerciseRequest
import com.example.russianpath.core.exercise.ExerciseRequestFactory
import com.example.russianpath.core.exercise.ExerciseType
import com.example.russianpath.core.exercise.TextAnswer
import com.example.russianpath.core.exercise.TextOption
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.core.progress.AnswerEvaluator
import com.example.russianpath.core.progress.ChoiceUserAnswer
import com.example.russianpath.core.progress.TextUserAnswer
import com.example.russianpath.data.analyzer.LetterAnalyzer
import com.example.russianpath.data.analyzer.RussianAnalyzerImpl
import com.example.russianpath.data.analyzer.SyllableAnalyzer
import com.example.russianpath.data.analyzer.SyllableSplitter
import com.example.russianpath.data.analyzer.VowelDetector
import com.example.russianpath.data.exercise.AnswerEvaluatorImpl
import com.example.russianpath.data.exercise.AnswerProviderImpl
import com.example.russianpath.data.exercise.DistractorGeneratorImpl
import com.example.russianpath.data.exercise.ExerciseBuilderImpl
import com.example.russianpath.data.exercise.ExerciseRequestFactoryImpl
import com.example.russianpath.data.exercise.TemplateEngineImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExerciseEngineIntegrationTest {

    private lateinit var analyzer: RussianAnalyzerImpl
    private lateinit var builder: ExerciseBuilder
    private lateinit var factory: ExerciseRequestFactory
    private lateinit var evaluator: AnswerEvaluator

    @Before
    fun setup() {
        val vowelDetector = VowelDetector()
        val letterAnalyzer = LetterAnalyzer(vowelDetector)
        val syllableSplitter = SyllableSplitter(vowelDetector)
        val syllableAnalyzer = SyllableAnalyzer(syllableSplitter)
        analyzer = RussianAnalyzerImpl(letterAnalyzer, syllableAnalyzer)

        val templateEngine = TemplateEngineImpl()
        val answerProvider = AnswerProviderImpl()
        val distractorGenerator = DistractorGeneratorImpl()
        builder = ExerciseBuilderImpl(templateEngine, distractorGenerator, answerProvider)
        factory = ExerciseRequestFactoryImpl()
        evaluator = AnswerEvaluatorImpl()
    }

    private fun dictionaryWord(id: String, word: String, difficulty: Int = 1): DictionaryWord {
        return DictionaryWord(
            id = WordId(id),
            word = word,
            normalized = word.lowercase(),
            gradeLevel = 1,
            difficulty = Difficulty(difficulty),
            tags = emptySet(),
            schemaVersion = 1
        )
    }

    // ================================================================
    // Тест 1: COUNT_SYLLABLES
    // ================================================================
    @Test
    fun `COUNT_SYLLABLES should produce valid numeric exercise`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_SYLLABLES, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertTrue(exercise.prompt.contains("слогов"))
        assertNotNull(exercise.correctOptionId)
        assertTrue(exercise.options.isNotEmpty())

        val correct = exercise.correctAnswer as TextAnswer
        assertEquals("2", correct.value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("2"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 2: FIND_FIRST_LETTER
    // ================================================================
    @Test
    fun `FIND_FIRST_LETTER should detect first letter`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.FIND_FIRST_LETTER, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertEquals("К", (exercise.correctAnswer as TextAnswer).value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("К"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 3: RECOGNIZE_SOFT_SIGN
    // ================================================================
    @Test
    fun `RECOGNIZE_SOFT_SIGN should detect soft sign`() {
        val word = dictionaryWord("word_mol", "МОЛЬ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.RECOGNIZE_SOFT_SIGN, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertEquals("Да", (exercise.correctAnswer as TextAnswer).value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("Да"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 4: RECOGNIZE_HARD_SIGN — отсутствует
    // ================================================================
    @Test
    fun `RECOGNIZE_HARD_SIGN should return No for word without hard sign`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.RECOGNIZE_HARD_SIGN, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertEquals("Нет", (exercise.correctAnswer as TextAnswer).value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("Нет"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 5: COUNT_LETTERS
    // ================================================================
    @Test
    fun `COUNT_LETTERS should count correctly`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_LETTERS, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertEquals("3", (exercise.correctAnswer as TextAnswer).value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("3"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 6: COUNT_VOWELS
    // ================================================================
    @Test
    fun `COUNT_VOWELS should count vowels correctly`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_VOWELS, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertEquals("2", (exercise.correctAnswer as TextAnswer).value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("2"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 7: COUNT_CONSONANTS
    // ================================================================
    @Test
    fun `COUNT_CONSONANTS should count consonants correctly`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_CONSONANTS, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertEquals("2", (exercise.correctAnswer as TextAnswer).value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("2"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 8: distractors не содержат правильный ответ
    // ================================================================
    @Test
    fun `distractors must not contain correct answer`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_LETTERS, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        val correctValue = (exercise.correctAnswer as TextAnswer).value

        exercise.options.forEach { option ->
            val text = (option as TextOption).text
            if (option.id != exercise.correctOptionId) {
                assertTrue(
                    "Distractor '$text' must not equal correct answer '$correctValue'",
                    text != correctValue
                )
            }
        }
    }

    // ================================================================
    // Тест 9: детерминированный fingerprint
    // ================================================================
    @Test
    fun `same input should produce same exercise id`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_SYLLABLES, analysis, Difficulty.EASY)

        val ex1 = builder.build(request)
        val ex2 = builder.build(request)

        assertEquals(ex1.id, ex2.id)
    }

    // ================================================================
    // Тест 10: evaluator с ChoiceUserAnswer
    // ================================================================
    @Test
    fun `choice answer evaluation with correct option id should succeed`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.FIND_FIRST_LETTER, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        val correctOptionId = exercise.correctOptionId
        val result = evaluator.evaluate(exercise, ChoiceUserAnswer(correctOptionId))

        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 11: evaluator с неправильным ChoiceUserAnswer
    // ================================================================
    @Test
    fun `choice answer evaluation with wrong option id should fail`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.FIND_FIRST_LETTER, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        val wrongOption = exercise.options
            .filterIsInstance<TextOption>()
            .first { it.id != exercise.correctOptionId }

        val result = evaluator.evaluate(exercise, ChoiceUserAnswer(wrongOption.id))

        assertTrue(!result.isCorrect)
    }

    // ================================================================
    // Тест 12: FIND_LAST_LETTER
    // ================================================================
    @Test
    fun `FIND_LAST_LETTER should detect last letter`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.FIND_LAST_LETTER, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertEquals("М", (exercise.correctAnswer as TextAnswer).value)

        val result = evaluator.evaluate(exercise, TextUserAnswer("М"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 13: неверный ответ
    // ================================================================
    @Test
    fun `wrong answer should be marked incorrect`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_SYLLABLES, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        val result = evaluator.evaluate(exercise, TextUserAnswer("999"))

        assertTrue(!result.isCorrect)
    }

    // ================================================================
    // Тест 14: все опции уникальны
    // ================================================================
    @Test
    fun `all options must be unique`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_VOWELS, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        val texts = exercise.options.map { (it as TextOption).text }
        assertEquals(texts.size, texts.distinct().size)
    }

    // ================================================================
    // Тест 15: prompt не пустой и содержит слово
    // ================================================================
    @Test
    fun `prompt must contain the word`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)
        val request = factory.createRequest(SkillCode.COUNT_LETTERS, analysis, Difficulty.EASY)
        val exercise = builder.build(request)

        assertTrue(exercise.prompt.isNotBlank())
        assertTrue(exercise.prompt.contains("ДОМ"))
    }
}

// Импорт для filterIsInstance
import com.example.russianpath.core.exercise.ExerciseOption
