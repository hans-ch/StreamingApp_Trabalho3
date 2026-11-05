package com.example.mystreaming.viewmodel

import com.example.mystreaming.data.local.Review

/**
 * Tudo o que as telas de avaliacao e comentario precisam para se desenhar,
 * reunido em um lugar so.
 *
 * Este estado e compartilhado pelas telas de Detalhes (bloco de comentarios),
 * Avaliar, Comentarios e Comentario (detalhe).
 */
data class ReviewsUiState(

    // ------------------------------- listas -------------------------------
    /** Avaliacoes do filme em foco (lista reativa do banco). */
    val reviews: List<Review> = emptyList(),

    // --------------------- avaliacao em foco (detalhe) ---------------------
    /** Comentario aberto na tela de Comentario (buscado no banco pelo id). */
    val reviewSelecionado: Review? = null,

    // ------------------------------- contexto ------------------------------
    val filmeId: Int = 0,
    val tituloFilme: String = "",

    // -------------------------------- status ------------------------------
    val carregando: Boolean = true,
    val erro: String? = null,

    // ------------------ formulario de avaliacao (Avaliar) -----------------
    val nota: Int = 3,
    val comentario: String = "",
    val enviando: Boolean = false,

    // ------------------- edicao (tela de Comentario) ----------------------
    val mostrarEdicao: Boolean = false,
    val textoEdicao: String = "",
    val notaEdicao: Int = 3
)
