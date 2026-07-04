// app/src/main/java/com/example/russianpath/presentation/screens/result/ResultViewModel.kt

package com.example.russianpath.presentation.screens.result

import androidx.lifecycle.ViewModel
import com.example.russianpath.presentation.screens.lesson.LessonResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel для экрана результатов после завершения урока.
 *
 * Управляет:
 * - Хранением результата урока (LessonResult)
 * - Переживанием пересоздания конфигурации (поворот экрана)
 *
 * Результат устанавливается из ResultScreen через setResult()
 * при навигации с LessonScreen.
 */
@HiltViewModel
class ResultViewModel @Inject constructor() : ViewModel() {

    private val _result = MutableStateFlow<LessonResult?>(null)
    val result: StateFlow<LessonResult?> = _result.asStateFlow()

    /**
     * Устанавливает результат урока.
     * Вызывается при навигации на ResultScreen.
     *
     * @param lessonResult Результат прохождения урока.
     */
    fun setResult(lessonResult: LessonResult) {
        _result.value = lessonResult
    }

    /**
     * Сбрасывает результат (при уходе с экрана).
     */
    fun clearResult() {
        _result.value = null
    }
}
