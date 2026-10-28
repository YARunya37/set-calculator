package org.example

import kotlin.random.Random

class ConsoleMenu(
    private val setManager: SetManager,
) {

    private val setParser: SetParser = SetParser(setManager)

    private var universalCreated = false
    fun run() {
        showStartScreen()
        showMainMenu()
    }

    private fun showStartScreen() {
        println(
            """
            |========== КАЛЬКУЛЯТОР МНОЖЕСТВ ==========
            |
            |Универсальное множество U
            |
            |1. Задать универсальное множество вручную
            |2. Сформировать U автоматически на основе объединения других множеств
            |
            |------------------------------------------
            """.trimMargin()
        )

        when (
            readValidInput(
                listOf("1", "2"),
                "Выберите действие: ",
                "Ошибка: выберите 1 или 2.\n",
            )
        ) {
            "1" -> {
                setManager.setUniversalSet(readSetElements())
                universalCreated = true
            }

            "2" -> setManager.clearUniversalSet()
        }
    }

    private fun readValidInput(
        validValues: Collection<String>,
        message: String,
        errorMessage: String,
        normalize: (String) -> String = { it.trim() },
    ): String {
        var input: String?

        do {
            print(message)
            input = readlnOrNull()?.let(normalize)

            if (input == null || input !in validValues) {
                println(errorMessage)
            }
        } while (input == null || input !in validValues)

        return input
    }

    /**
     * Единая функция ввода элементов любого множества.
     */
    private fun readSetElements(): Set {
        println(
            """Выберите как заполнить множество:
                |1. Вручную
                |2. Случайными числами [0, 100]
            """.trimMargin()
        )

        return when (
            readValidInput(
                listOf("1", "2"),
                "",
                "Ошибка ввода! Введите число, соответствующее выбранной команде",
            )
        ) {
            "1" -> readUserInput()
            "2" -> randomSet()
            else -> randomSet()
        }
    }

    private fun readUserInput(): Set{
        var valid = false
        val set = Set(100)

        while (!valid) {
            println(
                """
                |Введите целые числа через пробел или запятую.
                |Пример: 1, 2, 3, 4, 5
                |Для создания пустого множества оставьте строку пустой.
                """.trimMargin()
            )
            print("Элементы: ")

            val input = readlnOrNull() ?: return Set(0)

            if (input.isBlank()) {
                return set
            }

            valid = true

            val elements = input.trim().split(Regex("[,\\s]+"))

            for (element in elements) {
                val number = element.toIntOrNull()

                if (number == null) {
                    println("Ошибка: '$element' не является целым числом.")
                    valid = false
                    break
                }

                set.add(number)
            }

            println("Повторите ввод.")
        }

        return set
    }

    private fun randomSet(): Set{
        val result = Set(100)

        // Количество
        println("Введите число элементов:")

        var count = readln().toIntOrNull()
        while(count == null || count < 0){
            println("Ошибка ввода! Необходимо ввести целое число >= 0")
            count = readln().toIntOrNull()
        }

        if(universalCreated){
            val universal = setManager.getUniversalSet()

            if(universal.size() <= count){
                println("Т. к. введенное число элементов больше или равно, чем есть в универсальном, то множество будет эквивалентно универсальному")

                // Копируем U в result
                for (element in universal){
                    result.add(element)
                }

                return result
            }

            for(i in 0..< count){
                // Если такое значение уже есть в универсальном множестве, то оно не добавиться. Нужно сгенерировать заново
                while(!result.add(universal[Random.nextInt(0, universal.size()-1)])){}
            }
        }
        else{
            // Генерируем случайные числа в диапазоне [0, 100] и добавляем их в рещультат
            for(i in 1..count){
                result.add(Random.nextInt(0, 100))
            }
        }

        return result
    }
    private fun showMainMenu() {
        while (true) {
            println(
                """
                |========== КАЛЬКУЛЯТОР МНОЖЕСТВ ==========
                |
                |U = ${buildSetContent(setManager.getUniversalSet())}
                |
                |A = ${buildSetContent(setManager.getSet('A'))}
                |B = ${buildSetContent(setManager.getSet('B'))}
                |C = ${buildSetContent(setManager.getSet('C'))}
                |D = ${buildSetContent(setManager.getSet('D'))}
                |E = ${buildSetContent(setManager.getSet('E'))}
                |
                |------------------------------------------
                |
                |1. Ввести выражение
                |2. Создать / изменить множество
                |0. Выход
                """.trimMargin()
            )
            when (
                readValidInput(
                    listOf("1", "2", "0"),
                    "Выберите действие: ",
                    "Ошибка: выберите 1, 2 или 0.",
                )
            ) {
                "1" -> showExpressionScreen()
                "2" -> showSetMenu()
                "0" -> {
                    println("Работа программы завершена.")
                    return
                }
            }
        }
    }

    /**
     * Выбор множества для создания или изменения.
     */
    private fun showSetMenu() {
        println(
            """
            |========== СОЗДАНИЕ МНОЖЕСТВА ==========
            |
            |Выберите множество:
            |A
            |B
            |C
            |D
            |E
            |
            |----------------------------------------
            |Введите 0 для возврата в главное меню.
            """.trimMargin()
        )

        val input = readValidInput(
            listOf("A", "B", "C", "D", "E", "0"),
            "Множество: ",
            "Ошибка: выберите A, B, C, D или E.",
            normalize = { it.trim().uppercase() },
        )

        if (input == "0") {
            return
        }

        val name = input[0]
        val newSet = readSetElements()

        val success = setManager.setSet(name, newSet)

        if (success) {
            println("Множество $name обновлено: ${buildSetContent(newSet)}")
        } else {
            println(
                "Ошибка: элементы множества $name должны принадлежать U."
            )
        }
    }

    private fun showExpressionScreen() {
        while (true) {
            println(
                """
                |============ ВВОД ВЫРАЖЕНИЯ ============
                |
                |Доступные множества:
                |A = ${buildSetContent(setManager.getSet('A'))}
                |B = ${buildSetContent(setManager.getSet('B'))}
                |C = ${buildSetContent(setManager.getSet('C'))}
                |D = ${buildSetContent(setManager.getSet('D'))}
                |E = ${buildSetContent(setManager.getSet('E'))}
                |
                |Операции:
                ||  — объединение (∪)
                |&  — пересечение (∩)
                |-  — разность (∖)
                |^  — симметрическая разность (△)
                |~  — дополнение относительно U
                |( ) — изменение порядка вычислений
                |
                |Приоритет операций: ~, ^, -, &, |
                |
                |Пример:
                |(A | B) & ~C
                |
                |----------------------------------------
                |Введите 0 для возврата в главное меню.
                """.trimMargin()
            )
            print("> ")

            val input = readlnOrNull()?.trim() ?: return

            if (input == "0") {
                return
            }

            try {
                val result = setParser.parse(input)

                println(
                    """
                    |Результат вычисления:
                    |$input = ${buildSetContent(result)}
                    |
                    |Нажмите enter, чтобы продолжить""".trimMargin()
                )
                readln()
            } catch (e: IllegalArgumentException) {
                println("Ошибка: ${e.message}")
            }
        }
    }

    private fun buildSetContent(set: Set?): String {
        return set?.joinToString(
            prefix = "[ ",
            postfix = " ]",
            separator = ", "
        ) ?: "не задано"
    }
}

