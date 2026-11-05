package com.example.mystreaming.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO da tabela "filmes".
 *
 * Todos os metodos sao `suspend fun`: a chamada tem que acontecer dentro de
 * uma corrotina em Dispatchers.IO para nunca travar a thread principal.
 *
 * As consultas de LISTA devolvem `Flow`: o Room reemite sozinho sempre que a
 * tabela muda (inserir, atualizar ou deletar), sem precisar buscar de novo.
 */
@Dao
interface FilmesDao {

    @Insert
    suspend fun inserir(filme: MediaItem): Long

    /** Consulta reativa: a lista se atualiza sozinha a cada mudanca na tabela. */
    @Query("SELECT * FROM filmes ORDER BY id")
    fun buscarTodos(): Flow<List<MediaItem>>

    /** Filtro da "Sua lista": apenas os filmes marcados pelo usuario. */
    @Query("SELECT * FROM filmes WHERE naMinhaLista = 1 ORDER BY id")
    fun buscarDaMinhaLista(): Flow<List<MediaItem>>

    /** Consulta filtrada por campo: busca um filme pelo id (usada na Tela de Detalhes). */
    @Query("SELECT * FROM filmes WHERE id = :id")
    suspend fun buscarPorId(id: Int): MediaItem?

    @Update
    suspend fun atualizar(filme: MediaItem)

    @Delete
    suspend fun deletar(filme: MediaItem)
}
