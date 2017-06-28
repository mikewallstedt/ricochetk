package goblinshark.ricochet


private val ANSI_RESET = "\u001B[0m"
private val ANSI_BLUE = "\u001B[34m"
private val ANSI_GREEN = "\u001B[32m"
private val ANSI_PURPLE = "\u001B[35m"
private val ANSI_RED = "\u001B[31m"
private val ANSI_YELLOW = "\u001B[33m"


private val BLACKHOLE = "⛒ "
private val MOON ="☽ "
private val PLANET ="⛞ "
private val STAR = "★ "
private val SUN = "♺ "


private val H_WALL = "═ "
private val H_LINE = "┈ "
private val V_WALL = "║ "
private val V_LINE = "┊ "
private val CELL = "  "
private val INTERSECTION = "  "


fun ShowBoard(board:EmptyBoard):String {
    val sb:StringBuilder = StringBuilder()
    val numLines = board.wallsPerCol[0].size + board.wallsPerRow.size
    val numChars = board.wallsPerRow[0].size + board.wallsPerCol.size
    for (i in numLines - 1 downTo 0) {
        for (j in 0..numChars - 1) {
            if (i % 2 == 0) {  // Walls in cols
                if (j % 2 == 0) {
                    sb.append(INTERSECTION)
                } else {
                    sb.append(if (board.wallsPerCol[j/2][i/2]) H_WALL else H_LINE)
                }
            } else if (i % 2 == 1){  // Walls in rows
                if (j % 2 == 0) {
                    sb.append(if (board.wallsPerRow[i/2][j/2]) V_WALL else V_LINE)
                } else {
                    val c = Coord(j/2, i/2)
                    sb.append(if(board.targets.containsKey(c)) ShowTarget(board.targets[c] as Target) else CELL)
                }
            }
        }
        sb.append("\n")
    }
    return sb.toString()
}

private fun ShowTarget(target:Target):String {
    val sb = StringBuilder(
        when (target.color) {
            Color.BLUE -> ANSI_BLUE
            Color.GREEN -> ANSI_GREEN
            Color.RED -> ANSI_RED
            Color.YELLOW -> ANSI_YELLOW
            Color.WILD -> ANSI_PURPLE
        })
    sb.append(
        when(target.symbol) {
            Symbol.BLACK_HOLE -> BLACKHOLE
            Symbol.MOON -> MOON
            Symbol.PLANET -> PLANET
            Symbol.STAR -> STAR
            Symbol.SUN -> SUN
        }
    )
    sb.append(ANSI_RESET)
    return sb.toString()
}
