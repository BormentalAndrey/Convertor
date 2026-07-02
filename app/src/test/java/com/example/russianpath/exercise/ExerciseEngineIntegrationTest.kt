package com.example.russianpath.exercise

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
import com.example.russianpath.data.exercise.TemplateEngineImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExerciseEngineIntegrationTest {

    private lateinit var analyzer: RussianAnalyzerImpl
    private lateinit var builder: ExerciseBuilder

    // Factory не используется в тестах — запросы создаются явно для детерминизма
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

    private fun createRequest(
        skillCode: SkillCode,
        analysis: WordAnalysis,
        type: ExerciseType = ExerciseType.CHOICE
    ): ExerciseRequest {
        return ExerciseRequest(
            skillCode = skillCode,
            exerciseType = type,
            difficulty = Difficulty.EASY,
            analysis = analysis
        )
    }

    // ================================================================
    // Тест 1: COUNT_SYLLABLES — анализ + упражнение + правильный ответ
    // ================================================================
    @Test
    fun `COUNT_SYLLABLES — analysis, exercise, and correct answer`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)

        // Проверка анализатора
        assertEquals(2, analysis.syllableAnalysis?.count)

        val request = createRequest(SkillCode.COUNT_SYLLABLES, analysis)
        val exercise = builder.build(request)

        // Проверка fingerprint
        assertEquals(SkillCode.COUNT_SYLLABLES, exercise.fingerprint.skillCode)
        assertEquals(word.id, exercise.fingerprint.wordId)
        assertEquals(Difficulty.EASY, exercise.fingerprint.difficulty)

        // Проверка правильного ответа
        assertEquals("2", (exercise.correctAnswer as TextAnswer).value)

        // Проверка correctOptionId существует
        assertTrue(exercise.options.any { it.id == exercise.correctOptionId })

        // Проверка prompt и hint
        assertTrue(exercise.prompt.contains("слогов"))
        assertTrue(exercise.prompt.contains("МАМА"))
        assertNotNull(exercise.hint)
        assertTrue(exercise.hint!!.isNotBlank())

        // Проверка числа вариантов
        assertEquals(4, exercise.options.size)

        // Проверка уникальности id
        val ids = exercise.options.map { it.id }
        assertEquals(ids.size, ids.distinct().size)

        // Правильный ответ
        val result = evaluator.evaluate(exercise, TextUserAnswer("2"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 2: FIND_FIRST_LETTER
    // ================================================================
    @Test
    fun `FIND_FIRST_LETTER — detects first letter`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)

        assertEquals('К', analysis.letterAnalysis.first)

        val request = createRequest(SkillCode.FIND_FIRST_LETTER, analysis)
        val exercise = builder.build(request)

        assertEquals("К", (exercise.correctAnswer as TextAnswer).value)

        // Проверка регистра
        val resultLower = evaluator.evaluate(exercise, TextUserAnswer("к"))
        assertTrue(resultLower.isCorrect)
    }

    // ================================================================
    // Тест 3: RECOGNIZE_SOFT_SIGN — есть
    // ================================================================
    @Test
    fun `RECOGNIZE_SOFT_SIGN — detects soft sign`() {
        val word = dictionaryWord("word_mol", "МОЛЬ")
        val analysis = analyzer.analyze(word)

        assertTrue(analysis.letterAnalysis.hasSoftSign)

        val request = createRequest(SkillCode.RECOGNIZE_SOFT_SIGN, analysis)
        val exercise = builder.build(request)

        assertEquals("Да", (exercise.correctAnswer as TextAnswer).value)

        // Регистр
        val result = evaluator.evaluate(exercise, TextUserAnswer("да"))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 4: RECOGNIZE_HARD_SIGN — нет
    // ================================================================
    @Test
    fun `RECOGNIZE_HARD_SIGN — returns No when absent`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)

        assertTrue(!analysis.letterAnalysis.hasHardSign)

        val request = createRequest(SkillCode.RECOGNIZE_HARD_SIGN, analysis)
        val exercise = builder.build(request)

        assertEquals("Нет", (exercise.correctAnswer as TextAnswer).value)
    }

    // ================================================================
    // Тест 5: COUNT_LETTERS
    // ================================================================
    @Test
    fun `COUNT_LETTERS — counts correctly`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)

        assertEquals(3, analysis.letterAnalysis.count)

        val request = createRequest(SkillCode.COUNT_LETTERS, analysis)
        val exercise = builder.build(request)

        assertEquals("3", (exercise.correctAnswer as TextAnswer).value)
    }

    // ================================================================
    // Тест 6: COUNT_VOWELS
    // ================================================================
    @Test
    fun `COUNT_VOWELS — counts vowels`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)

        val vowelCount = analysis.letterAnalysis.letters.count { it.isVowel }
        assertEquals(2, vowelCount)

        val request = createRequest(SkillCode.COUNT_VOWELS, analysis)
        val exercise = builder.build(request)

        assertEquals("2", (exercise.correctAnswer as TextAnswer).value)
    }

    // ================================================================
    // Тест 7: COUNT_CONSONANTS
    // ================================================================
    @Test
    fun `COUNT_CONSONANTS — counts consonants`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)

        val consonantCount = analysis.letterAnalysis.letters.count { it.isConsonant }
        assertEquals(2, consonantCount)

        val request = createRequest(SkillCode.COUNT_CONSONANTS, analysis)
        val exercise = builder.build(request)

        assertEquals("2", (exercise.correctAnswer as TextAnswer).value)
    }

    // ================================================================
    // Тест 8: Дистракторы не содержат правильный ответ
    // ================================================================
    @Test
    fun `distractors must not contain correct answer`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.COUNT_LETTERS, analysis)
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
    // Тест 9: Детерминированный fingerprint
    // ================================================================
    @Test
    fun `same input produces same exercise id`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.COUNT_SYLLABLES, analysis)

        val ex1 = builder.build(request)
        val ex2 = builder.build(request)

        assertEquals(ex1.id, ex2.id)
    }

    // ================================================================
    // Тест 10: Choice — правильный OptionId
    // ================================================================
    @Test
    fun `choice answer with correct option id succeeds`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.FIND_FIRST_LETTER, analysis)
        val exercise = builder.build(request)

        val result = evaluator.evaluate(exercise, ChoiceUserAnswer(exercise.correctOptionId))
        assertTrue(result.isCorrect)
    }

    // ================================================================
    // Тест 11: Choice — неправильный OptionId
    // ================================================================
    @Test
    fun `choice answer with wrong option id fails`() {
        val word = dictionaryWord("word_kot", "КОТ")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.FIND_FIRST_LETTER, analysis)
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
    fun `FIND_LAST_LETTER — detects last letter`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)

        assertEquals('М', analysis.letterAnalysis.last)

        val request = createRequest(SkillCode.FIND_LAST_LETTER, analysis)
        val exercise = builder.build(request)

        assertEquals("М", (exercise.correctAnswer as TextAnswer).value)
    }

    // ================================================================
    // Тест 13: Неправильный ответ
    // ================================================================
    @Test
    fun `wrong answer is marked incorrect`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.COUNT_SYLLABLES, analysis)
        val exercise = builder.build(request)

        val result = evaluator.evaluate(exercise, TextUserAnswer("999"))
        assertTrue(!result.isCorrect)
    }

    // ================================================================
    // Тест 14: Все тексты вариантов уникальны
    // ================================================================
    @Test
    fun `all option texts are unique`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.COUNT_VOWELS, analysis)
        val exercise = builder.build(request)

        val texts = exercise.options.map { (it as TextOption).text }
        assertEquals(texts.size, texts.distinct().size)
    }

    // ================================================================
    // Тест 15: Prompt содержит слово
    // ================================================================
    @Test
    fun `prompt contains the word`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.COUNT_LETTERS, analysis)
        val exercise = builder.build(request)

        assertTrue(exercise.prompt.isNotBlank())
        assertTrue(exercise.prompt.contains("ДОМ"))
    }

    // ================================================================
    // Тест 16: Пустой ответ
    // ================================================================
    @Test
    fun `empty answer is marked incorrect`() {
        val word = dictionaryWord("word_mama", "МАМА")
        val analysis = analyzer.analyze(word)
        val request = createRequest(SkillCode.COUNT_SYLLABLES, analysis)
        val exercise = builder.build(request)

        val result = evaluator.evaluate(exercise, TextUserAnswer(""))
        assertTrue(!result.isCorrect)
    }

    // ================================================================
    // Тест 17: Слово с Ё
    // ================================================================
    @Test
    fun `word with Yo letter is analyzed correctly`() {
        val word = dictionaryWord("word_yolka", "ЁЛКА")
        val analysis = analyzer.analyze(word)

        assertEquals(4, analysis.letterAnalysis.count)
        assertEquals('Ё', analysis.letterAnalysis.first)
        assertEquals(2, analysis.letterAnalysis.letters.count { it.isVowel })

        val request = createRequest(SkillCode.FIND_FIRST_LETTER, analysis)
        val exercise = builder.build(request)

        assertEquals("Ё", (exercise.correctAnswer as TextAnswer).value)
    }

    // ================================================================
    // Тест 18: Односложное слово
    // ================================================================
    @Test
    fun `single syllable word`() {
        val word = dictionaryWord("word_dom", "ДОМ")
        val analysis = analyzer.analyze(word)

        assertEquals(1, analysis.syllableAnalysis?.count)

        val request = createRequest(SkillCode.COUNT_SYLLABLES, analysis)
        val exercise = builder.build(request)

        assertEquals("1", (exercise.correctAnswer as TextAnswer).value)
    }
}
