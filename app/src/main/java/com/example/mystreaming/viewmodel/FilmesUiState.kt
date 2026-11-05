package com.example.mystreaming.viewmodel

import com.example.mystreaming.data.local.MediaItem

/**
 * Tudo o que as telas de filme precisam para se desenhar, reunido em um lugar
 * so — no lugar de varias `var ... by remember` soltas pela interface.
 *
 * Este estado e compartilhado pelas telas de Home, Minha Lista, Detalhes e
 * Perfil (todas usam o mesmo ViewModel).
 */
data class FilmesUiState(

    // ------------------------------- listas -------------------------------
    val filmes: List<MediaItem> = emptyList(),
    val minhaLista: List<MediaItem> = emptyList(),

    // --------------------------- filme em foco ---------------------------
    /** Filme aberto na tela de Detalhes (buscado no banco pelo id da rota). */
    val filmeSelecionado: MediaItem? = null,

    // -------------------------------- status ------------------------------
    val carregando: Boolean = true,
    val erro: String? = null,

    // ------------------- formulario de adicionar (Home) -------------------
    val mostrarFormulario: Boolean = false,
    val titulo: String = "",
    val ano: String = "",
    val nota: String = "",

    // ------------------- edicao e exclusao (Detalhes) ---------------------
    val mostrarEdicao: Boolean = false,
    val tituloEdicao: String = "",
    val anoEdicao: String = "",
    val notaEdicao: String = "",
    val mostrarExclusao: Boolean = false
)
