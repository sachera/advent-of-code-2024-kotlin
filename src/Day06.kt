private operator fun <E> List<E>.component6(): E  = this[5]

enum class Direction(val indicator: Char) {
    Left('<'),
    Up('^'),
    Right('>'),
    Down('v');

    fun next(x: Int, y: Int) = when(this) {
        Left -> (x-1) to y
        Right -> (x+1) to y
        Up -> x to (y-1)
        Down -> x to (y+1)
    }

    val rotateRight by lazy {
        Direction.entries[(this.ordinal+1) % Direction.entries.size]
    }

    companion object {
        val indicators = entries.map { it.indicator }

        fun from(char: Char) = entries.first { it.indicator == char }
    }
}

sealed interface Field
data object Blocked: Field { override fun toString(): String = "#" }
data class Free(val visited: Set<Direction>): Field {
    override fun toString(): String = if(visited.isEmpty()) "." else "X"

    fun visit(direction: Direction) = (field.visited + direction).let { newSet ->
        cache.getOrPut(newSet) { Free(newSet) }
    }

    companion object {
        private val cache = mutableMapOf<Set<Direction>,Free>()

        val field = Free(emptySet())
    }
}

fun path(map: Array<Array<Field>>, startX: Int, startY: Int, startDirection: Direction): List<Pair<Int,Int>>? {
    val height = map.size
    val width = map[0].size

    fun isValid(posX: Int, posY: Int) = posX in 0..< width && posY in 0 ..< height

    var posAndDirection: Triple<Int,Int,Direction>? = Triple(startX, startY, startDirection)
    val visited = mutableListOf<Pair<Int,Int>>()

    try {
        while (posAndDirection != null) {
            val (posX, posY, direction) = posAndDirection

            visited.add(Pair(posX, posY))
            if ((map[posY][posX] as? Free)?.visited?.contains(direction) == true) {
                return null
            }

            val (nextX, nextY) = direction.next(posX, posY)
            posAndDirection = when {
                !isValid(nextX, nextY) -> null
                map[nextY][nextX] == Blocked -> Triple(posX, posY, direction.rotateRight)
                else -> {
                    map[posY][posX] = (map[posY][posX] as Free).visit(direction)
                    Triple(nextX, nextY, direction)
                }
            }
        }
    } finally {
        // reset map
        for((x,y) in visited) {
            map[y][x] = when(val field = map[y][x]) {
                is Free -> Free.field
                else -> field
            }
        }
    }

    return visited
}

fun main() {
    val input = readInputLines("Day06")
    val map = input.map { line ->
        line.map {
            when(it) {
                '#' -> Blocked
                else -> Free.field
            }
        }.toTypedArray()
    }.toTypedArray()

    val (posX,posY,direction) = run {
        input.mapIndexedNotNull { y, line ->
            val x = line.indexOfFirst { Direction.indicators.contains(it) }
            if(x != -1) {
                Triple(x, y, Direction.from(line[x]))
            } else {
                null
            }
        }.first()
    }

    val unobstructed = path(map, posX, posY, direction)
    val fields = unobstructed?.toSet()
    println(fields?.size)

    val loops = fields?.filter{
        it != Pair(posX, posY)
    }?.count { (x,y) ->
        val prevField = map[y][x]
        map[y][x] = Blocked
        val isLoop = path(map, posX, posY, direction) == null
        map[y][x] = prevField
        isLoop
    }
    println(loops)
}
