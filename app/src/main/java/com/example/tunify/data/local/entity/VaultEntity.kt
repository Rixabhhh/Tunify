package com.example.tunify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaults")
data class VaultEntity(
    @PrimaryKey(autoGenerate = true) val vaultId: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)