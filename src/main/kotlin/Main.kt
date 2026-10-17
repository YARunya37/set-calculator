package org.example

fun main() {
    val setman = SetManager()

    val consoleMenu = ConsoleMenu(setman)

    consoleMenu.run()
}