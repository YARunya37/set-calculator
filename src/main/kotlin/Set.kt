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
     * Объединение множеств.
     *
     * Возвращает новое множество, содержащее все элементы
     * текущего множества и другого множества.
     */
    infix fun union(other: Set): Set {
        val result = Set(this.size + other.size)

        var i = 0
        var j = 0

        // Одновременно проходим по двум отсортированным массивам.
        while (i < this.size && j < other.size) {
            when {
                // Элемент текущего множества меньше.
                this.elements[i] < other.elements[j] -> {
                    result.add(this.elements[i])
                    i++
                }

                // Элемент другого множества меньше.
                this.elements[i] > other.elements[j] -> {
                    result.add(other.elements[j])
                    j++
                }

                // Элементы равны — добавляем только один.
                else -> {
                    result.add(this.elements[i])
                    i++
                    j++
                }
            }
        }

        // Добавляем оставшиеся элементы текущего множества.
        while (i < this.size) {
            result.add(this.elements[i])
            i++
        }

        // Добавляем оставшиеся элементы другого множества.
        while (j < other.size) {
            result.add(other.elements[j])
            j++
        }

        return result
    }

    /**
     * Пересечение множеств.
     *
     * Возвращает новое множество, содержащее только те элементы,
     * которые присутствуют одновременно в обоих множествах.
     */
    infix fun intersection(other: Set): Set {
        // Размер пересечения не может быть больше
        // размера меньшего из двух множеств.
        val result = Set(minOf(this.size, other.size))

        var i = 0
        var j = 0

        // Проходим по обоим отсортированным массивам.
        while (i < this.size && j < other.size) {
            when {
                // Элемент текущего множества меньше —
                // его нет в оставшейся части другого множества.
                this.elements[i] < other.elements[j] -> {
                    i++
                }

                // Элемент другого множества меньше —
                // его нет в оставшейся части текущего множества.
                this.elements[i] > other.elements[j] -> {
                    j++
                }

                // Элементы равны — добавляем в результат.
                else -> {
                    result.add(this.elements[i])
                    i++
                    j++
                }
            }
        }

        return result
    }

    /**
     * Разность множеств.
     *
     * Возвращает новое множество, содержащее элементы
     * текущего множества, которых нет в другом множестве.
     */
    infix fun difference(other: Set): Set {
        // В худшем случае результат содержит
        // все элементы текущего множества.
        val result = Set(this.size)

        var i = 0
        var j = 0

        // Сравниваем элементы двух отсортированных массивов.
        while (i < this.size && j < other.size) {
            when {
                // Элемент текущего множества меньше —
                // значит, его нет в другом множестве.
                this.elements[i] < other.elements[j] -> {
                    result.add(this.elements[i])
                    i++
                }

                // Элемент другого множества меньше —
                // двигаемся по нему дальше.
                this.elements[i] > other.elements[j] -> {
                    j++
                }

                // Элементы равны — этот элемент
                // не добавляем в результат.
                else -> {
                    i++
                    j++
                }
            }
        }

        // Добавляем оставшиеся элементы текущего множества.
        while (i < this.size) {
            result.add(this.elements[i])
            i++
        }

        return result
    }

    /**
     * Симметрическая разность множеств.
     *
     * Возвращает новое множество, содержащее элементы,
     * которые принадлежат только одному из двух множеств.
     */
    infix fun symmetricDifference(other: Set): Set {
        // В худшем случае все элементы обоих множеств
        // попадут в результат.
        val result = Set(this.size + other.size)

        var i = 0
        var j = 0

        // Проходим по двум отсортированным массивам.
        while (i < this.size && j < other.size) {
            when {
                // Элемент есть только в текущем множестве.
                this.elements[i] < other.elements[j] -> {
                    result.add(this.elements[i])
                    i++
                }

                // Элемент есть только в другом множестве.
                this.elements[i] > other.elements[j] -> {
                    result.add(other.elements[j])
                    j++
                }

                // Элемент есть в обоих множествах —
                // в симметрическую разность он не попадает.
                else -> {
                    i++
                    j++
                }
            }
        }

        // Добавляем оставшиеся элементы текущего множества.
        while (i < this.size) {
            result.add(this.elements[i])
            i++
        }

        // Добавляем оставшиеся элементы другого множества.
        while (j < other.size) {
            result.add(other.elements[j])
            j++
        }

        return result
    }

    /**
     * Текущее количество элементов в множестве.
     */
    fun size(): Int {
        return size
    }
}