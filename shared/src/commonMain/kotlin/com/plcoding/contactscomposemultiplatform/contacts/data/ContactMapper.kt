package com.plcoding.contactscomposemultiplatform.contacts.data

import com.plcoding.contactscomposemultiplatform.contacts.domain.Contact
import database.ContactEntity

fun  ContactEntity.toContact(): Contact {
    return Contact(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        photoBytes = null // TODO: Get the image
    )
}