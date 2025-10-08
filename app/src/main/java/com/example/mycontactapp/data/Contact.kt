package com.example.mycontactapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Biến Contact thành một bảng trong database
@Entity(tableName = "contacts")
data class Contact(
    // Đánh dấu 'id' là khóa chính và để database tự động tạo giá trị
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val phoneNumber: String
)
