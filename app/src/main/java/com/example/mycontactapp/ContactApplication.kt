package com.example.mycontactapp

import android.app.Application
import com.example.mycontactapp.data.AppDatabase
import com.example.mycontactapp.data.ContactRepository

class ContactApplication : Application() {
    // Sử dụng lazy để database và repository chỉ được tạo khi cần
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ContactRepository(database.contactDao(), this) }
}
