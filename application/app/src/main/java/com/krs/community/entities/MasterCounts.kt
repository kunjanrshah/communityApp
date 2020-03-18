package com.krs.community.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MasterCounts(
        @PrimaryKey(autoGenerate = false)
        var id: Int,
        var business_categories: Int,
        var business_sub_categories: Int,
        var cities: Int,
        var committees: Int,
        var current_activity: Int,
        var designations: Int,
        var districts: Int,
        var educations: Int,
        var local_community: Int,
        var native: Int,
        var occupation: Int,
        var relations: Int,
        var states: Int,
        var sub_casts: Int,
        var sub_community: Int
) {
    constructor() : this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
}