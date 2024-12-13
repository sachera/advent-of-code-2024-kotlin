data class Region(val plant: Char, val size: Int, val perimeter: Int, val sides: Int)

fun main() {
    val input = readInputLines("Day12")

    val plotArray = input.map { it.toCharArray() }.toTypedArray()

    // common
    fun testPlant(char: Char, x: Int, y: Int) = plotArray.getOrNull(y)?.getOrNull(x) == char

    fun markPlots(plotArray: Array<CharArray>): List<Region> {
        val markings = Array(plotArray.size) { Array(plotArray[0].size) { false } }

        val directions = listOf(Pair(0,1), Pair(1,0),Pair(0,-1),Pair(-1,0))

        fun testCorner(top: Boolean, right: Boolean, topRight: Boolean): Boolean =
            (!top && !right) || (top && right && !topRight)

        fun plotCorners(pattern: List<List<Boolean>>): Int = listOf(
            Triple(pattern[0][1], pattern[1][0], pattern[0][0]),
            Triple(pattern[0][1], pattern[1][2], pattern[0][2]),
            Triple(pattern[1][0], pattern[2][1], pattern[2][0]),
            Triple(pattern[2][1], pattern[1][2], pattern[2][2]),
        ).count { (top, right, topRight) -> testCorner(top, right, topRight) }

        fun markArea(plant: Char, x: Int, y: Int): Region? {
            if(markings[y][x]) return null
            markings[y][x] = true

            var regionSize = 1
            val pattern = (-1 .. 1).map { dy -> (-1 .. 1).map { dx -> testPlant(plant, x+dx, y+dy) } }
            var regionPerimeter = directions.count { (dx,dy) -> !pattern[dy+1][dx+1] }
            // number of sides == number of corners
            var regionSides = plotCorners(pattern)
            for((dx,dy) in listOf(Pair(0,1), Pair(1,0),Pair(0,-1),Pair(-1,0))) {
                if(testPlant(plant, x+dx, y+dy)) {
                    markArea(plant, x+dx, y+dy)?.let { (_,size,addPerimeter,addSides) ->
                        regionSize += size
                        regionPerimeter += addPerimeter
                        regionSides += addSides
                    }
                }
            }
            return Region(plant, regionSize, regionPerimeter, regionSides)
        }

        return plotArray.flatMapIndexed { y, row ->
            row.mapIndexed { x, plant ->
                markArea(plant, x, y)?.also { region ->
                    println("Region $y, $x: $region")
                }
            }.filterNotNull()
        }
    }

    val regions = markPlots(plotArray)

    // part 1
    println(regions.sumOf { it.size * it.perimeter })

    // part 2
    println(regions.sumOf { it.size * it.sides })
}
