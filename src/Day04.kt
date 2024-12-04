private operator fun <E> List<E>.component6(): E  = this[5]

fun main() {
    val input = readInputLines("Day04")

    // assumes every line has the same length
    fun countWord(word: String, text: List<String>): Int {
        fun testWord(from: Int, atX: Int, atY: Int, dx: Int, dy: Int): Boolean =
            from == word.length || (
                    text[atY][atX] == word[from] &&
                    testWord(from+1, atX+dx, atY+dy, dx, dy)
            )

        return text.indices.sumOf { y ->
            text[y].indices.sumOf { x ->
                (-1 .. 1).sumOf { dy ->
                    (-1 .. 1).count { dx ->
                        // ignore non sensible case
                        (dx != 0 || dy != 0) &&
                                // range check
                                0 <= x + (word.length-1) * dx &&
                                x + word.length * dx <= text[0].length &&
                                0 <= y + (word.length-1) * dy &&
                                y + word.length * dy <= text.size &&
                                // word test
                                testWord(0, x, y, dx, dy)
                    }
                }
            }
        }
    }
    println(countWord("XMAS", input))

    // assumes every line has the same length
    fun countWordX(word: String, text: List<String>): Int {
        fun testWord(from: Int, atX: Int, atY: Int, dx: Int, dy: Int): Boolean =
            from == word.length || (
                    text[atY][atX] == word[from] &&
                            testWord(from+1, atX+dx, atY+dy, dx, dy)
                    )

        return text.indices.sumOf { y ->
            text[y].indices.sumOf { x ->
                listOf(1,-1).sumOf { dy ->
                    listOf(1,-1).count { dx ->
                        0 <= x + (word.length-1) * dx &&
                                x + word.length * dx <= text[0].length &&
                                0 <= y + (word.length-1) * dy &&
                                y + word.length * dy <= text.size && (
                                testWord(0, x, y, dx, dy) && (
                                        testWord(0, x + dx * (word.length-1), y, -dx, dy) ||
                                        testWord(0, x, y + dy*(word.length-1), dx, -dy)
                                )
                        )
                    }
                }
            }
        } / 2 // we will find every X twice
    }
    println(countWordX("MAS", input))
}
