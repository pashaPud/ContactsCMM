package com.plcoding.contactscomposemultiplatform.core.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.plcoding.contactscomposemultiplatform.contacts.domain.Contact
import com.plcoding.contactscomposemultiplatform.contacts.presentation.ContactListEvent
import com.plcoding.contactscomposemultiplatform.contacts.presentation.ContactListState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactListViewModel : ViewModel() {
    private val _state = MutableStateFlow(ContactListState(
        contacts = contacts
    ))
    val state = _state.asStateFlow()

    var newContact: Contact? by mutableStateOf(null)
        private set

    fun onEvent(event: ContactListEvent) {

    }
}

private val contacts = (1..50).map {
    Contact(
        id = it.toLong(),
        firstName = "First $it",
        lastName = "Last $it",
        email = "test@test$it.com",
        phoneNumber = "123456789",
        photoBytes = null
    )
}