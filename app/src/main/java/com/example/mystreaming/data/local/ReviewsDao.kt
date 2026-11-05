package com.example.mystreaming.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO da tabela "reviews".
 *
 * Todos os metodos sao `suspend fun`: a chamada tem que acontecer dentro de
 * uma corrotina em Dispatchers.IO para nunca travar a thread principal.
 *
 * As consultas de LISTA devolvem `Flow`: o Room reemite sozinho quando a
 * tabela muda, entao a lista de comentarios de um filme nunca fica atrasada.
 */
@Dao
interface ReviewsDao {

    @Insert
    suspend fun inserir(review: Review): Long

    /** Consulta reativa: a lista se atualiza sozinha a cada mudanca na tabela. */
    @Query("SELECT * FROM reviews ORDER BY id")
    fun buscarTodos(): Flow<List<Review>>

    /** Filtro por filme: todas as avaliacoes de um filme especifico. */
    @Query("SELECT * FROM reviews WHERE filmeId = :filmeId ORDER BY id")
    fun buscarPorFilme(filmeId: Int): Flow<List<Review>>

    /** Consulta filtrada por campo: busca uma avaliacao pelo id. */
    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun buscarPorId(id: Int): Review?

    @Update
    suspend fun atualizar(review: Review)

    @Delete
    suspend fun deletar(review: Review)
}
