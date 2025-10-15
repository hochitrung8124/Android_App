package com.example.mycontactapp.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mycontactapp.R
import com.example.mycontactapp.navigation.AppDestinations
import com.example.mycontactapp.ui.ContactViewModel
import com.example.mycontactapp.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ContactListScreen(navController: NavHostController, viewModel: ContactViewModel = viewModel()) {
    val context = LocalContext.current // Giữ context nếu cần cho Toast hoặc các việc khác sau này
    var nameInput by remember { mutableStateOf(TextFieldValue()) }
    var phoneNumberInput by remember { mutableStateOf(TextFieldValue()) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    // Lấy danh sách liên hệ từ StateFlow của ViewModel
    val allContacts by viewModel.allContacts.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Lọc danh sách dựa trên allContacts
    val filteredContacts = if (searchQuery.isBlank()) {
        allContacts
    } else {
        allContacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery, ignoreCase = true)
        }
    }
    val readContactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Nếu được cấp quyền, bắt đầu đồng bộ
                Toast.makeText(context, "Đang đồng bộ danh bạ...", Toast.LENGTH_SHORT).show()
                viewModel.syncContactsFromSystem()
            } else {
                // Nếu từ chối, thông báo cho người dùng
                Toast.makeText(context, "Quyền đọc danh bạ bị từ chối.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = { AppTopBar(title = "Danh Bạ Điện Thoại",
            showSyncAction = true,
            onSyncClick = {
                // Khi nhấn nút, khởi chạy yêu cầu quyền
                readContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
            )
                 },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddContactDialog = true },
                containerColor = colorResource(id = R.color.green),
                contentColor = Color.White,
                shape = RoundedCornerShape(100)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm liên hệ")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm liên hệ...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Biểu tượng tìm kiếm"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(
                    items = filteredContacts,
                    key = { contact -> contact.id }
                ) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("${AppDestinations.CONTACT_DETAIL_ROUTE}/${contact.id}")
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = contact.name, fontSize = 18.sp)
                        Text(text = contact.phoneNumber, fontSize = 16.sp, color = Color.Gray)
                    }
                    Divider() // Thêm đường kẻ phân cách
                }
            }
        }
    }

    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Thêm Liên Hệ Mới") },
            text = {
                Column {
                    TextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Tên liên hệ") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true
                    )
                    TextField(
                        value = phoneNumberInput,
                        onValueChange = { phoneNumberInput = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                        // keyboardType = KeyboardType.Phone // Có thể thêm
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = nameInput.text.trim()
                        val phone = phoneNumberInput.text.trim()

                        val isNameValid = name.isNotEmpty()
                        val isPhoneValid = Regex("^\\+?0[0-9]{9}$").matches(phone)

                        if (!isNameValid) {
                            Toast.makeText(context, "Tên không được để trống.", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        if (!isPhoneValid) {
                            Toast.makeText(context, "Số điện thoại không hợp lệ.", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        viewModel.addContact(name, phone)

                        nameInput = TextFieldValue("")
                        phoneNumberInput = TextFieldValue("")
                        showAddContactDialog = false
                    }
                ) {
                    Text("Thêm")
                }

            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

