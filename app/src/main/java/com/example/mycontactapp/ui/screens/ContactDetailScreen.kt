package com.example.mycontactapp.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.mycontactapp.data.Contact // Thêm nếu chưa có
import com.example.mycontactapp.data.sampleContacts
import com.example.mycontactapp.navigation.AppDestinations // Thêm import
import com.example.mycontactapp.ui.components.AppTopBar
import com.example.mycontactapp.utils.makeCall
import android.content.pm.PackageManager
import androidx.compose.ui.res.colorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    navController: NavHostController,
    contactId: Int?
) {
    val context = LocalContext.current
    // Sử dụng derivedStateOf để contact tự cập nhật khi sampleContacts thay đổi
    val contact by remember(contactId) {
        derivedStateOf { sampleContacts.find { it.id == contactId } }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                contact?.phoneNumber?.let { makeCall(context, it) }
            } else {
                Toast.makeText(context, "Quyền gọi điện bị từ chối.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Nếu contact bị xóa (trở thành null), quay lại màn hình trước đó
//    LaunchedEffect(contact) {
//        if (contact == null && contactId != null) { // contactId khác null để chắc chắn đây là màn hình chi tiết
//            Toast.makeText(context, "Liên hệ đã bị xóa hoặc không tồn tại.", Toast.LENGTH_SHORT).show()
//            navController.popBackStack()
//        }
//    }


    Scaffold(
        topBar = {
            AppTopBar(
                title = contact?.name ?: "Chi Tiết",
                canNavigateBack = true,
                onNavigateUp = { navController.popBackStack() }, // Sử dụng popBackStack để quay lại
                showActions = contact != null, // Chỉ hiển thị actions nếu có contact
                onEditClick = {
                    // Điều hướng đến màn hình sửa với contactId
                    navController.navigate("${AppDestinations.EDIT_CONTACT_ROUTE}/${contact?.id}")
                },
                onDeleteClick = {
                    showDeleteDialog = true // Hiển thị dialog xác nhận xóa
                }
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
            contact?.let { // Chỉ hiển thị nội dung nếu contact không null
                Text("ID: ${it.id}", style = MaterialTheme.typography.labelLarge)
                Text(it.name, style = MaterialTheme.typography.headlineMedium)
                Text(it.phoneNumber, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        when (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        )) {
                            PackageManager.PERMISSION_GRANTED -> {
                                makeCall(context, it.phoneNumber)
                            }
                            else -> {
                                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                ) {
                    Text("Gọi ${it.name}")
                }
            } ?: run {
                // Nếu contact là null sau khi LaunchedEffect chạy (ví dụ: bị xóa từ màn hình khác)
                // Hoặc nếu contactId ban đầu là null
                if (contactId != null) { // Chỉ hiển thị nếu đang cố gắng xem chi tiết 1 contact cụ thể
                    CircularProgressIndicator() // Hiển thị loading hoặc thông báo khác
                } else {
                    Text("Không có thông tin liên hệ để hiển thị.")
                }
            }
        }
    }

    // Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận Xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa liên hệ '${contact?.name}' không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        contact?.let { contactToDelete ->
                            sampleContacts.remove(contactToDelete) // Xóa khỏi danh sách
                            Toast.makeText(context, "Đã xóa liên hệ.", Toast.LENGTH_SHORT).show()
                            showDeleteDialog = false
                            navController.popBackStack() // Quay lại màn hình danh sách
                        }
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}
