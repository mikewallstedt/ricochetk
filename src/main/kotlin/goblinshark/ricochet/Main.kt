package goblinshark.ricochet

fun main(args: Array<String>) {
    val b: EmptyBoard = BoardBuilder(SIMPLE_BLUE_MOON, SIMPLE_BLUE_PLANET, SIMPLE_BLUE_STAR, SIMPLE_BLUE_SUN).Build()
    val robot = hashMapOf<Coord, Robot>(
        Coord(13, 3) to Robot(Color.BLUE),
        Coord(3, 3) to Robot(Color.RED),
        Coord(13, 8) to Robot(Color.GREEN),
        Coord(5, 14) to Robot(Color.YELLOW),
        Coord(1, 14) to Robot(Color.BLACK)
    )
    val state = GameState(b, robot)

    print(ShowBoard(state))

    for (c in state.board.targets.keys) {
//        if (Target(Symbol.STAR, Color.GREEN) != state.board.targets[c]) {
//            continue
//        }
        val start = System.currentTimeMillis()
        println(state.board.targets[c])
        val solver = BreadFirstSolver(12)
        val solution = solver.Solve(state, c)
        solution?.forEach { println("  " + it) }
        println(String.format("%dms", System.currentTimeMillis() - start))
        println("")
    }
}
