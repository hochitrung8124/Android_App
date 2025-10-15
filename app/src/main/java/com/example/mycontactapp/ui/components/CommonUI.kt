package com.example.mycontactapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // Icon xóa
import androidx.compose.material.icons.filled.Edit // Icon sửa
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.mycontactapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateUp: () -> Unit = {},
    showEditDeleteActions: Boolean = false, // Đổi tên cho rõ ràng
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    showSyncAction: Boolean = false, // Cờ mới cho nút Sync
    onSyncClick: () -> Unit = {} // Action mới
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }
            }
        },
        actions = {
            if (showEditDeleteActions) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Sửa liên hệ",
                        tint = Color.White // Tùy chỉnh màu nếu cần
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Xóa liên hệ",
                        tint = Color.White // Tùy chỉnh màu nếu cần
                    )
                }
            }
            if (showSyncAction) {
                IconButton(onClick = onSyncClick) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Đồng bộ danh bạ"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors( // Tùy chỉnh màu cho TopAppBar nếu muốn
            containerColor = colorResource(id = R.color.green),
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer // Hoặc onPrimary
        )
    )
}
fun isValidName(name: String): Boolean {
    return name.trim().isNotEmpty()
}

// ✅ Hàm kiểm tra số điện thoại
fun isValidPhoneNumber(phone: String): Boolean {
    val regex = Regex("^\\+?[0-9]{9,15}$")
    return regex.matches(phone)
}
