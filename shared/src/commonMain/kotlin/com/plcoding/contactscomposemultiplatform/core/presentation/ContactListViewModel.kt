package com.plcoding.contactscomposemultiplatform.core.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.plcoding.contactscomposemultiplatform.contacts.domain.Contact
import com.plcoding.contactscomposemultiplatform.contacts.domain.ContactDataSource
import com.plcoding.contactscomposemultiplatform.contacts.domain.ContactValidator
import com.plcoding.contactscomposemultiplatform.contacts.presentation.ContactListEvent
import com.plcoding.contactscomposemultiplatform.contacts.presentation.ContactListState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactListViewModel(
    private val contactDataSource: ContactDataSource
) : ViewModel() {
    private val _state = MutableStateFlow(
        ContactListState(
            contacts = contacts
        )
    )
    val state = combine(
        _state,
        contactDataSource.getContacts(),
        contactDataSource.getRecentsContacts(20)
    ) { state, contacts, recentContacts ->
        state.copy(
            contacts = contacts,
            recentlyAddedContacts = recentContacts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ContactListState())

    var newContact: Contact? by mutableStateOf(null)
        private set

    fun onEvent(event: ContactListEvent) {
        when (event) {
            ContactListEvent.DeleteContact -> {
                viewModelScope.launch {
                    _state.value.selectedContact?.id?.let { id ->
                        _state.update { it.copy(isSelectedContactSheetOpen = false) }
                        contactDataSource.deleteContact(id = id)
                        delay(300L)
                        _state.update {
                            it.copy(selectedContact = null)
                        }
                    }

                }
            }

            ContactListEvent.DismissContact -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isSelectedContactSheetOpen = false,
                            isAddContactSheetOpen = false,
                            firstNameError = null,
                            lastNameError = null,
                            emailError = null,
                            phoneNumberError = null,
                        )
                    }
                    delay(300L)
                    newContact = null
                    _state.update {
                        it.copy(
                            selectedContact = null
                        )
                    }
                }
            }

            is ContactListEvent.EditContact -> {
                _state.update {
                    it.copy(
                        selectedContact = null,
                        isAddContactSheetOpen = true,
                        isSelectedContactSheetOpen = false
                    )
                }
                newContact = event.contact
            }

            ContactListEvent.OnAddNewContactClick -> {
                _state.update {
                    it.copy(
                        isAddContactSheetOpen = true
                    )
                }
                newContact = Contact(
                    null,
                    "",
                    "",
                    "",
                    "",
                    null
                )
            }

//            ContactListEvent.OnAddPhotoClicked -> {
//                newContact = newContact?.copy(
//                    photoBytes = event.
//                )
//            }

            is ContactListEvent.OnEmailChanged -> {
                newContact = newContact?.copy(
                    email = event.value
                )
            }

            is ContactListEvent.OnFirstNameChanged -> {
                newContact = newContact?.copy(
                    firstName = event.value
                )
            }

            is ContactListEvent.OnLastNameChanged -> {
                newContact = newContact?.copy(
                    lastName = event.value
                )
            }

            is ContactListEvent.OnPhoneNumberChanged -> {
                newContact = newContact?.copy(
                    phoneNumber = event.value
                )
            }

            is ContactListEvent.OnPhotoChanged -> {
                newContact = newContact?.copy(
                    photoBytes = event.bytes
                )
            }

            ContactListEvent.SaveContact -> {
                newContact?.let { contact ->
                    val result = ContactValidator.validateContact(contact)
                    val errors = listOfNotNull(
                        result.firstNameError,
                        result.lastNameError,
                        result.emailError,
                        result.phoneNumberError,
                    )

                    if (errors.isEmpty()) {
                        _state.update {
                            it.copy(
                                isAddContactSheetOpen = false,
                                firstNameError = null,
                                lastNameError = null,
                                emailError = null,
                                phoneNumberError = null,
                            )
                        }
                        viewModelScope.launch {
                            contactDataSource.insertContact(contact)
                            delay(300L)
                            newContact = null
                        }
                    } else {
                        _state.update { it.copy(
                            firstNameError = result.firstNameError,
                            lastNameError = result.lastNameError,
                            phoneNumberError = result.phoneNumberError,
                            emailError = result.emailError,
                        ) }
                    }
                }
            }

            is ContactListEvent.SelectContact -> {
                _state.update {
                    it.copy(
                        selectedContact = event.contact,
                        isSelectedContactSheetOpen = true
                    )
                }
            }

            else -> Unit
        }
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