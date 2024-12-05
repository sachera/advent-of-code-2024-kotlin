private operator fun <E> List<E>.component6(): E  = this[5]

fun main() {
    val input = readInputLines("Day05")
    val forbiddenRules = input
        .takeWhile { it.isNotEmpty() }
        .map { line ->
            val (a,b) = line.split("|")
            Pair(b.toInt(), a.toInt())
        }
        .groupBy { it.first }
        .mapValues { (_,snd) ->
            snd.map { it.second }.toSet()
        }
    val updates = input
        .dropWhile { it.isNotEmpty() }
        .drop(1)
        .map { line ->
            line.split(",").map(String::toInt)
        }

    fun isOrderedCorrectly(update: List<Int>, forbiddenRules: Map<Int,Set<Int>>) =
        update.fold(Pair(true, emptySet<Int>())) { (correct,forbidden), next ->
            Pair(
                correct && !forbidden.contains(next),
                forbidden + forbiddenRules.getOrDefault(next, emptySet())
            )
        }.first

    val (correct,incorrect) = updates.partition { isOrderedCorrectly(it, forbiddenRules) }

    val correctMiddleNumberSum = correct.sumOf { it[it.size / 2] }
    println(correctMiddleNumberSum)

    val comparator = Comparator<Int> { a, b ->
        when {
            forbiddenRules.getOrDefault(a, emptySet()).contains(b) -> -1
            forbiddenRules.getOrDefault(b, emptySet()).contains(a) -> 1
            else -> 0
        }
    }
    val incorrectReorderedMiddleNumber = incorrect
        .map { it.sortedWith(comparator) }
        .sumOf { it[it.size / 2] }
    println(incorrectReorderedMiddleNumber)
}
