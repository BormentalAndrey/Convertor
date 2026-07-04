// app/src/main/java/com/example/russianpath/data/exercise/TemplateEngineImpl.kt

package com.example.russianpath.data.exercise

import com.example.russianpath.core.exercise.ExerciseRequest
import com.example.russianpath.core.exercise.TemplateEngine
import com.example.russianpath.core.knowledge.SkillCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateEngineImpl @Inject constructor() : TemplateEngine {

    override fun buildPrompt(request: ExerciseRequest): String {
        val word = request.analysis.dictionaryWord.word

        return when (request.skillCode) {
            SkillCode.PHONETIC_ANALYSIS -> "Сколько слогов в слове «$word»?"
            SkillCode.PUNCTUATION_ANALYSIS -> "Раздели слово «$word» на слоги"
            SkillCode.ORTHOGRAPHY_NORMS -> "Какой слог ударный в слове «$word»?"
            SkillCode.MORPHOLOGICAL_ANALYSIS -> "Сколько гласных в слове «$word»?"
            SkillCode.SYNTACTIC_ANALYSIS -> "Сколько согласных в слове «$word»?"
            SkillCode.IDENTIFY_LANGUAGE_UNITS -> "Какая первая буква в слове «$word»?"
            SkillCode.MORPHEMIC_ANALYSIS -> "Какая последняя буква в слове «$word»?"
            SkillCode.ORTHOGRAPHIC_ANALYSIS -> "Сколько букв в слове «$word»?"
            SkillCode.LEXICAL_MEANS -> "Есть ли мягкий знак (ь) в слове «$word»?"
            SkillCode.SPEECH_STYLES -> "Есть ли твёрдый знак (ъ) в слове «$word»?"
            SkillCode.SPEECH_TYPES -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.GRAMMAR_NORMS -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.SPEECH_NORMS -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.TEXT_ANALYSIS -> "Найди корень в слове «$word»"
            SkillCode.TEXT_CREATION -> "Найди приставку в слове «$word»"
            SkillCode.TEXT_EDITING -> "Найди суффикс в слове «$word»"
            SkillCode.INFORMATION_PROCESSING -> "Найди окончание в слове «$word»"
            SkillCode.PUNCTUATION_NORMS -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.EXPRESSIVE_MEANS -> "Найди корень в слове «$word»"
            SkillCode.UNKNOWN -> "?"
        }
    }

    override fun buildHint(request: ExerciseRequest): String {
        return when (request.skillCode) {
            SkillCode.PHONETIC_ANALYSIS -> "Сколько гласных, столько и слогов"
            SkillCode.PUNCTUATION_ANALYSIS -> "Прохлопай слово по частям"
            SkillCode.ORTHOGRAPHY_NORMS -> "Позови слово: МА-а-а-МА"
            SkillCode.MORPHOLOGICAL_ANALYSIS -> "Гласные: А, О, У, Ы, Э, И, Е, Ё, Ю, Я"
            SkillCode.SYNTACTIC_ANALYSIS -> "Все буквы кроме гласных, Ь и Ъ"
            SkillCode.IDENTIFY_LANGUAGE_UNITS -> "Посмотри на первую букву слова"
            SkillCode.MORPHEMIC_ANALYSIS -> "Посмотри на последнюю букву слова"
            SkillCode.ORTHOGRAPHIC_ANALYSIS -> "Посчитай все буквы в слове"
            SkillCode.LEXICAL_MEANS -> "Мягкий знак выглядит так: Ь"
            SkillCode.SPEECH_STYLES -> "Твёрдый знак выглядит так: Ъ"
            SkillCode.SPEECH_TYPES -> "ЖИ-ШИ пиши с буквой И"
            SkillCode.GRAMMAR_NORMS -> "ЧА-ЩА пиши с буквой А"
            SkillCode.SPEECH_NORMS -> "ЧУ-ЩУ пиши с буквой У"
            SkillCode.TEXT_ANALYSIS -> "Корень — общая часть родственных слов"
            SkillCode.TEXT_CREATION -> "Приставка стоит перед корнем"
            SkillCode.TEXT_EDITING -> "Суффикс стоит после корня"
            SkillCode.INFORMATION_PROCESSING -> "Окончание изменяется: МАМА, МАМЫ, МАМЕ"
            SkillCode.PUNCTUATION_NORMS -> "ЧА-ЩА пиши с буквой А"
            SkillCode.EXPRESSIVE_MEANS -> "Корень — общая часть родственных слов"
            SkillCode.UNKNOWN -> "?"
        }
    }
}
