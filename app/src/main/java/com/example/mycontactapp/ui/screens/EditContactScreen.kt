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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mycontactapp.ui.ContactViewModel
import com.example.mycontactapp.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    navController: NavHostController,
    contactId: Int?,
    viewModel: ContactViewModel = viewModel()
) {
    val context = LocalContext.current

    // 1. Lấy trạng thái của liên hệ từ ViewModel dưới dạng StateFlow
    // Nếu contactId là null, contactState sẽ là null
    val contactState by if (contactId != null) {
        viewModel.getContactById(contactId).collectAsState()
    } else {
        remember { mutableStateOf(null) }
    }

    var nameInput by remember { mutableStateOf(TextFieldValue("")) }
    var phoneNumberInput by remember { mutableStateOf(TextFieldValue("")) }

    // 2. Sử dụng LaunchedEffect để cập nhật các trường nhập liệu một lần khi contactState có dữ liệu
    LaunchedEffect(contactState) {
        contactState?.let {
            nameInput = TextFieldValue(it.name)
            phoneNumberInput = TextFieldValue(it.phoneNumber)
        }
    }

    // Xử lý trường hợp không tìm thấy liên hệ hoặc không có ID
    if (contactId == null) {
        // Hiển thị thông báo và quay lại nếu không có ID
        LaunchedEffect(Unit) {
            Toast.makeText(context, "ID liên hệ không hợp lệ.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return // Không hiển thị UI
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
            // Chỉ hiển thị giao diện khi đã có dữ liệu liên hệ
            if (contactState != null) {
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
                            // 3. Gọi phương thức update của ViewModel
                            val updatedContact = contactState!!.copy(
                                name = nameInput.text,
                                phoneNumber = phoneNumberInput.text
                            )
                            viewModel.updateContact(updatedContact)

                            Toast.makeText(context, "Đã cập nhật liên hệ!", Toast.LENGTH_SHORT).show()
                            // Quay lại màn hình trước đó (màn hình chi tiết)
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Tên và số điện thoại không được để trống.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Lưu Thay Đổi")
                }
            } else {
                // Hiển thị vòng quay tải trong khi chờ dữ liệu
                CircularProgressIndicator()
            }
        }
    }
}
