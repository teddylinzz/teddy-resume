package com.teddy.teddyresume.ui.mainmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val resume = mutableStateListOf<Resume.Information>()
    fun updateResume(information: List<Resume.Information>) {
        resume.addAll(information)
    }
}