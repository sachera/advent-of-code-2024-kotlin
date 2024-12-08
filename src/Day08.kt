fun main() {
    val input = readInputLines("Day08")

    val height = input.size
    val width = input[0].length

    val antennaLocations = input
        .flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, char ->
                when(char) {
                    '.' -> null
                    else -> Pair(char, Pair(x,y))
                }
            }
        }
        .groupBy { it.first }
        .mapValues { entry ->
            entry.value.map { it.second }
        }

    // common
    fun isInBounds(pos: Pair<Int,Int>) =
        pos.first in 0 ..< width && pos.second in 0 ..< height

    fun calculateImpactLocations(
        antennas: List<Pair<Int,Int>>,
        calculateLocations: (Pair<Int,Int>, Pair<Int,Int>) -> Set<Pair<Int,Int>>,
    ): Set<Pair<Int,Int>> {
        val mutableLocations = antennas.toMutableList()
        val result = mutableSetOf<Pair<Int,Int>>()
        while(mutableLocations.isNotEmpty()) {
            val a = mutableLocations.removeFirst()
            result.addAll(
                mutableLocations.flatMap { b ->
                    calculateLocations(a, b)
                }
            )
        }
        return result.toSet()
    }

    // part 1
    fun locations(a: Pair<Int,Int>, b: Pair<Int,Int>): Set<Pair<Int,Int>> {
        val (aX,aY) = a
        val (bX,bY) = b
        val dX = aX - bX
        val dY = aY - bY
        return listOf(Pair(aX+dX,aY+dY), Pair(bX-dX,bY-dY))
            .filter { location -> isInBounds(location) }
            .toSet()
    }

    val uniqueAntinodeLocations = antennaLocations.flatMap { (_, positions) ->
        calculateImpactLocations(positions) { a, b -> locations(a,b) }
    }.toSet()

    println(uniqueAntinodeLocations.size)

    // part 2
    fun locationsWithHarmonics(a: Pair<Int,Int>, b: Pair<Int,Int>): Set<Pair<Int,Int>> {
        val dX = b.first - a.first
        val dY = b.second - a.second

        val test = { loc: Pair<Int,Int> -> isInBounds(loc) }
        return generateSequence(a) { (x,y) -> Pair(x+dX,y+dY) }.takeWhile(test).toSet() +
                generateSequence(a) { (x,y) -> Pair(x-dX,y-dY) }.takeWhile(test).toSet()
    }

    val uniqueAntinodeLocationsWithHarmonics = antennaLocations.flatMap { (_, positions) ->
        calculateImpactLocations(positions) { a, b -> locationsWithHarmonics(a,b) }
    }.toSet()

    println(uniqueAntinodeLocationsWithHarmonics.size)
}
