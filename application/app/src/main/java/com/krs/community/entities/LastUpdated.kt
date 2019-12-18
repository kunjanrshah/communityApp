package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastUpdated(
   @PrimaryKey(autoGenerate = false)
   var name: String,
   var date:String
)