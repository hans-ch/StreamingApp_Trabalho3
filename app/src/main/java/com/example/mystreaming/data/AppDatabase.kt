package com.example.mystreaming.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

/**
 * Banco de dados do app (Room sobre SQLite).
 *
 * Une as duas entidades do projeto e expoe os DAOs. Segue o padrao Singleton
 * (companion object + @Volatile + synchronized) para que a instancia do banco
 * seja unica em todo o app.
 */
@Database(entities = [MediaItem::class, Review::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun filmesDao(): FilmesDao

    abstract fun reviewsDao(): ReviewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mystreaming.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
