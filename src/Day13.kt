import kotlin.math.round

data class Machine(val xA: Long, val yA:Long, val xB:Long, val yB:Long, val priceX:Long, val priceY:Long)

fun main() {
    val input = readInputLines("Day13")

    val buttonRegex = """Button .: X([+-]?\d+), Y([+-]?\d+)""".toRegex()
    val priceRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()
    val machines = input.windowed(size = 3, step = 4).map {
        val (_, xA, yA) = buttonRegex.matchEntire(it[0])!!.groupValues
        val (_, xB, yB) = buttonRegex.matchEntire(it[1])!!.groupValues
        val (_, priceX, priceY) = priceRegex.matchEntire(it[2])!!.groupValues
        Machine(xA.toLong(), yA.toLong(), xB.toLong(), yB.toLong(), priceX.toLong(), priceY.toLong())
    }

    // common
    fun solve(xA: Long, yA:Long, xB:Long, yB:Long, priceX:Long, priceY: Long): Long? {
        // will be correct iff there is an integer solution
        // but that's good enough since we are only interested in integer solutions
        val f = yA.toDouble() / xA
        val b = round(((priceY-priceX*f) / (yB - xB*f))).toLong()
        val a = (priceX - b * xB) / xA

        // check if correct, discard otherwise
        // (which is exactly the case if there is no integer solution as argued above)
        if(a*xA + b*xB != priceX || a*yA + b*yB != priceY) return null

        return 3*a + b
    }

    // part 1
    println(machines.mapNotNull { (xA, yA, xB, yB, priceX, priceY) ->
        solve(xA, yA, xB, yB, priceX, priceY)
    }.sum())

    // part 2
    println(machines.mapNotNull { (xA, yA, xB, yB, priceX, priceY) ->
        solve(xA, yA, xB, yB, priceX+10000000000000, priceY+10000000000000)
    }.sum())
}
