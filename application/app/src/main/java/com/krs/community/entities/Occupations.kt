package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Occupations(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)