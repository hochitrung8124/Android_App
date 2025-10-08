package com.example.mycontactapp.ui

// Import thêm AndroidViewModel
import android.app.Application
import androidx.compose.animation.core.copy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycontactapp.ContactApplication // Import lớp Application
import com.example.mycontactapp.data.Contact
import com.example.mycontactapp.data.IContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    // StateFlow riêng cho màn hình Edit, giữ trạng thái nhập liệu của người dùng.
    private val _editContactState = MutableStateFlow<Contact?>(null)
    val editContactState: StateFlow<Contact?> = _editContactState.asStateFlow()

    // 1. Tải dữ liệu vào editContactState MỘT LẦN
    fun loadContactForEdit(id: Int) {
        viewModelScope.launch {
            // Lấy giá trị đầu tiên từ Flow của Room và đặt nó vào _editContactState
            _editContactState.value = repository.getContactById(id).first()
        }
    }

    // 2. Cập nhật state cục bộ này khi người dùng gõ
    fun onNameChange(newName: String) {
        _editContactState.value = _editContactState.value?.copy(name = newName)
    }

    fun onPhoneNumberChange(newPhone: String) {
        _editContactState.value = _editContactState.value?.copy(phoneNumber = newPhone)
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