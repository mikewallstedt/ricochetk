package goblinshark.ricochet


private val ANSI_RESET = "\u001B[0m"
private val ANSI_BLUE = "\u001B[34m"
private val ANSI_GREEN = "\u001B[32m"
private val ANSI_CYAN = "\u001B[36m"
private val ANSI_RED = "\u001B[31m"
private val ANSI_YELLOW = "\u001B[33m"
private val ANSI_BG_RED = "\u001B[41m"
private val ANSI_BG_GREEN = "\u001B[42m"
private val ANSI_BG_YELLOW = "\u001B[43m"
private val ANSI_BG_BLUE = "\u001B[44m"
private val ANSI_BG_PURPLE = "\u001B[45m"

private val BLACKHOLE = "⛒ "
private val MOON = "☽ "
private val PLANET = "⛞ "
private val STAR = "★ "
private val SUN = "♺ "

private val H_WALL = "═ "
private val H_LINE = "┈ "
private val V_WALL = "║ "
private val V_LINE = "┊ "
private val CELL = "  "
private val INTERSECTION = "  "


fun ShowBoard(state: GameState): String {
    val board = state.board
    val sb: StringBuilder = StringBuilder()
    val numLines = board.wallsPerCol[0].size + board.wallsPerRow.size
    val numChars = board.wallsPerRow[0].size + board.wallsPerCol.size
    for (i in numLines - 1 downTo 0) {
        for (j in 0..numChars - 1) {
            if (i % 2 == 0) {  // Walls in cols
                if (j % 2 == 0) {
                    sb.append(INTERSECTION)
                } else {
                    sb.append(if (board.wallsPerCol[j / 2][i / 2]) H_WALL else H_LINE)
                }
            } else if (i % 2 == 1) {  // Walls in rows
                if (j % 2 == 0) {
                    sb.append(if (board.wallsPerRow[i / 2][j / 2]) V_WALL else V_LINE)
                } else {
                    val c = Coord(j / 2, i / 2)
                    sb.append(ShowCell(state, c))
                }
            }
        }
        sb.append("\n")
    }
    return sb.toString()
}

private fun ShowCell(state: GameState, c: Coord): String {
    return StringBuilder(state.robots[c]?.let { ShowRobot(it) } ?: "")
            .append(state.board.targets[c]?.let { ShowTarget(it) } ?: CELL)
            .append(ANSI_RESET)
            .toString()
}

private fun ShowRobot(robot: Robot): String {
    return when (robot.color) {
        Color.BLUE -> ANSI_BG_BLUE
        Color.GREEN -> ANSI_BG_GREEN
        Color.RED -> ANSI_BG_RED
        Color.YELLOW -> ANSI_BG_YELLOW
        Color.BLACK -> ANSI_BG_PURPLE
        else -> {
            throw IllegalArgumentException()
        }
    }
}

private fun ShowTarget(target: Target): String {
    return StringBuilder(
            when (target.color) {
                Color.BLUE -> ANSI_BLUE
                Color.GREEN -> ANSI_GREEN
                Color.RED -> ANSI_RED
                Color.YELLOW -> ANSI_YELLOW
                Color.WILD -> ANSI_CYAN
                else -> {
                    throw IllegalArgumentException()
                }
            })
    .append(
            when (target.symbol) {
                Symbol.BLACK_HOLE -> BLACKHOLE
                Symbol.MOON -> MOON
                Symbol.PLANET -> PLANET
                Symbol.STAR -> STAR
                Symbol.SUN -> SUN
            })
    .toString()
}