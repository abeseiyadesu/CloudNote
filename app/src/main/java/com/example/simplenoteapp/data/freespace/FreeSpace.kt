package com.example.simplenoteapp.data.freespace

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "free_space")
data class FreeSpace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    var header: String, // 常に1つだけ保持するのでIDは固定
    var detail: String
)