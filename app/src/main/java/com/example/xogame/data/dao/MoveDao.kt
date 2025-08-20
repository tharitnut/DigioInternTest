package com.example.xogame.data.dao

import androidx.room.Dao
import androidx.room.*
import com.example.xogame.data.entity.Move
import kotlinx.coroutines.flow.Flow

@Dao
interface MoveDao {

    @Insert
    suspend fun insertMove(move: Move)

    @Query("select * from Move where sessionId = :sessionId order by moveNumber asc")
    fun getMoveForSession(sessionId: Long): Flow<List<Move>>

    @Query("delete from Move where sessionId = :sessionId")
    suspend fun deleteMoveForSession(sessionId: Long)

    @Query("DELETE FROM Move")
    suspend fun deleteAllMoves()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'Move'")
    suspend fun resetMoveIdSequence()

    @Query("DELETE FROM Move WHERE sessionId = :sessionId")
    suspend fun deleteMovesForSession(sessionId: Long)

}