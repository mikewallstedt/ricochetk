package goblinshark.ricochet;

enum class Color {
    BLUE, GREEN, RED, YELLOW, WILD;
}

data class Coord(val x:Int, val y:Int)

// Assumes coordinate system with origin in lower-left.
enum class Direction(val x:Int, val y:Int) {
    LEFT(-1, 0), RIGHT(1, 0), UP(1, 0), DOWN(-1, 0)
}

enum class Symbol {
    PLANET, MOON, STAR, SUN, BLACK_HOLE
}

data class Target(val symbol: Symbol, val color: Color)


class EmptyBoard(val wallsPerRow:Array<Array<Boolean>>,
                 val wallsPerCol:Array<Array<Boolean>>,
                 val targets:Map<Coord, Target>) {

    fun isPassable(coord: Coord, direction: Direction): Boolean {
        when(direction) {
            Direction.UP -> return wallsPerCol[coord.x][coord.y + 1]
            Direction.DOWN -> return wallsPerCol[coord.x][coord.y]
            Direction.LEFT -> return wallsPerRow[coord.y][coord.x]
            Direction.RIGHT -> return wallsPerRow[coord.y][coord.x + 1]
        }
    }

    fun rotated(): EmptyBoard {
        return EmptyBoard(rotateColToRow(wallsPerCol), rotateRowToCol(wallsPerRow), rotateTargets())
    }

    override fun toString(): String {
        val hWallStr = "═"
        val hLineStr = "┈"
        val vWallStr = "║"
        val vLineStr = "┊"
        val targetStr = "@"
        val cellStr = " "
        val intersectionStr = " "
        val sb:StringBuilder = StringBuilder()
        val numLines = wallsPerCol[0].size + wallsPerRow.size
        val numChars = wallsPerRow[0].size + wallsPerCol.size
        for (i in numLines - 1 downTo 0) {
            for (j in 0..numChars - 1) {
                if (i % 2 == 0) {  // Walls in cols
                    if (j % 2 == 0) {
                        sb.append(intersectionStr)
                    } else {
                        sb.append(if (wallsPerCol[j/2][i/2]) hWallStr else hLineStr)
                    }
                } else if (i % 2 == 1){  // Walls in rows
                    if (j % 2 == 0) {
                        sb.append(if (wallsPerRow[i/2][j/2]) vWallStr else vLineStr)
                    } else {
                        sb.append(if(targets.containsKey(Coord(j/2, i/2))) targetStr else cellStr)
                    }
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    private fun rotateColToRow(input: Array<Array<Boolean>>): Array<Array<Boolean>> {
        // Need to initialize the whole array before setting anything, to be sure we stay in bounds.
        val result = Array(input.size, { Array(input[0].size, { false }) })

        for(i in 0..result.size - 1) {
            for(j in 0..result[i].size - 1) {
                result[input.size - 1 - i][j] = input[i][j]
            }
        }
        return result
    }

    private fun rotateRowToCol(input: Array<Array<Boolean>>): Array<Array<Boolean>> {
        // Need to initialize the whole array before setting anything, to be sure we stay in bounds.
        val result = Array(input.size, { Array(input[0].size, { false }) })

        for(i in 0..result.size - 1) {
            for(j in 0..result[i].size - 1) {
                result[i][result[i].size - 1 - j] = input[i][j]
            }
        }
        return result
    }

    private fun rotateTargets(): Map<Coord, Target> {
        // Use wallsPerCol size, since that will be the height after the rotation.
        return targets.mapKeys { e -> Coord(e.key.y, wallsPerCol.size - 1 - e.key.x) }
    }

}