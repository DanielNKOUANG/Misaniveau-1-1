package com.example.misaniveau.ui.screen3

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.misaniveau.R
import kotlinx.coroutines.flow.MutableStateFlow

data class Element(
    val title: String,
    val subtitle: String,
    @DrawableRes val image: Int
)

class Screen3ViewModel: ViewModel() {
    // Liste de String
    val listFlow = MutableStateFlow(listOf<Element>())

    // Ajouter un élément
    fun addElement(element: String) {
        listFlow.value += Element(
            title = element,
            "sous titre",
            R.drawable.download
        )
    }

    // Supprimer le dernier élément
    fun removeElement() {
        if(listFlow.value.isNotEmpty()){
            listFlow.value= listFlow.value.dropLast(1)
        }
    }

    fun clearList() {
        listFlow.value = emptyList()
    }
}