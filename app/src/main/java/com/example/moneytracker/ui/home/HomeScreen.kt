package com.example.moneytracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.data.model.TransactionEntity
import com.example.moneytracker.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddTransactionClick: () -> Unit
) {
    // 💡 核心：把 ViewModel 中的 StateFlow 转换为 Compose 能识别的 State。
    // 每当底层 Flow 流出新数值，这里的变量会自动更新，并触发下方的受影响组件“重组”。
    val netWorth by viewModel.netWorth.collectAsState()
    val currentMonthIncome by viewModel.currentMonthIncome.collectAsState()
    val currentMonthExpense by viewModel.currentMonthExpense.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()

    // 👉 新增：用于控制底部面板显示/隐藏的状态
    var showAddSheet by remember { mutableStateOf(false) }

    // Scaffold 是 Material Design 的标准页面结构脚手架，能极其方便地安放 FAB 悬浮按钮
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // 👉 修改：不再向外传递点击事件，直接在这里改变状态，呼出面板
                showAddSheet = true
            }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "添加账单")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. 顶部资产卡片
            DashboardCard(
                netWorth = netWorth,
                income = currentMonthIncome,
                expense = currentMonthExpense
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "最近账单 (仅显示10条)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 2. 账单明细列表 (过滤展示最近10条)
            val displayTransactions = recentTransactions.take(10)
            if (displayTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无账单数据，请点击右下角添加",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayTransactions) { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
    // 👉 新增：当状态为 true 时，显示我们刚才写的底部面板
    if (showAddSheet) {
        AddTransactionSheet(
            viewModel = viewModel,
            onDismiss = { showAddSheet = false } // 传递关闭面板的回调
        )
    }
}

@Composable
fun DashboardCard(
    netWorth: Double,
    income: Double,
    expense: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "净资产 (总存款 - 总负债)",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format(Locale.getDefault(), "￥%.2f", netWorth),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "本月收入",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "￥%.2f", income),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "本月支出",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "￥%.2f", expense),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(transaction.timestamp))

    val amountPrefix = when (transaction.type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        TransactionType.TRANSFER -> ""
    }

    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.remark ?: "未分类账单",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text = String.format(Locale.getDefault(), "%s￥%.2f", amountPrefix, transaction.amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}