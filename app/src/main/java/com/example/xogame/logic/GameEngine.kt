package com.example.xogame.logic

object GameEngine {

    /**
     * @param board ex. [["X","O","O"],["X","O",""],["O","X",""]]
     * @param winLength = จำนวนตัวที่ต้องเรียงติดกัน
     * @return "X" | "O" | null (player who win, null if draw)
     * **/

    fun checkWinner(board: List<List<String>>, winLength: Int): String? {
        val boardSize = board.size
        if (boardSize == 0) return null

        // check row
        for (rowIndex in 0 until boardSize) {
            val row = board[rowIndex]
            checkLine(row, winLength)?.let { return it }
        }

        // check column
        for (colIndex in 0 until boardSize) {
            val col = MutableList(boardSize) { rowIndex -> board[rowIndex][colIndex] }
            checkLine(col, winLength)?.let { return it }
        }

        // check diagonal down
        for (startCol in 0 until boardSize) {
            val diagonal = collectDiagonalDown(board, 0, startCol)
            checkLine(diagonal, winLength)?.let { return it }
        }
        for (startRow in 1 until boardSize) {
            val diagonal = collectDiagonalDown(board, startRow, 0)
            checkLine(diagonal, winLength)?.let { return it }
        }

        // check diagonal up
        for (startCol in 0 until boardSize) {
            val diagonal = collectDiagonalDown(board, boardSize - 1, startCol)
            checkLine(diagonal, winLength)?.let { return it }
        }
        for (startRow in boardSize - 2 downTo 0) {
            val diagonal = collectDiagonalUp(board, startRow, 0)
            checkLine(diagonal, winLength)?.let { return it }
        }

        return null
    }

    private fun checkLine(line: List<String>, winLength: Int): String? {
        var currentSymbol = "" // X or O
        var currentStreak = 0

        for (cell in line) {
            if (cell.isNotEmpty() && cell == currentSymbol) {
                currentStreak++
            } else {
                currentSymbol = cell
                currentStreak = if (cell.isEmpty()) 0 else 1
            }

            if (currentStreak >= winLength && currentSymbol.isNotEmpty()) {
                return currentSymbol
            }
        }
        return null
    }

    private fun collectDiagonalDown(
        board: List<List<String>>,
        startRow: Int,
        startCol: Int
    ): List<String> {
        val boardSize = board.size
        val diagonal = mutableListOf<String>()
        var row = startRow
        var col = startCol
        while (row in 0 until boardSize && col in 0 until boardSize) {
            diagonal += board[row][col]
            row++
            col++
        }
        return diagonal
    }

    private fun collectDiagonalUp(
        board: List<List<String>>,
        startRow: Int,
        startCol: Int
    ): List<String> {
        val boardSize = board.size
        val diagonal = mutableListOf<String>()
        var row = startRow
        var col = startCol
        while (row in 0 until boardSize && col in 0 until boardSize) {
            diagonal += board[row][col]
            row--
            col++
        }
        return diagonal
    }

}