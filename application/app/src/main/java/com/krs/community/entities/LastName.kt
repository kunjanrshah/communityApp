package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastName(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    val name: String
)