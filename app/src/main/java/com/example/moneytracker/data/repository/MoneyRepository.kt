package com.example.moneytracker.data.repository

import com.example.moneytracker.data.dao.AccountDao
import com.example.moneytracker.data.dao.CategoryDao
import com.example.moneytracker.data.dao.TransactionDao
import com.example.moneytracker.data.model.TransactionEntity
import com.example.moneytracker.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class MoneyRepository(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao
) {
    // 暴露插入方法给后续记账功能使用
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }

    // 获取最近的账单流水
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    // 计算净资产：总收入 - 总支出
    // .map 操作符会在每次数据库更新时，重新计算结果并发送给下游
    fun getNetWorth(): Flow<Double> {
        return transactionDao.getAllTransactions().map { transactions ->
            val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            totalIncome - totalExpense
        }
    }

    // 计算当月支出
    fun getCurrentMonthExpense(): Flow<Double> {
        return transactionDao.getAllTransactions().map { transactions ->
            val (startOfMonth, endOfMonth) = getCurrentMonthTimeRange()
            transactions
                .filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfMonth..endOfMonth }
                .sumOf { it.amount }
        }
    }

    // 计算当月收入
    fun getCurrentMonthIncome(): Flow<Double> {
        return transactionDao.getAllTransactions().map { transactions ->
            val (startOfMonth, endOfMonth) = getCurrentMonthTimeRange()
            transactions
                .filter { it.type == TransactionType.INCOME && it.timestamp in startOfMonth..endOfMonth }
                .sumOf { it.amount }
        }
    }

    // 辅助方法：获取本月第一天和最后一天的时间戳 (毫秒)
    private fun getCurrentMonthTimeRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        return Pair(startOfMonth, endOfMonth)
    }
}