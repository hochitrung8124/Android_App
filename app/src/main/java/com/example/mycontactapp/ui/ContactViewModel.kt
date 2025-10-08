package com.example.mycontactapp.ui

// Import thêm AndroidViewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycontactapp.ContactApplication // Import lớp Application
import com.example.mycontactapp.data.Contact
import com.example.mycontactapp.data.IContactRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Kế thừa từ AndroidViewModel thay vì ViewModel
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    // Lấy repository từ lớp Application
    private val repository: IContactRepository = (application as ContactApplication).repository

    // Phần còn lại của ViewModel giữ nguyên
    val allContacts: StateFlow<List<Contact>> = repository.getAllContacts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun getContactById(id: Int): StateFlow<Contact?> {
        return repository.getContactById(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )
    }

    fun addContact(name: String, phone: String) {
        viewModelScope.launch {
            if (name.isNotBlank() && phone.isNotBlank()) {
                repository.insertContact(Contact(name = name, phoneNumber = phone))
            }
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            repository.updateContact(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }
}