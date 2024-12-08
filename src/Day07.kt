import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val input = readInputLines("Day07")

    val calibrations = input.map { line ->
        val (res,rem) = line.split(':', limit = 2)
        val numbers = rem.trim().split("""\s+""".toRegex())

        res.toLong() to numbers.map { it.toLong() }
    }

    // common
    fun possibleTotals(numbers: List<Long>, operations: List<(Long,Long) -> Long>): Set<Long> =
        numbers.drop(1).fold(setOf(numbers[0])) { acc, value ->
            acc.flatMap { operations.map { f -> f(it, value) } }.toSet()
        }

    // part 1
    val simpleOperations = listOf<(Long,Long) -> Long>({ a, b -> a + b }, { a, b -> a * b })
    val totalCalibrationResult = calibrations.filter {
        possibleTotals(it.second, simpleOperations).contains(it.first)
    }.sumOf { it.first }
    println(totalCalibrationResult)

    // part 2
    fun concat(a: Long, b: Long): Long {
        val numberOfDigits = floor(log10(b.toDouble())) + 1
        val factor = 10.0.pow(numberOfDigits).toLong()
        return a * factor + b
    }

    val extendedOperations = simpleOperations + { a, b -> concat(a,b) }
    val totalCalibrationResultExtended = calibrations.filter {
        possibleTotals(it.second, extendedOperations).contains(it.first)
    }.sumOf { it.first }
    println(totalCalibrationResultExtended)
}
