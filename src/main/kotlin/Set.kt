package org.example

class Set(capacity: Int) : Iterable<Int> {

    private val elements = IntArray(capacity)
    private var size = 0

    /**
     * Добавляет элемент в множество.
     *
     * @return true, если элемент был добавлен;
     *         false, если такой элемент уже есть или массив заполнен.
     */
    fun add(element: Int): Boolean {
        if (size == elements.size) {
            return false
        }

        // Ищем позицию, куда нужно вставить элемент,
        // сохраняя массив отсортированным.
        var position = 0

        while (position < size && elements[position] < element) {
            position++
        }

        // Такой элемент уже есть.
        if (position < size && elements[position] == element) {
            return false
        }

        // Сдвигаем элементы вправо.
        var i = size
        while (i > position) {
            elements[i] = elements[i - 1]
            i--
        }

        elements[position] = element
        size++

        return true
    }

    /**
     * Проверяет, содержится ли элемент в множестве.
     * Бинарный поиск
     */
    fun contains(element: Int): Boolean {
        var left = 0
        var right = size - 1

        while (left <= right) {
            val middle = (left + right) / 2

            when {
                elements[middle] == element -> return true
                elements[middle] < element -> left = middle + 1
                else -> right = middle - 1
            }
        }

        return false
    }

    // Чтение элементов множества по индексу
    operator fun get(index: Int): Int {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Индекс выходит за границы множества: $index")
        }

        return elements[index]
    }

    /**
     * Перегрузка итератора,
     * чтобы по множеству можно было проходиться в циклах
     */
    override fun iterator(): Iterator<Int> {
        return object : Iterator<Int> {
            private var index = 0

                override fun hasNext(): Boolean {
                    return index < size
                }

                override fun next(): Int {
                    if (!hasNext()) {
                        throw NoSuchElementException()
                    }

                    return elements[index++]
                }
            }
        }


    /**
     * Текущее количество элементов в множестве.
     */
    fun size(): Int {
        return size
    }
}