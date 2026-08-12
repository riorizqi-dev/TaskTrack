package com.dark.appmanejementugasharian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dark.appmanejementugasharian.model.Task
import com.dark.appmanejementugasharian.ui.theme.AppManejemenTugasHarianTheme
import com.dark.appmanejementugasharian.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppManejemenTugasHarianTheme {
                val viewModel: TaskViewModel = viewModel()
                val tasks by viewModel.tasks.collectAsState()
                var showAddSheet by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = { TopAppBarComponent() },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showAddSheet = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(32.dp))
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        ) {
                            SummaryCard(tasks)
                            Text(
                                "Tugas Hari Ini",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            TaskList(tasks, onTaskChecked = { task, checked ->
                                viewModel.updateTask(task.copy(isCompleted = checked))
                            })
                        }

                        if (showAddSheet) {
                            AddTaskBottomSheet(
                                onDismiss = { showAddSheet = false },
                                onSave = { title, desc ->
                                    viewModel.addTask(Task(title = title, description = desc, deadline = System.currentTimeMillis()))
                                    showAddSheet = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComponent() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Menampilkan Logo dari mipmap ic_launcher
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "TaskTrack",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "Manajemen Tugas Harian",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { /* Profile */ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun SummaryCard(tasks: List<Task>) {
    val total = tasks.size
    val completed = tasks.count { it.isCompleted }
    val pending = total - completed

    val gradient = Brush.horizontalGradient(
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
    ) {
        Box(modifier = Modifier.background(gradient).padding(28.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryItem("Total", total.toString(), Icons.Default.List)
                Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.White.copy(alpha = 0.3f))
                SummaryItem("Selesai", completed.toString(), Icons.Default.CheckCircle)
                Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.White.copy(alpha = 0.3f))
                SummaryItem("Belum", pending.toString(), Icons.Default.Info)
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, count: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Text(count, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TaskList(tasks: List<Task>, onTaskChecked: (Task, Boolean) -> Unit) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada tugas. Semangat!", color = Color.Gray)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskItem(task, onTaskChecked)
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onTaskChecked: (Task, Boolean) -> Unit) {
    val backgroundColor = if (task.isCompleted) Color(0xFFF5F5F5) else Color.White
    val titleColor = if (task.isCompleted) Color.Gray else Color.Black
    val elevation = if (task.isCompleted) 0.dp else 4.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (task.isCompleted) Color.LightGray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (task.isCompleted) Icons.Default.Done else Icons.Default.Edit,
                    contentDescription = null,
                    tint = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = titleColor,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(task.deadline)),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onTaskChecked(task, it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .fillMaxWidth()
        ) {
            Text("Buat Tugas Baru", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            Text("Tuliskan apa yang harus kamu selesaikan hari ini", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Tugas") },
                placeholder = { Text("Contoh: Kerjakan Tugas PBO") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Keterangan Tambahan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { if (title.isNotBlank()) onSave(title, desc) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("Simpan Tugas Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
