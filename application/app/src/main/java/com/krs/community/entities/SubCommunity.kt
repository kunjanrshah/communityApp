package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubCommunity(
        @PrimaryKey(autoGenerate = false)
        val id: Int,
        val name: String
)