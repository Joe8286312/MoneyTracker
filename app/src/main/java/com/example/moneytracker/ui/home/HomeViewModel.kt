package com.example.moneytracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.model.TransactionEntity
import com.example.moneytracker.data.repository.MoneyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: MoneyRepository) : ViewModel() {

    // stateIn 操作符的作用是将冷流 (Flow) 转化为热流 (StateFlow)。
    // 它可以缓存最新的状态，这样当屏幕旋转导致 UI 重新绑定时，能立刻获取到最新数据。
    // SharingStarted.WhileSubscribed(5000) 是一种优化机制，UI 不可见 5 秒后自动停止监听数据库，节省手机电量。

    val netWorth: StateFlow<Double> = repository.getNetWorth()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val currentMonthExpense: StateFlow<Double> = repository.getCurrentMonthExpense()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val currentMonthIncome: StateFlow<Double> = repository.getCurrentMonthIncome()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

// 工厂类：负责向 HomeViewModel 注入 MoneyRepository 实例
class HomeViewModelFactory(private val repository: MoneyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}