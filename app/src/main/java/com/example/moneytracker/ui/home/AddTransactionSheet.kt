// 路径: app/src/main/java/com/example/moneytracker/ui/home/AddTransactionSheet.kt
package com.example.moneytracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.data.model.Category
import com.example.moneytracker.data.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    viewModel: HomeViewModel,
    onDismiss: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()

    // 核心参数：让面板直接展开到最高，不再半遮半掩
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amountStr by remember { mutableStateOf("0") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var remarkText by remember { mutableStateOf("") } // 👈 新增：备注状态

    // 动态过滤出当前交易类型对应的分类
    val displayCategories = categories.filter { it.type.name == selectedType.name }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    // 监听：当用户切换 收/支 类型时，自动选中该分类下的第一个选项
    LaunchedEffect(selectedType, displayCategories) {
        if (displayCategories.isNotEmpty() && !displayCategories.contains(selectedCategory)) {
            selectedCategory = displayCategories.first()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        // 高度设置为占据屏幕的 95%，给各种键盘留足空间
        modifier = Modifier.fillMaxHeight(0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. 交易类型切换 (支出/收入/转账)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TransactionType.entries.forEach { type ->
                    val isSelected = selectedType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedType = type },
                        label = {
                            Text(when(type) {
                                TransactionType.EXPENSE -> "支出"
                                TransactionType.INCOME -> "收入"
                                TransactionType.TRANSFER -> "转账"
                            })
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. 动态分类选择器 (支持横向滑动)
            if (displayCategories.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(displayCategories) { category ->
                        InputChip(
                            selected = selectedCategory?.id == category.id,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. 备注输入框 (点击会弹出系统拼音键盘)
            OutlinedTextField(
                value = remarkText,
                onValueChange = { remarkText = it },
                label = { Text("添加备注 (如: 粽子和豆浆)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 金额显示区 (靠右对齐)
            Text(
                text = "￥$amountStr",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. T9 数字键盘
            T9Keyboard(
                onKeyClick = { key ->
                    when (key) {
                        "⌫" -> {
                            if (amountStr.length > 1) amountStr = amountStr.dropLast(1)
                            else amountStr = "0"
                        }
                        "." -> {
                            if (!amountStr.contains(".")) amountStr += "."
                        }
                        "确认" -> {
                            val finalAmount = amountStr.toDoubleOrNull() ?: 0.0
                            if (finalAmount > 0) {
                                viewModel.addTransaction(
                                    amount = finalAmount,
                                    type = selectedType,
                                    // 默认存入账户1(微信/支付宝)，后期可扩展账户选择
                                    accountId = 1,
                                    categoryId = selectedCategory?.id,
                                    // 如果用户填了备注就用用户的，否则使用分类名兜底
                                    remark = remarkText.ifBlank { selectedCategory?.name ?: "日常记账" }
                                )
                                onDismiss()
                            }
                        }
                        else -> { // 点击了 0-9
                            if (amountStr == "0") {
                                amountStr = key
                            } else {
                                val parts = amountStr.split(".")
                                // 限制只能输入两位小数，防止输错
                                if (parts.size == 1 || parts[1].length < 2) {
                                    amountStr += key
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun T9Keyboard(onKeyClick: (String) -> Unit) {
    val keys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        ".", "0", "⌫"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(keys) { key ->
                KeyboardKey(text = key, onClick = { onKeyClick(key) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onKeyClick("确认") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("保存账单", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        // 底部安全距离，防止全面屏手势条遮挡
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun KeyboardKey(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(2.2f) // 稍微调扁一点点，给上方留空间
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}