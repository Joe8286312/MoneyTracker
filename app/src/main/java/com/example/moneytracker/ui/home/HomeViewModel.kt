package com.example.moneytracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.model.Account
import com.example.moneytracker.data.model.Category
import com.example.moneytracker.data.model.CategoryType
import com.example.moneytracker.data.model.TransactionEntity
import com.example.moneytracker.data.model.TransactionType
import com.example.moneytracker.data.repository.MoneyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MoneyRepository) : ViewModel() {

    init {
        // 初始化时，插入一些默认的账户和分类，防止后续插入账单时触发 SQLite 外键约束崩溃
        viewModelScope.launch {
            repository.initDefaultDataIfNeeded()
        }
    }

    val netWorth: StateFlow<Double> = repository.getNetWorth()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentMonthExpense: StateFlow<Double> = repository.getCurrentMonthExpense()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentMonthIncome: StateFlow<Double> = repository.getCurrentMonthIncome()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 暴露给底部的账户和分类列表，供记账页面选择
    val accounts: StateFlow<List<Account>> = repository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 处理用户的记账请求
    fun addTransaction(amount: Double, type: TransactionType, accountId: Int, categoryId: Int?, remark: String) {
        viewModelScope.launch {
            val transaction = TransactionEntity(
                amount = amount,
                type = type,
                accountId = accountId,
                categoryId = categoryId,
                timestamp = System.currentTimeMillis(),
                remark = remark.ifBlank { "日常记账" }
            )
            repository.insertTransaction(transaction)
        }
    }
}

class HomeViewModelFactory(private val repository: MoneyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}