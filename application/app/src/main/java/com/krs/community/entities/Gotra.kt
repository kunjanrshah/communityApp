package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Gotra(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)