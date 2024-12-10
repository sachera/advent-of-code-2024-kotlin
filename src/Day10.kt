data class Point(val x: Int, val y: Int) {
    val left by lazy { Point(x - 1, y) }
    val top by lazy { Point(x, y - 1) }
    val right by lazy { Point(x + 1, y) }
    val bottom by lazy { Point(x, y + 1) }
}

operator fun <T> List<List<T>>.get(pos: Point) = this[pos.y][pos.x]

operator fun <T> List<MutableList<T>>.set(pos: Point, value: T) { this[pos.y][pos.x] = value }

fun main() {
    val input = readInputLines("Day10")

    // common
    val map = input.map { line -> line.map { it.digitToInt() } }

    fun <T> ifCanReach(
        map: List<List<Int>>,
        pos: Point,
        neighbors: Point,
        empty: T,
        thenF: (Point) -> T
    ): T {
        if(neighbors.x < 0 || neighbors.x >= map[0].size || neighbors.y < 0 || neighbors.y >= map.size) {
            return empty
        }
        if(map[pos] != map[neighbors] - 1) {
            return empty
        }
        return thenF(neighbors)
    }

    fun <T> calculateFrom(
        map: List<List<Int>>,
        knownReachable: List<MutableList<T?>>,
        peak: (Point) -> T,
        empty: T,
        sum: (T,T,T,T) -> T,
        pos: Point
    ): T {
        return knownReachable[pos] ?: run {
            val actualReachable = if(map[pos] == 9) {
                peak(pos)
            } else {
                sum(
                    ifCanReach(map, pos, pos.left, empty) { calculateFrom(map, knownReachable, peak, empty, sum, it) },
                    ifCanReach(map, pos, pos.top, empty) { calculateFrom(map, knownReachable, peak, empty, sum, it) },
                    ifCanReach(map, pos, pos.right, empty) { calculateFrom(map, knownReachable, peak, empty, sum, it) },
                    ifCanReach(map, pos, pos.bottom, empty) { calculateFrom(map, knownReachable, peak, empty, sum, it) }
                )
            }
            knownReachable[pos] = actualReachable
            actualReachable
        }
    }

    fun <T> calculateMemorized(
        map: List<List<Int>>,
        peak: (Point) -> T,
        empty: T,
        sum: (T,T,T,T) -> T,
    ): List<List<T>> {
        val scores = List(map.size) {
            List<T?>(map[0].size) { null }.toMutableList()
        }
        for (y in map.indices) {
            for (x in map[y].indices) {
                calculateFrom(map, scores, peak, empty, sum, Point(x, y))
            }
        }
        return scores.map { it.map { score -> score ?: empty } }
    }

    fun sumAtTrailheads(map: List<List<Int>>, values: List<List<Int>>): Int = map.mapIndexed { y, row ->
        row.mapIndexed { x, height ->
            if(height == 0) values[y][x] else 0
        }.sum()
    }.sum()

    // part 1
    fun calculateReachable(map: List<List<Int>>) = calculateMemorized(
        map = map,
        peak = { pos -> setOf(pos) },
        empty = emptySet(),
        sum = { a,b,c,d ->  a + b + c + d }
    ).map {
        it.map { reachablePeakSet -> reachablePeakSet.size }
    }

    val reachable: List<List<Int>> = calculateReachable(map)
    val trailheadReachablePeaks = sumAtTrailheads(map, reachable)

    println(trailheadReachablePeaks) // 496

    // part 2
    fun calculateScores(map: List<List<Int>>) = calculateMemorized(
        map = map,
        peak = { _ -> 1 },
        empty = 0,
        sum = { a,b,c,d ->  a + b + c + d }
    )

    val scores: List<List<Int>> = calculateScores(map)
    val trailheadScores = sumAtTrailheads(map, scores)

    println(trailheadScores)
}
