package com.example.mycontactapp.data

import kotlinx.coroutines.flow.Flow

// Interface để dễ dàng thay thế triển khai sau này (ví dụ: cho testing)
interface IContactRepository {
    fun getAllContacts(): Flow<List<Contact>>
    fun getContactById(id: Int): Flow<Contact>
    suspend fun insertContact(contact: Contact)
    suspend fun updateContact(contact: Contact)
    suspend fun deleteContact(contact: Contact)
}

// Triển khai Repository sử dụng ContactDao
class ContactRepository(private val contactDao: ContactDao) : IContactRepository {
    override fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()
    override fun getContactById(id: Int): Flow<Contact> = contactDao.getContactById(id)
    override suspend fun insertContact(contact: Contact) = contactDao.insert(contact)
    override suspend fun updateContact(contact: Contact) = contactDao.update(contact)
    override suspend fun deleteContact(contact: Contact) = contactDao.delete(contact)
}