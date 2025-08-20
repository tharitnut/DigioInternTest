package com.example.xogame.data.dao

import androidx.room.Dao
import androidx.room.*
import com.example.xogame.data.entity.GameSession
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {

    @Insert
    suspend fun insertSession(session: GameSession): Long

    @Update
    suspend fun updateSession(session: GameSession)

    @Query("select * from GameSession order by startedAt desc")
    fun getAllSession(): Flow<List<GameSession>>

    @Query("select * from GameSession where id = :id limit 1")
    fun getSession(id: Long): Flow<GameSession?>

    @Query("DELETE FROM GameSession")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'GameSession'")
    suspend fun resetSessionIdSequence()

    @Query("DELETE FROM GameSession WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

}