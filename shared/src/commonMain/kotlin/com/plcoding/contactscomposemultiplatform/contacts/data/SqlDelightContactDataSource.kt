package com.plcoding.contactscomposemultiplatform.contacts.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.plcoding.contactscomposemultiplatform.contacts.domain.Contact
import com.plcoding.contactscomposemultiplatform.contacts.domain.ContactDataSource
import com.plcoding.contactscomposemultiplatform.database.ContactDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.coroutines.CoroutineContext

class SqlDelightContactDataSource(
    db: ContactDatabase
): ContactDataSource {

    private val queries = db.contactQueries
    override fun getContacts(): Flow<List<Contact>> {
        return queries
            .getContacts()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { contactEntities ->
                contactEntities.map { contactEntity ->
                    contactEntity.toContact()
                }
            }
    }

    override fun getRecentsContacts(amount: Int): Flow<List<Contact>> {
        return queries
            .getRecentContacts(amount.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { contactEntities ->
                contactEntities.map { contactEntity ->
                    contactEntity.toContact()
                }
            }
    }

    override suspend fun insertContact(contact: Contact) {
        queries.insertContactEntity(
            id = contact.id,
            firstName = contact.firstName,
            lastName = contact.lastName,
            phoneNumber = contact.phoneNumber,
            email = contact.email,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            imagePath = null
        )
    }

    override suspend fun deleteContact(id: Long) {
        queries.deleteContact(id)
    }
}