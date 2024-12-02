import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val input = readInputLines("Day02")

    fun isSafe(report: List<Int>): Boolean {
        val zipped = report.zipWithNext()
        val expectedSign = (zipped[0].first - zipped[0].second).sign
        val isMonotone = zipped.all { (a,b) -> (a-b).sign == expectedSign }
        val isGraduallyChanging = zipped.all { (a,b) -> a != b && (a-b).absoluteValue <= 3 }
        return isMonotone && isGraduallyChanging
    }

    val safeReports = input.map { line ->
        line.split(" ").map { it.toInt() }
    }.count { report ->
        isSafe(report)
    }
    println(safeReports)

    val safeReportsWithDampener = input.map { line ->
        line.split(" ").map { it.toInt() }
    }.count { report ->
        isSafe(report) || report.indices.any { i ->
            isSafe(report.take(i) + report.drop(i+1))
        }
    }
    println(safeReportsWithDampener)
}
