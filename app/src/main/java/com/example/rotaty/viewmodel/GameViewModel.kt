package com.example.rotaty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import com.example.rotaty.model.GameState
import com.example.rotaty.model.CharacterState
import com.example.rotaty.model.TypingStats
import kotlin.math.round

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _characterStates = MutableStateFlow<List<CharacterState>>(listOf())

    private var startTime: Long = 0
    private var totalKeystrokes: Int = 0
    private var timerJob: Job? = null

    private val sampleTexts = listOf(
        """Почему Dota 2 лучше Лиги-легенд. Она предлагает более глубокий и разнообразный геймплей. Глубокая система предметов и героев позволяет создавать уникальные стратегии. Высокая степень свободы действий и командной игры делают каждую игру неповторимой. Dota 2 постоянно развивается, предлагая новые обновления и контент. Более того, у Dota 2 более активное и профессиональное сообщество. Все это делает Dota 2 более привлекательной для игроков, жаждущих сложных и захватывающих испытаний.""",

        """Мета в Лига Легенд меняется очень быстро. Чтобы быть успешным, игроки вынуждены постоянно следить за последними обновлениями и адаптировать свой стиль игры под текущую мету. Это создает ощущение, что игроки играют не за любимых чемпионов, а за тех, кто в данный момент является самым сильным. Такая зависимость от меты убивает индивидуальность и разнообразие в игре.""",

        """Лига Легенд позиционирует себя как игра для всех, однако на деле это далеко не так. Низкий порог вхождения обманчив: чтобы достичь высоких рангов и конкурировать с опытными игроками, требуется огромное количество времени и усилий. Система подбора игроков часто подводит, собирая команды с разным уровнем мастерства, что делает игру несправедливой и демотивирующей. Кроме того, постоянные изменения баланса и появление новых чемпионов заставляют игроков постоянно адаптироваться и изучать новые механики, что отпугивает многих.""",

        """Лига Легенд давно перешла из разряда бесплатных игр в категорию pay-to-win. Огромное количество косметических предметов, которые можно приобрести за реальные деньги, создает серьезное неравенство между игроками. Кроме того, разработчики постоянно выпускают новые скины и события, заставляя игроков тратить деньги, чтобы не отставать от моды. Такая модель монетизации негативно сказывается на игровом процессе и превращает Лигу Легенд в магазин косметики.""",

        """Токсичность – одна из самых больших проблем Лига Легенд. Чат в игре часто превращается в помойку, где игроки оскорбляют друг друга и угрожают. Такая атмосфера отпугивает новых игроков и создает негативный имидж игры. Разработчики предпринимают попытки бороться с токсичностью, но пока эти меры не дают ощутимых результатов.""",

        """Лор Лига Легенд – это красивая картинка, которая не имеет глубокого смысла. Истории чемпионов часто противоречат друг другу, а мир Runeterra кажется плоским и однообразным. Разработчики уделяют недостаточно внимания развитию лора, что делает игру менее интересной для тех, кто ценит глубокие сюжеты.""",

        """Баланс чемпионов – это вечная проблема Лига Легенд. Разработчики постоянно пытаются сбалансировать игру, но у них это получается далеко не всегда. Некоторые чемпионы оказываются слишком сильными и доминируют на протяжении нескольких сезонов, в то время как другие остаются бесполезными. Это приводит к тому, что игроки вынуждены выбирать только определенных героев, чтобы быть конкурентоспособными.""",

        """Minecraft - это не просто игра, а целая вселенная, ограниченная лишь вашей фантазией. Ее уникальность заключается в абсолютной свободе действий: строить замки, исследовать пещеры, создавать целые миры. Игра развивает креативность и воображение, позволяя воплощать в жизнь любые идеи. Благодаря постоянным обновлениям и огромному сообществу мододелов, Minecraft никогда не надоедает, предлагая бесконечные возможности для развлечений и самовыражения. В отличие от других игр, где игрока ведут по строго определенному пути, Minecraft дает полную свободу выбора, что делает ее самой лучшей игрой для всех возрастов."""
    )

    init {
        startNewGame()
    }

    private fun calculateTypingStats(input: String, targetText: String, elapsedTimeSeconds: Double): TypingStats {
        val correctChars = input.filterIndexed { index, c -> c == targetText[index] }.length
        val totalChars = input.length
        val accuracy = if (totalChars > 0) (correctChars.toDouble() / totalChars) * 100 else 0.0
        val timeInMinutes = elapsedTimeSeconds / 60
        val charsPerMinute = if (timeInMinutes > 0) (correctChars / timeInMinutes) else 0.0

        return TypingStats(
            charsPerMinute = round(charsPerMinute * 10) / 10,
            accuracy = round(accuracy * 10) / 10,
            correctChars = correctChars,
            totalChars = totalChars
        )
    }

    fun startNewGame() {
        timerJob?.cancel()
        viewModelScope.launch {
            val newText = sampleTexts.random()
            startTime = System.currentTimeMillis()
            totalKeystrokes = 0

            _gameState.value = GameState(
                targetText = newText,
                timeLeft = 60,
                isGameOver = false,
                isSuccess = false,
                currentInput = "",
                correctChars = 0,
                lastCharCorrect = true,
                typingSpeed = 0.0,
                accuracy = 0.0,
                totalKeystrokes = 0
            )
            _characterStates.value = newText.map { CharacterState(it) }
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.timeLeft > 0 && !_gameState.value.isSuccess) {
                delay(1000)
                val currentState = _gameState.value
                val elapsedTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0

                val stats = calculateTypingStats(
                    currentState.currentInput,
                    currentState.targetText,
                    elapsedTimeSeconds
                )

                _gameState.value = currentState.copy(
                    timeLeft = currentState.timeLeft - 1,
                    isGameOver = currentState.timeLeft <= 1,
                    typingSpeed = stats.charsPerMinute,
                    accuracy = stats.accuracy,
                    totalKeystrokes = totalKeystrokes
                )
            }
        }
    }

    fun onTextInput(input: String) {
        val currentState = _gameState.value
        val targetText = currentState.targetText

        if (input.length <= targetText.length) {
            totalKeystrokes++

            val newCharStates = _characterStates.value.toMutableList()

            _characterStates.value = newCharStates

            val elapsedTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0
            val stats = calculateTypingStats(input, targetText, elapsedTimeSeconds)

            _gameState.value = currentState.copy(
                currentInput = input,
                correctChars = stats.correctChars,
                lastCharCorrect = input.lastOrNull()?.let { it == targetText[input.length - 1] } ?: true,
                isSuccess = input == targetText,
                typingSpeed = stats.charsPerMinute,
                accuracy = stats.accuracy,
                totalKeystrokes = totalKeystrokes
            )

            if (_gameState.value.isSuccess) {
                viewModelScope.launch {
                    delay(3000)
                    startNewGame()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}