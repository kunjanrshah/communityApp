package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Educations(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    val name: String
)