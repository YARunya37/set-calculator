package org.example

/**
 * Разбирает выражения над множествами и вычисляет результат
 * с учётом приоритета операций и круглых скобок.
 *
 * @param setManager менеджер для получения множеств.
 */
class SetParser(
    private val setManager: SetManager
) {
    // Выражение, которое обрабатывается парсером.
    private var expression: String = ""

    // Позиция текущего символа в выражении.
    private var position: Int = 0

    /**
     * Разбирает выражение и вычисляет результирующее множество.
     *
     * @param input строковое выражение над множествами.
     * @return результат вычисления выражения.
     * @throws IllegalArgumentException при пустом выражении,
     * синтаксической ошибке или неизвестном множестве.
     */
    fun parse(input: String): Set {
        require(input.isNotBlank()) {
            "Выражение не должно быть пустым."
        }

        expression = input
        position = 0

        val result = parseUnion()

        skipSpaces()

        if (position < expression.length) {
            throw IllegalArgumentException(
                "Неожиданный символ '${expression[position]}' " +
                        "в позиции $position."
            )
        }

        return result
    }

    /**
     * Разбирает объединение множеств.
     *
     * Обрабатывает операторы | слева направо.
     * Разбор правого операнда выполняется через parseIntersection(),
     * чтобы сохранить приоритет операций.
     */
    private fun parseUnion(): Set {
        var result = parseIntersection()

        while (position < expression.length) {
            skipSpaces()

            if (!match('|')) {
                break
            }

            val right = parseIntersection()
            result = result union right
        }

        return result
    }

    /**
     * Разбирает пересечение множеств.
     *
     * Обрабатывает операторы & слева направо.
     * Операнды разбираются через parseDifference().
     */
    private fun parseIntersection(): Set {
        var result = parseDifference()

        while (position < expression.length) {
            skipSpaces()

            if (!match('&')) {
                break
            }

            val right = parseDifference()
            result = result intersection right
        }

        return result
    }

    /**
     * Разбирает разность множеств.
     *
     * Обрабатывает операторы - слева направо.
     * Операнды разбираются через parseXor().
     */
    private fun parseDifference(): Set {
        var result = parseXor()

        while (position < expression.length) {
            skipSpaces()

            if (!match('-')) {
                break
            }

            val right = parseXor()
            result = result difference right
        }

        return result
    }

    /**
     * Разбирает симметрическую разность множеств.
     *
     * Обрабатывает операторы ^ слева направо.
     * Операнды разбираются через parseUnary().
     */
    private fun parseXor(): Set {
        var result = parseUnary()

        while (position < expression.length) {
            skipSpaces()

            if (!match('^')) {
                break
            }

            val right = parseUnary()
            result = result symmetricDifference right
        }

        return result
    }

    /**
     * Разбирает унарное дополнение множества.
     *
     * При обнаружении ~ рекурсивно разбирает операнд,
     * получает универсальное множество U из SetManager
     * и вычисляет дополнение относительно U.
     *
     * @return результат дополнения или первичное выражение.
     * @throws IllegalArgumentException если U не подготовлено.
     */
    private fun parseUnary(): Set {
        skipSpaces()

        if (match('~')) {
            val operand = parseUnary()

            val universal = setManager.getUniversalSet()
                ?: throw IllegalArgumentException(
                    "Универсальное множество U не подготовлено."
                )

            return operand.complement(universal)
        }

        return parsePrimary()
    }

    /**
     * Разбирает первичное выражение.
     *
     * Распознаёт обозначения множеств A–E и выражения
     * в круглых скобках.
     *
     * @return множество, соответствующее выражению.
     * @throws IllegalArgumentException при неизвестном обозначении,
     * незаданном множестве или ошибке в скобках.
     */
    private fun parsePrimary(): Set {
        skipSpaces()

        if (position >= expression.length) {
            throw IllegalArgumentException(
                "Ожидалось обозначение множества или '('."
            )
        }

        val current = expression[position]

        if (current == '(') {
            position++

            val result = parseUnion()

            skipSpaces()

            if (!match(')')) {
                throw IllegalArgumentException(
                    "Отсутствует закрывающая скобка ')'."
                )
            }

            return result
        }

        if (current in 'A'..'E') {
            position++

            return setManager.getSet(current)
                ?: throw IllegalArgumentException(
                    "Множество $current не задано."
                )
        }

        throw IllegalArgumentException(
            "Недопустимый символ '$current' в позиции $position."
        )
    }

    /**
     * Пропускает пробельные символы.
     *
     * Цикл завершается при достижении конца строки
     * или первого непробельного символа.
     */
    private fun skipSpaces() {
        while (
            position < expression.length &&
            expression[position].isWhitespace()
        ) {
            position++
        }
    }

    /**
     * Проверяет текущий символ и при совпадении
     * перемещает позицию на следующий символ.
     *
     * @param expected ожидаемый символ.
     * @return true, если символ совпал; иначе false.
     */
    private fun match(expected: Char): Boolean {
        if (
            position < expression.length &&
            expression[position] == expected
        ) {
            position++
            return true
        }

        return false
    }
}

