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
    suspend fun deleteMoveFrrSession(sessionId: Long)

}