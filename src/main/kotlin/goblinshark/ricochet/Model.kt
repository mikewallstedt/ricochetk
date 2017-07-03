package goblinshark.ricochet;

enum class Color {
    BLACK, BLUE, GREEN, RED, YELLOW, WILD;
}

data class Coord(val x:Int, val y:Int) {
    fun move(d: Direction): Coord {
        return Coord(x + d.x, y + d.y)
    }

    // Performance hack.
    // Assumes board is no larger than 16 x 16
    override fun hashCode(): Int {
        return x shl 4 or y
    }
}

// Assumes coordinate system with origin in lower-left.
enum class Direction(val x:Int, val y:Int) {
    LEFT(-1, 0), RIGHT(1, 0), UP(0, 1), DOWN(0, -1)
}

data class Move(val robot:Robot, val direction: Direction, val numCells:Int)

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

    var moves: List<Move> = ArrayList()

    constructor(board: EmptyBoard, robots: Map<Coord, Robot>, moves: List<Move>):  this(board, robots) {
        this.moves = moves
    }

    fun isPassable(c: Coord, direction: Direction): Boolean {
        return !(when(direction) {
            Direction.UP -> board.wallsPerCol[c.x][c.y + 1] || robots.contains(Coord(c.x, c.y + 1))
            Direction.DOWN -> board.wallsPerCol[c.x][c.y] || robots.contains(Coord(c.x, c.y - 1))
            Direction.LEFT ->  board.wallsPerRow[c.y][c.x] || robots.contains(Coord(c.x - 1, c.y))
            Direction.RIGHT ->  board.wallsPerRow[c.y][c.x + 1] || robots.contains(Coord(c.x + 1, c.y))
        })
    }

    // Performance hack.
    // Assumes board is no larger than 16 x 16
    // Assumes only compared to states sharing the same EmptyBoard.
    override fun hashCode(): Int {
        return encode().hashCode()
    }

    fun encode():Long {
        var result = 0L
        robots.forEach({e -> result = result or encode(e)})
        return result
    }

    fun encode(e:Map.Entry<Coord, Robot>): Long {
        return when (e.value.color) {
            Color.BLACK -> encode(0, e.key)
            Color.BLUE -> encode(1, e.key)
            Color.GREEN -> encode(2, e.key)
            Color.RED -> encode(3, e.key)
            Color.YELLOW -> encode(4, e.key)
            else -> throw IllegalStateException()
        }
    }

    fun encode(offset:Int, c:Coord):Long {
        return (c.x.toLong() shl (offset * 8)) or (c.y.toLong() shl (offset * 8 + 4))
    }
}

fun main(args: Array<String>) {
    val b: EmptyBoard = BoardBuilder(SIMPLE_BLUE_MOON, SIMPLE_BLUE_PLANET, SIMPLE_BLUE_STAR, SIMPLE_BLUE_SUN).Build()
    val robot = hashMapOf<Coord, Robot>(
            Coord(13, 3) to Robot(Color.BLUE),
            Coord(3, 3) to Robot(Color.RED),
            Coord(13, 8) to Robot(Color.GREEN),
            Coord(5, 14) to Robot(Color.YELLOW),
            Coord(1, 14) to Robot(Color.BLACK)
    )

    val s1 = GameState(b, robot)
    val r2 = HashMap(robot)
    r2.remove(Coord(1, 14))
    val s2 = GameState(b, r2)

    println(s1.encode())
    println(s2.encode())
}