package com.example.mystreaming.data.repository

import com.example.mystreaming.data.local.FilmesDao
import com.example.mystreaming.data.local.MediaItem
import kotlinx.coroutines.flow.Flow

/**
 * Unica porta de entrada para a tabela "filmes".
 *
 * Quem fala com o DAO e apenas o Repository; quem fala com o Repository e
 * apenas o ViewModel. Se um dia os dados vierem de outro lugar (uma API, por
 * exemplo), so este arquivo muda.
 */
class FilmesRepository(private val dao: FilmesDao) {

    /** Lista reativa de todos os filmes do banco. */
    val filmes: Flow<List<MediaItem>> = dao.buscarTodos()

    /** Lista reativa dos filmes salvos na "Sua lista". */
    val minhaLista: Flow<List<MediaItem>> = dao.buscarDaMinhaLista()

    suspend fun buscarPorId(id: Int): MediaItem? = dao.buscarPorId(id)

    suspend fun inserir(filme: MediaItem): Long = dao.inserir(filme)

    suspend fun atualizar(filme: MediaItem) = dao.atualizar(filme)

    suspend fun deletar(filme: MediaItem) = dao.deletar(filme)
}
