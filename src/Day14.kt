data class Robot(val posX: Int, val posY: Int, val velocityX: Int, val velocityY: Int)

private fun Iterable<Int>.product(): Int = this.fold(1) { acc, elem -> acc * elem }

fun main() {
    val input = readInputLines("Day14")
    val width = 101
    val height = 103

    val robotRegex = """p=(\d+),(\d+) v=([+-]?\d+),([+-]?\d+)""".toRegex()
    val robots = input.map {
        val (_,rX,rY,vX,vY) = robotRegex.matchEntire(it)!!.groupValues
        Robot(rX.toInt(), rY.toInt(), vX.toInt(), vY.toInt())
    }

    // common
    fun naturalMod(a: Int, b: Int) = (a%b+b)%b

    fun pos(r: Robot, steps: Int): Pair<Int,Int> {
        val x = naturalMod(r.posX + steps * r.velocityX, width)
        val y = naturalMod(r.posY + steps * r.velocityY, height)

        return Pair(x,y)
    }

    // part 1
    val centerX = (width-1)/2
    val centerY = (height-1)/2

    fun quadrant(r: Robot, steps: Int): Int? {
        val (x,y) = pos(r, steps)

        return when {
            x < centerX && y < centerY -> 0
            x > centerX && y < centerY -> 1
            x < centerX && y > centerY -> 2
            x > centerX && y > centerY -> 3
            else -> null // either x == centerX or y == centerY
        }
    }

    fun countQuadrants(robots: List<Robot>, steps: Int): Map<Int?,Int> = robots
        .groupingBy { quadrant(it, steps) }
        .eachCount()

    println(countQuadrants(robots, 100)
        .mapNotNull { (key,c) -> key?.let { c } }
        .product()
    )

    // part 2
    val field = Array(height) { CharArray(width) { '.' } }

    fun picture(steps: Int) {
        // reset
        field.forEach { it.fill('.') }

        robots.forEach {
            val (x,y) = pos(it, steps)
            field[y][x] = 'X'
        }

        println(steps)
        field.forEach {
            println(String(it))
        }
        println()
    }

    fun isTree(robots: List<Robot>, steps: Int): Boolean {
        val positions = robots.map { pos(it, steps) }.toSet()

        return positions.any { (x,y) ->
            val neighbours = listOf(
                Pair(0,1),Pair(1,0),Pair(0,-1),Pair(-1,0),
                Pair(1,1),Pair(-1,1),Pair(1,-1),Pair(-1,-1)
            ).map { (dx,dy) -> Pair(x+dx, y+dy) }
            positions.containsAll(neighbours)
        }
    }

    val steps = generateSequence(0) { it+1 }
        .dropWhile { !isTree(robots, it) }
        .first()
    picture(steps)
}
