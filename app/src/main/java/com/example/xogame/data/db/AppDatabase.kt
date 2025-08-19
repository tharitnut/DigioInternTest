package com.example.xogame.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.xogame.data.dao.GameSessionDao
import com.example.xogame.data.dao.MoveDao
import com.example.xogame.data.entity.GameSession
import com.example.xogame.data.entity.Move

@Database(entities = [GameSession::class, Move::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): GameSessionDao
    abstract fun moveDao(): MoveDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xo.db"
                ).build().also { INSTANCE = it }
            }
    }
}