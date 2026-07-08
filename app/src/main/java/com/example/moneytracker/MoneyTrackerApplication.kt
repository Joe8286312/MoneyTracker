package com.example.moneytracker

import android.app.Application
import androidx.room.Room
import com.example.moneytracker.data.AppDatabase
import com.example.moneytracker.data.repository.MoneyRepository

class MoneyTrackerApplication : Application() {

    // 使用 by lazy (懒加载)：只有当第一次被调用时，才会真正去执行初始化代码
    // 这等同于 Java 中的单例模式 (Double-checked locking Singleton)
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "money_tracker.db"
        ).build()
    }

    // 实例化 Repository，并将数据库的 DAO 传递给它
    val repository by lazy {
        MoneyRepository(
            transactionDao = database.transactionDao(),
            accountDao = database.accountDao(),
            categoryDao = database.categoryDao()
        )
    }
}