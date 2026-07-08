package com.example.moneytracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moneytracker.data.dao.AccountDao
import com.example.moneytracker.data.dao.CategoryDao
import com.example.moneytracker.data.dao.TransactionDao
import com.example.moneytracker.data.model.Account
import com.example.moneytracker.data.model.Category
import com.example.moneytracker.data.model.Converters
import com.example.moneytracker.data.model.TransactionEntity

@Database(
    entities = [Account::class, Category::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // 注册我们在上面写的枚举转换器
abstract class AppDatabase : RoomDatabase() {
    // 暴露底层的 DAO，Room 编译器会自动生成它们的实现
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}