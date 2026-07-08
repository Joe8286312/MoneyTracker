package com.example.moneytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: CategoryType,
    val parentId: Int? = null // Kotlin 中的 ? 表示该字段可以为 null，等价于 Java 的 @Nullable
)