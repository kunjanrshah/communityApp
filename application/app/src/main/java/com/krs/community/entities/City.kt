package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
        @PrimaryKey(autoGenerate = false)
        var id: Int,
        var name: String,
        var parent_id: Int
) {
    constructor() : this(0, "", 0)
}