package com.example.xogame.data.db

import com.example.xogame.data.entity.GameSession
import com.example.xogame.data.entity.Move
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GameRepository(private val db: AppDatabase) {

    fun allSession(): Flow<List<GameSession>> = db.sessionDao().getAllSession()
    fun session(sessionId: Long): Flow<GameSession?> = db.sessionDao().getSession(sessionId)
    fun moves(sessionId: Long): Flow<List<Move>> = db.moveDao().getMoveForSession(sessionId)

    suspend fun createSession(boardSize: Int, winLength: Int): Long = withContext(Dispatchers.IO) {
        db.sessionDao().insertSession(
            GameSession(
                boardSize = boardSize,
                winLength = winLength,
                startedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addMove(sessionId: Long, moveNumber: Int, row: Int, col: Int, player: String) =
        withContext(Dispatchers.IO) {
            db.moveDao().insertMove(
                Move(
                    sessionId = sessionId,
                    moveNumber = moveNumber,
                    row = row,
                    col = col,
                    player = player,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

    suspend fun findSession(sessionId: Long, winner: String?) = withContext(Dispatchers.IO) {
        val session = db.sessionDao().getSession(sessionId).first() ?: return@withContext
        val ended = System.currentTimeMillis()
        db.sessionDao().updateSession(
            session.copy(
                endedAt = ended,
                durationMs = ended - session.startedAt,
                winner = winner ?: "Draw"
            )
        )
    }


}