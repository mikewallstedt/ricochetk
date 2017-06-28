package goblinshark.ricochet


class Quadrant(val wallsPerRow: Array<Array<Int>>, val wallsPerCol: Array<Array<Int>>,
               val targets: Map<Coord, Target>)

// All predefs are oriented with x=0, y=0 in the south west corner.
// Borders are on the south and west.

val SIMPLE_BLUE_MOON = Quadrant(
        arrayOf(
                arrayOf(0, 4),
                arrayOf(0, 6),
                arrayOf(0),
                arrayOf(0, 5),
                arrayOf(0),
                arrayOf(0, 2),
                arrayOf(0, 5),
                arrayOf(0, 3, 7)),
        arrayOf(
                arrayOf(0, 3),
                arrayOf(0, 5),
                arrayOf(0, 8),
                arrayOf(0),
                arrayOf(0, 4),
                arrayOf(0, 6),
                arrayOf(0, 2),
                arrayOf(0, 7)),
        hashMapOf(
                Coord(2, 7) to Target(Symbol.BLACK_HOLE, Color.WILD),
                Coord(5, 6) to Target(Symbol.PLANET, Color.GREEN),
                Coord(1, 5) to Target(Symbol.MOON, Color.BLUE),
                Coord(4, 3) to Target(Symbol.STAR, Color.RED),
                Coord(6, 1) to Target(Symbol.SUN, Color.YELLOW)
        )
)

val SIMPLE_BLUE_PLANET = Quadrant(
        arrayOf(
                arrayOf(0, 7),
                arrayOf(0, 5),
                arrayOf(0),
                arrayOf(0, 1),
                arrayOf(0, 6),
                arrayOf(0),
                arrayOf(0, 4),
                arrayOf(0, 7)),
        arrayOf(
                arrayOf(0, 2),
                arrayOf(0, 3),
                arrayOf(0),
                arrayOf(0, 7),
                arrayOf(0, 1),
                arrayOf(0),
                arrayOf(0, 5),
                arrayOf(0, 7)),
        hashMapOf(
                Coord(3, 6) to Target(Symbol.STAR, Color.YELLOW),
                Coord(6, 4) to Target(Symbol.PLANET, Color.BLUE),
                Coord(1, 3) to Target(Symbol.SUN, Color.GREEN),
                Coord(4, 1) to Target(Symbol.MOON, Color.RED)
        )
)

val SIMPLE_BLUE_STAR = Quadrant(
        arrayOf(
                arrayOf(0, 6),
                arrayOf(0, 4),
                arrayOf(0),
                arrayOf(0, 7),
                arrayOf(0, 1),
                arrayOf(0, 5),
                arrayOf(0),
                arrayOf(0, 7)),
        arrayOf(
                arrayOf(0, 2),
                arrayOf(0, 5),
                arrayOf(0),
                arrayOf(0, 1),
                arrayOf(0),
                arrayOf(0, 5),
                arrayOf(0, 4),
                arrayOf(0, 7)),
        hashMapOf(
                Coord(5, 5) to Target(Symbol.PLANET, Color.YELLOW),
                Coord(1, 4) to Target(Symbol.MOON, Color.GREEN),
                Coord(6, 3) to Target(Symbol.STAR, Color.BLUE),
                Coord(3, 1) to Target(Symbol.SUN, Color.RED)
        )
)

val SIMPLE_BLUE_SUN = Quadrant(
        arrayOf(
                arrayOf(0, 4),
                arrayOf(0),
                arrayOf(0, 4),
                arrayOf(0, 2),
                arrayOf(0, 6),
                arrayOf(0, 4),
                arrayOf(0),
                arrayOf(0, 7)),
        arrayOf(
                arrayOf(0, 6),
                arrayOf(0),
                arrayOf(0, 3),
                arrayOf(0, 5),
                arrayOf(0, 3),
                arrayOf(0, 5),
                arrayOf(0),
                arrayOf(0, 7)),
        hashMapOf(
                Coord(3, 5) to Target(Symbol.SUN, Color.BLUE),
                Coord(5, 4) to Target(Symbol.STAR, Color.GREEN),
                Coord(2, 3) to Target(Symbol.MOON, Color.YELLOW),
                Coord(4, 2) to Target(Symbol.PLANET, Color.RED)
        )
)

