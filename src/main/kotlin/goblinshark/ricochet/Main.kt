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
    
}