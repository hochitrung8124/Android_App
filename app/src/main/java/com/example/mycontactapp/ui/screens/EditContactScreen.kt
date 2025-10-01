package com.example.mycontactapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mycontactapp.data.Contact
import com.example.mycontactapp.data.sampleContacts
import com.example.mycontactapp.navigation.AppDestinations
import com.example.mycontactapp.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    navController: NavHostController,
    contactId: Int?
) {
    val context = LocalContext.current
    val contactToEdit = sampleContacts.find { it.id == contactId }

    var nameInput by remember(contactToEdit) { mutableStateOf(TextFieldValue(contactToEdit?.name ?: "")) }
    var phoneNumberInput by remember(contactToEdit) { mutableStateOf(TextFieldValue(contactToEdit?.phoneNumber ?: "")) }

    if (contactToEdit == null && contactId != null) { // Xử lý trường hợp contactId có nhưng không tìm thấy
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Không tìm thấy liên hệ để sửa.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return // Không hiển thị gì nếu không có contact
    }


    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sửa Liên Hệ",
                canNavigateBack = true,
                onNavigateUp = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (contactToEdit != null) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Tên liên hệ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phoneNumberInput,
                    onValueChange = { phoneNumberInput = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (nameInput.text.isNotBlank() && phoneNumberInput.text.isNotBlank()) {
                            val updatedContact = contactToEdit.copy(
                                name = nameInput.text,
                                phoneNumber = phoneNumberInput.text
                            )
                            // Cập nhật trong danh sách
                            val index = sampleContacts.indexOfFirst { it.id == contactToEdit.id }
                            if (index != -1) {
                                sampleContacts[index] = updatedContact
                            }
                            Toast.makeText(context, "Đã cập nhật liên hệ!", Toast.LENGTH_SHORT).show()
                            // Quay lại màn hình chi tiết hoặc danh sách
                            // Để quay lại màn hình chi tiết và cập nhật nó, bạn cần cơ chế phức tạp hơn
                            // (ví dụ: sử dụng shared ViewModel hoặc truyền kết quả trở lại)
                            // Cách đơn giản nhất là quay lại danh sách
                            navController.popBackStack(AppDestinations.CONTACT_LIST_ROUTE, inclusive = false)
                            // Hoặc nếu muốn quay lại màn hình chi tiết (cần đảm bảo nó tự cập nhật)
                            // navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Tên và số điện thoại không được để trống.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Lưu Thay Đổi")
                }
            } else if (contactId == null) { // Trường hợp điều hướng mà không có ID (ví dụ: lỗi)
                Text("Không có liên hệ nào được chọn để sửa.")
            }
        }
    }
}

