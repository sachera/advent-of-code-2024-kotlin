import kotlin.math.absoluteValue

fun main() {
    // Or read a large test input from the `src/Day01_test.txt` file:
    val (listA, listB) = readInputLines("Day01").map {
        val (fst,snd) = it.split("\\s+".toRegex())
        Pair(fst.toInt(),snd.toInt())
    }.unzip()

    val diffSum = listA.sorted().zip(listB.sorted()).sumOf { (a, b) ->
        (a - b).absoluteValue
    }
    println(diffSum)

    val testSet = listA.toSet()
    val similarity = listB.filter { testSet.contains(it) }
        .groupBy { it }
        .map { it.key * it.value.size }
        .sum()

    println(similarity)
}
