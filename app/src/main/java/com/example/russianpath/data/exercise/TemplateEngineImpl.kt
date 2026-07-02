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
            SkillCode.COUNT_SYLLABLES -> "Сколько слогов в слове «$word»?"
            SkillCode.DIVIDE_TO_SYLLABLES -> "Раздели слово «$word» на слоги"
            SkillCode.FIND_STRESSED_SYLLABLE -> "Какой слог ударный в слове «$word»?"
            SkillCode.COUNT_VOWELS -> "Сколько гласных в слове «$word»?"
            SkillCode.COUNT_CONSONANTS -> "Сколько согласных в слове «$word»?"
            SkillCode.FIND_FIRST_LETTER -> "Какая первая буква в слове «$word»?"
            SkillCode.FIND_LAST_LETTER -> "Какая последняя буква в слове «$word»?"
            SkillCode.COUNT_LETTERS -> "Сколько букв в слове «$word»?"
            SkillCode.RECOGNIZE_SOFT_SIGN -> "Есть ли мягкий знак (ь) в слове «$word»?"
            SkillCode.RECOGNIZE_HARD_SIGN -> "Есть ли твёрдый знак (ъ) в слове «$word»?"
            SkillCode.SPELLING_ZHI_SHI -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.SPELLING_CHA_SCHA -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.SPELLING_CHU_SCHU -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.FIND_ROOT -> "Найди корень в слове «$word»"
            SkillCode.FIND_PREFIX -> "Найди приставку в слове «$word»"
            SkillCode.FIND_SUFFIX -> "Найди суффикс в слове «$word»"
            SkillCode.FIND_ENDING -> "Найди окончание в слове «$word»"
        }
    }

    override fun buildHint(request: ExerciseRequest): String {
        return when (request.skillCode) {
            SkillCode.COUNT_SYLLABLES -> "Сколько гласных, столько и слогов"
            SkillCode.DIVIDE_TO_SYLLABLES -> "Прохлопай слово по частям"
            SkillCode.FIND_STRESSED_SYLLABLE -> "Позови слово: МА-а-а-МА"
            SkillCode.COUNT_VOWELS -> "Гласные: А, О, У, Ы, Э, И, Е, Ё, Ю, Я"
            SkillCode.COUNT_CONSONANTS -> "Все буквы кроме гласных, Ь и Ъ"
            SkillCode.FIND_FIRST_LETTER -> "Посмотри на первую букву слова"
            SkillCode.FIND_LAST_LETTER -> "Посмотри на последнюю букву слова"
            SkillCode.COUNT_LETTERS -> "Посчитай все буквы в слове"
            SkillCode.RECOGNIZE_SOFT_SIGN -> "Мягкий знак выглядит так: Ь"
            SkillCode.RECOGNIZE_HARD_SIGN -> "Твёрдый знак выглядит так: Ъ"
            SkillCode.SPELLING_ZHI_SHI -> "ЖИ-ШИ пиши с буквой И"
            SkillCode.SPELLING_CHA_SCHA -> "ЧА-ЩА пиши с буквой А"
            SkillCode.SPELLING_CHU_SCHU -> "ЧУ-ЩУ пиши с буквой У"
            SkillCode.FIND_ROOT -> "Корень — общая часть родственных слов"
            SkillCode.FIND_PREFIX -> "Приставка стоит перед корнем"
            SkillCode.FIND_SUFFIX -> "Суффикс стоит после корня"
            SkillCode.FIND_ENDING -> "Окончание изменяется: МАМА, МАМЫ, МАМЕ"
        }
    }
}
