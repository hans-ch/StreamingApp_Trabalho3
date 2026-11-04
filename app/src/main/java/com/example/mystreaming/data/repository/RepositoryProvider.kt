package com.example.mystreaming.data.repository

import android.content.Context
import com.example.mystreaming.data.local.AppDatabase

/**
 * ServiceLocator: entrega as instancias dos repositories.
 *
 * Existe para que a camada `ui` nunca precise conhecer o Room — ela so fala
 * com o Repository. As instancias sao unicas (o banco Room tambem e).
 */
object RepositoryProvider {

    @Volatile
    private var database: AppDatabase? = null

    @Volatile
    private var filmesRepository: FilmesRepository? = null

    @Volatile
    private var reviewsRepository: ReviewsRepository? = null

    @Volatile
    private var seedRepository: SeedRepository? = null

    private fun database(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: AppDatabase.getInstance(context.applicationContext).also { database = it }
        }
    }

    fun filmesRepository(context: Context): FilmesRepository {
        return filmesRepository ?: synchronized(this) {
            filmesRepository ?: FilmesRepository(database(context).filmesDao())
                .also { filmesRepository = it }
        }
    }

    fun reviewsRepository(context: Context): ReviewsRepository {
        return reviewsRepository ?: synchronized(this) {
            reviewsRepository ?: ReviewsRepository(database(context).reviewsDao())
                .also { reviewsRepository = it }
        }
    }

    /** Repository responsavel por popular o banco na primeira execucao. */
    fun seedRepository(context: Context): SeedRepository {
        return seedRepository ?: synchronized(this) {
            seedRepository ?: SeedRepository(
                database(context).filmesDao(),
                database(context).reviewsDao()
            ).also { seedRepository = it }
        }
    }
}
