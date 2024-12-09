const val Empty = -1

sealed interface SpaceType {
    fun checksum(startIx: Int, size: Int): Long
}
data object EmptySpace: SpaceType {
    override fun checksum(startIx: Int, size: Int): Long = 0L
}
data class FileType(val fileId: Int): SpaceType {
    override fun checksum(startIx: Int, size: Int): Long =
        fileId.toLong() * ((size*size + size)/2 + size*(startIx-1))
}

fun main() {
    val input = readInput("Day09").map { char -> char.digitToInt() }

    // part 1
    val blocks = sequence {
        var fileId = 0
        var isFile = true
        for(num in input) {
            val toWrite = if(isFile) fileId else Empty
            for(i in 0 ..< num) {
                yield(toWrite)
            }
            if(isFile) fileId += 1
            isFile = !isFile
        }
    }.toList()

    fun reorderInplace(blocks: IntArray): IntArray {
        var startIx = 0
        var endIx = blocks.size-1
        while(startIx < endIx) {
            if(blocks[startIx] != Empty) {
                startIx += 1
            } else if(blocks[endIx] == Empty) {
                endIx -= 1
            } else {
                blocks[startIx] = blocks[endIx]
                blocks[endIx] = Empty
                startIx += 1
                endIx -= 1
            }
        }
        return blocks
    }

    val checksum = reorderInplace(blocks.toIntArray()).mapIndexed { index, i ->
        if(i == Empty) 0 else index * i.toLong()
    }.sum()
    println(checksum)

    // part 2
    val spaceRepresentation = sequence {
        var fileId = 0
        var isFile = true
        for(num in input) {
            val toWrite = if(isFile) FileType(fileId) else EmptySpace
            yield(Pair(toWrite, num))
            if(isFile) fileId += 1
            isFile = !isFile
        }
        input.toIntArray()
    }.toMutableList()

    fun mergeSpace(blocks: MutableList<Pair<SpaceType,Int>>, around: Int) {
        val size = blocks[around-1].second +
                blocks[around].second +
                (blocks.getOrNull(around+1)?.second ?: 0)
        blocks.removeAt(around-1)
        blocks.removeAt(around-1)
        if(blocks.size > around-1) blocks.removeAt(around-1)
        blocks.add(around-1, Pair(EmptySpace, size))
    }

    fun reorderInplace(
        blocks: MutableList<Pair<SpaceType,Int>>
    ): MutableList<Pair<SpaceType,Int>> {
        var endIx = blocks.size-1
        while(endIx > 0) {
            if(blocks[endIx].first == EmptySpace) {
                endIx -= 1
            } else {
                var startIx = 0
                while(startIx < endIx) {
                    if(blocks[startIx].first != EmptySpace ||
                        blocks[startIx].second < blocks[endIx].second) {
                        startIx += 1
                    } else {
                        val remainingSpace = blocks[startIx].second - blocks[endIx].second
                        blocks[startIx] = Pair(EmptySpace, 0)
                        blocks.add(startIx+1, blocks[endIx])
                        blocks.add(startIx+2, Pair(EmptySpace, remainingSpace))
                        mergeSpace(blocks, endIx+2)
                        endIx += 1 // increase due to inserted indices before
                        break
                    }
                }
                endIx -= 1
            }
        }
        return blocks
    }

    reorderInplace(spaceRepresentation)
    println(spaceRepresentation.fold(Pair(0L,0)) { (sum,ix), (type,size) ->
        Pair(sum + type.checksum(ix, size), ix+size)
    }.first)
}
