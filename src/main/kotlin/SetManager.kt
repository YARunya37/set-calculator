package org.example

class SetManager {

    // Универсальное множество.
    // Может быть не задано.
    private var universalSet: Set? = null

    // Массив множеств A, B, C, D, E.
    private val sets = arrayOfNulls<Set>(5)

    /**
     * Возвращает универсальное множество.
     *
     * Если U задано вручную — возвращается оно.
     *
     * Если U не задано — формируется объединение
     * всех множеств A, B, C, D и E.
     */
    fun getUniversalSet(): Set {
        if (universalSet != null) {
            return universalSet!!
        }

        var result = Set(0)

        for (set in sets) {
            if (set != null) {
                result = result union set
            }
        }

        return result
    }

    /**
     * Задаёт универсальное множество U.
     */
    fun setUniversalSet(set: Set) {
        universalSet = set
    }

    /**
     * Удаляет заданное универсальное множество.
     */
    fun clearUniversalSet() {
        universalSet = null
    }

    /**
     * Возвращает множество по его имени.
     *
     * Поддерживаются A, B, C, D и E.
     */
    fun getSet(name: Char): Set? {
        val index = getSetIndex(name)

        return if (index != -1) {
            sets[index]
        } else {
            null
        }
    }

    /**
     * Устанавливает множество A-E.
     *
     * Если U задано, проверяется, что каждый элемент
     * нового множества принадлежит U.
     */
    fun setSet(name: Char, set: Set): Boolean {
        val index = getSetIndex(name)

        if (index == -1) {
            return false
        }

        // Если U задано, множество должно быть
        // подмножеством универсального множества.
        if (universalSet != null && !(set isSubsetOf universalSet!!)) {
            return false
        }

        sets[index] = set
        return true
    }

    /**
     * Добавляет элемент в множество A-E.
     *
     * Если U задано, элемент должен содержаться в U.
     *
     * Если U не задано, элемент добавляется без проверки.
     */
    fun addElement(name: Char, element: Int): Boolean {
        val index = getSetIndex(name)

        if (index == -1) {
            return false
        }

        // Если U задано, проверяем принадлежность элемента U.
        if (universalSet != null && !universalSet!!.contains(element)) {
            return false
        }

        val set = sets[index]

        if (set == null) {
            return false
        }

        return set.add(element)
    }

    /**
     * Возвращает индекс множества в массиве.
     *
     * A -> 0
     * B -> 1
     * C -> 2
     * D -> 3
     * E -> 4
     *
     * Если имя неизвестно, возвращает -1.
     */
    private fun getSetIndex(name: Char): Int {
        return when (name.uppercaseChar()) {
            'A' -> 0
            'B' -> 1
            'C' -> 2
            'D' -> 3
            'E' -> 4
            else -> -1
        }
    }
}