package goblinshark.ricochet;

enum class Color {
    BLACK, BLUE, GREEN, RED, YELLOW, WILD;
}

data class Coord(val x:Int, val y:Int)

// Assumes coordinate system with origin in lower-left.
enum class Direction(val x:Int, val y:Int) {
    LEFT(-1, 0), RIGHT(1, 0), UP(1, 0), DOWN(-1, 0)
}

enum class Symbol {
    PLANET, MOON, STAR, SUN, BLACK_HOLE
}

data class Robot(val color: Color)

data class Target(val symbol: Symbol, val color: Color)


class EmptyBoard(val wallsPerRow:Array<Array<Boolean>>,
                 val wallsPerCol:Array<Array<Boolean>>,
                 val targets:Map<Coord, Target>) {

    fun rotated(): EmptyBoard {
        return EmptyBoard(rotateColToRow(wallsPerCol), rotateRowToCol(wallsPerRow), rotateTargets())
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


data class GameState(val board:EmptyBoard, val robots:Map<Coord, Robot>) {

    fun isPassable(c: Coord, direction: Direction): Boolean {
        return !(when(direction) {
            Direction.UP -> board.wallsPerCol[c.x][c.y + 1] || robots.contains(Coord(c.x, c.y + 1))
            Direction.DOWN -> board.wallsPerCol[c.x][c.y] || robots.contains(Coord(c.x, c.y - 1))
            Direction.LEFT ->  board.wallsPerRow[c.y][c.x] || robots.contains(Coord(c.x - 1, c.y))
            Direction.RIGHT ->  board.wallsPerRow[c.y][c.x + 1] || robots.contains(Coord(c.x + 1, c.y + 1))
        })
    }
}