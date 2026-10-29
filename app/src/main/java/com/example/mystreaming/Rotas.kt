package com.example.mystreaming

object Rotas {
    const val HOME     = "home"
    const val DESCRICAO = "descricao/{filmeId}"
    const val AVALIACAO = "avaliacao/{filmeId}"
    const val MINHA_LISTA_GRID = "grid"
    const val COMENTARIOS = "comentarios/{filmeId}"
    const val COMENTARIO_DETALHES = "comentario_detalhes/{reviewId}"
    const val PERFIL    = "perfil"

    // Rotas com argumento montado: a navegacao carrega apenas o id,
    // e a tela busca o objeto no banco Room.
    fun descricao(filmeId: Int) = "descricao/$filmeId"
    fun avaliacao(filmeId: Int) = "avaliacao/$filmeId"
    fun comentarios(filmeId: Int) = "comentarios/$filmeId"
    fun comentarioDetalhes(reviewId: Int) = "comentario_detalhes/$reviewId"
}
