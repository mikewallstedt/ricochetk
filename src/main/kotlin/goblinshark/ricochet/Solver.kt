package goblinshark.ricochet

import java.util.*


class BreadFirstSolver(val maxDepth:Int) {

    fun Solve(state:GameState, targetCoord:Coord):List<Move>? {
        val target:Target = state.board.targets[targetCoord] ?: throw IllegalArgumentException()
        val solvableColors:Set<Color> =
            when (target.color) {
                Color.WILD -> hashSetOf(Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
                else -> {
                    hashSetOf(target.color)
                }
            }
        val seenStates:MutableSet<GameState> = HashSet()
        val stateStack:MutableList<GameState> = LinkedList()

        stateStack.add(state)
        while (!stateStack.isEmpty()) {
            val nextState = stateStack.removeAt(0)
            if (seenStates.contains(nextState) || nextState.moves.size > maxDepth) {
                continue
            }
            if (solvableColors.contains(nextState.robots[targetCoord]?.color)) {
                return nextState.moves
            }
            seenStates.add(nextState)
            for (roboCoord in nextState.robots.keys) {
                for (direction in Direction.values()) {
                    var numMoves = 0
                    var c = roboCoord
                    while (nextState.isPassable(c, direction)) {
                        numMoves++
                        c = c.move(direction)
                    }
                    if (numMoves > 0) {
                        val roboMap = HashMap(nextState.robots)
                        val robot = roboMap[roboCoord] ?: throw IllegalStateException()
                        roboMap.remove(roboCoord)
                        roboMap[c] = robot
                        val newMoves = ArrayList(nextState.moves)
                        newMoves.add(Move(robot, direction, numMoves))
                        stateStack.add(GameState(nextState.board, roboMap, newMoves))
                    }
                }
            }
        }
        return null
    }
}
