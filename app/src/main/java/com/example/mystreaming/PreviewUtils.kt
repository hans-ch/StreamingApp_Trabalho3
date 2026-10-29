package com.example.mystreaming

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Aplicacao real usada apenas nos @Preview.
 *
 * O Preview nao tem Activity, por isso pegamos o Application do Context local
 * para poder criar o MainViewModel (que precisa do Context para abrir o banco).
 */
@Composable
fun previewApplication(): Application =
    LocalContext.current.applicationContext as Application
