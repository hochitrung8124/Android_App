package com.example.mycontactapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    // Lấy tất cả các liên hệ và sắp xếp theo tên, trả về một Flow
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    // Lấy một liên hệ duy nhất bằng ID, trả về một Flow
    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactById(id: Int): Flow<Contact>

    // Thêm một liên hệ mới
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    // Cập nhật một liên hệ đã có
    @Update
    suspend fun update(contact: Contact)

    // Xóa một liên hệ
    @Delete
    suspend fun delete(contact: Contact)
}