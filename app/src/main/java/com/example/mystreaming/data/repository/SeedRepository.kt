package com.example.mystreaming.data.repository

import com.example.mystreaming.data.local.FilmesDao
import com.example.mystreaming.data.local.ReviewsDao
import com.example.mystreaming.data.local.filmesDeExemplo
import com.example.mystreaming.data.local.reviewsDeExemplo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Repository responsavel por popular o banco na primeira execucao.
 *
 * Ele precisa dos dois DAOs porque o seed e atomico: as avaliacoes referenciam
 * os ids dos filmes (1, 1 e 2), entao filme e avaliacao precisam ser inseridos
 * juntos e nessa ordem — senao a chave estrangeira quebraria.
 */
class SeedRepository(
    private val filmesDao: FilmesDao,
    private val reviewsDao: ReviewsDao
) {

    private val mutex = Mutex()
    private var semeado = false

    suspend fun popularBancoSeVazio() = mutex.withLock {
        if (semeado) return

        if (filmesDao.buscarTodos().first().isEmpty()) {
            filmesDeExemplo.forEach { filmesDao.inserir(it) }
            reviewsDeExemplo.forEach { reviewsDao.inserir(it) }
        }

        semeado = true
    }
}
