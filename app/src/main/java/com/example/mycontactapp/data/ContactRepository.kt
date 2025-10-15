package com.example.mycontactapp.data

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// Interface để dễ dàng thay thế triển khai sau này (ví dụ: cho testing)
interface IContactRepository {
    fun getAllContacts(): Flow<List<Contact>>
    fun getContactById(id: Int): Flow<Contact>
    suspend fun insertContact(contact: Contact)
    suspend fun updateContact(contact: Contact)
    suspend fun deleteContact(contact: Contact)
    suspend fun syncSystemContacts()
}

// Triển khai Repository sử dụng ContactDao
class ContactRepository(private val contactDao: ContactDao, private val context: Context) : IContactRepository {
    override fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()
    override fun getContactById(id: Int): Flow<Contact> = contactDao.getContactById(id)
    override suspend fun insertContact(contact: Contact) = contactDao.insert(contact)
    override suspend fun updateContact(contact: Contact) = contactDao.update(contact)
    override suspend fun deleteContact(contact: Contact) = contactDao.delete(contact)

    override suspend fun syncSystemContacts() {
        withContext(Dispatchers.IO) {
            val systemContacts = fetchSystemContacts()
            // Xóa danh bạ cũ có nguồn gốc từ hệ thống (tùy chọn) hoặc chỉ thêm mới
            // Ở đây chúng ta sẽ chỉ chèn, Room với onConflict = IGNORE sẽ bỏ qua nếu đã có
            systemContacts.forEach { contact ->
                // Chúng ta không có ID từ hệ thống, vì vậy Room sẽ tự tạo ID mới
                contactDao.insert(Contact(name = contact.name, phoneNumber = contact.phoneNumber))
            }
        }
    }

    private fun fetchSystemContacts(): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver

        // Các cột cần truy vấn
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" // Sắp xếp theo tên
        )

        cursor?.use { cur ->
            val nameIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (nameIndex == -1 || numberIndex == -1) {
                return@use // Nếu không tìm thấy cột, thoát
            }
            while (cur.moveToNext()) {
                val name = cur.getString(nameIndex)
                val number = cur.getString(numberIndex)
                if (name != null && number != null) {
                    contactsList.add(Contact(name = name, phoneNumber = number))
                }
            }
        }
        return contactsList
    }
}