class BoardBuilder(val northWest: Quadrant, val northEast: Quadrant,
                   val southWest: Quadrant, val southEast: Quadrant) {

    fun Build(): EmptyBoard {
        return appendVert(
                appendHoriz(convert(southWest), convert(southEast)),
                appendHoriz(convert(northEast), convert(northWest))
        )
    }

    private fun convert(q: Quadrant): EmptyBoard {
        return EmptyBoard(convert(q.wallsPerRow), convert(q.wallsPerCol), q.targets)
    }

    private fun convert(walls: Array<Array<Int>>): Array<Array<Boolean>> {
        val array = Array(walls.size, { i -> Array(walls.size + 1, { j -> walls[i].contains(j) }) })
        return array
    }

    private fun appendHoriz(west: EmptyBoard, east: EmptyBoard): EmptyBoard {
        val eastRot = east.rotated().rotated().rotated()
        val numVCells = west.wallsPerRow.size
        val numHWalls = west.wallsPerRow[0].size
        val newWallsPerRow = Array(
                numVCells,
                { y -> Array(
                        2 * numHWalls - 1,
                        { x ->
                            // Walls overlap at the joint.
                            if (x < numHWalls - 1)
                                west.wallsPerRow[y][x]
                            else if (x == numHWalls - 1)
                                west.wallsPerRow[y][x] || eastRot.wallsPerRow[y][0]
                            else
                                eastRot.wallsPerRow[y][x - (numHWalls - 1)]
                        })
                })
        val numHCells = west.wallsPerCol.size
        val numVWalls = west.wallsPerCol[0].size
        val newWallsPerCol = Array(
                2 * numHCells,
                { x -> Array(
                        numVWalls,
                        { y ->
                            if (x < numHCells)
                                west.wallsPerCol[x][y]
                            else
                                eastRot.wallsPerCol[x - numHCells][y]
                        })
                })

        val newTargets = west.targets.plus(eastRot.targets.mapKeys { e -> Coord(e.key.x + numHCells, e.key.y) })
        return EmptyBoard(newWallsPerRow, newWallsPerCol, newTargets)
    }

    private fun appendVert(south: EmptyBoard, north: EmptyBoard): EmptyBoard {
        val northRot = north.rotated().rotated()
        val numVCells = south.wallsPerRow.size
        val numHWalls = south.wallsPerRow[0].size
        val newWallsPerRow = Array(
                2 * numVCells,
                { y -> Array(
                        numHWalls,
                        { x ->
                            if (y < numVCells)
                                south.wallsPerRow[y][x]
                            else
                                northRot.wallsPerRow[y - numVCells][x]
                        })
                })
        val numHCells = south.wallsPerCol.size
        val numVWalls = south.wallsPerCol[0].size
        val newWallsPerCol = Array(
                numHCells,
                { x -> Array(
                        2 * numVWalls - 1,
                        { y ->
                            if (y < numVWalls - 1)
                                south.wallsPerCol[x][y]
                            else if (y == numVWalls - 1)
                                south.wallsPerCol[x][y] || northRot.wallsPerCol[x][0]
                            else
                                northRot.wallsPerCol[x][y  - (numVWalls - 1)]
                        })
                })

        val newTargets = south.targets.plus(northRot.targets.mapKeys { e -> Coord(e.key.x, e.key.y + numVCells) })
        return EmptyBoard(newWallsPerRow, newWallsPerCol, newTargets)
    }
}

fun main(args: Array<String>) {
    val b: EmptyBoard = BoardBuilder(SIMPLE_BLUE_MOON, SIMPLE_BLUE_PLANET, SIMPLE_BLUE_STAR, SIMPLE_BLUE_SUN).Build()
    print(ShowBoard(b))
}