package com.example.mycontactapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    // Xử lý trường hợp không có ID ngay từ đầu (giữ nguyên)
    if (contactId == null) {
        // ... (phần này giữ nguyên như cũ)
        LaunchedEffect(Unit) {
            Toast.makeText(context, "ID liên hệ không hợp lệ.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    // 1. Tải dữ liệu MỘT LẦN khi Composable được tạo
    LaunchedEffect(key1 = contactId) {
        viewModel.loadContactForEdit(contactId)
    }

    // 2. Lắng nghe state dành riêng cho màn hình Edit từ ViewModel
    val editContact by viewModel.editContactState.collectAsState()

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
            // Chỉ hiển thị giao diện khi dữ liệu đã được tải
            if (editContact != null) {
                // Các TextField giờ đây lấy giá trị trực tiếp từ state của ViewModel
                OutlinedTextField(
                    value = editContact!!.name,
                    onValueChange = { viewModel.onNameChange(it) }, // Gọi phương thức của ViewModel
                    label = { Text("Tên liên hệ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editContact!!.phoneNumber,
                    onValueChange = { viewModel.onPhoneNumberChange(it) }, // Gọi phương thức của ViewModel
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val currentContact = editContact
                        if (currentContact != null && currentContact.name.isNotBlank() && currentContact.phoneNumber.isNotBlank()) {
                            // Gọi phương thức update của ViewModel
                            viewModel.updateContact(currentContact)
                            Toast.makeText(context, "Đã cập nhật liên hệ!", Toast.LENGTH_SHORT).show()
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
