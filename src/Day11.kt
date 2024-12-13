import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

fun Long.digitCount() = when(this) {
    0L -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun main() {
    val input = readInput("Day11")

    // common
    val stones = input.split("\\s+".toRegex()).map(String::toLong)
    fun blink(sequence: Sequence<Pair<Long,Long>>): Sequence<Pair<Long,Long>> = sequence
        .flatMap {
            val (factor,graved) = it
            val numberOfDigits = graved.digitCount()
            when {
                graved == 0L -> sequenceOf(Pair(factor,1L))
                numberOfDigits % 2 == 0 -> {
                    val divider = 10.0.pow(numberOfDigits / 2).toLong()
                    sequenceOf(Pair(factor, graved / divider), Pair(factor, graved % divider))
                }
                else -> sequenceOf(Pair(factor,graved * 2024L))
            }
        }
        .groupBy { it.second }
        .map { (graved,pairs) -> Pair(pairs.sumOf { it.first }, graved) }
        .asSequence()

    val infiniteSequence = generateSequence(stones.asSequence().map { Pair(1L,it) }) { blink(it) }

    // part 1
    println(infiniteSequence.drop(25).first().sumOf { it.first })

    // part 2
    println(infiniteSequence.drop(75).first().sumOf { it.first })
}
