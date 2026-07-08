package com.example.moneytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 默认值为 0，Room 会自动递增
    val name: String,
    val isLiability: Boolean = false // 标识是否为负债账户（如信用卡），方便计算净资产
)