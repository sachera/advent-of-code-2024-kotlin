private operator fun <E> List<E>.component6(): E  = this[5]

fun main() {
    val input = readInput("Day03")

    val uncorruptedRegex = """mul\((\d+),(\d+)\)""".toRegex()

    val productSum = uncorruptedRegex.findAll(input)
        .sumOf {
            val (_,a,b) = it.groupValues
            a.toInt() * b.toInt()
        }
    println(productSum)

    val statementRegexp = """(do\(\))|(don't\(\))|(mul\((\d+),(\d+)\))""".toRegex()
    val (_,enabledProductSum) = statementRegexp.findAll(input)
        .fold(true to 0) { (enabled,sum), match ->
            val (_,start,end,_,a,b) = match.groupValues
            when {
                start != "" ->
                    true to sum
                end != "" ->
                    false to sum
                enabled && a != "" && b != "" ->
                    true to (sum + a.toInt() * b.toInt())
                else ->
                    enabled to sum
            }

        }
    println(enabledProductSum)
}
