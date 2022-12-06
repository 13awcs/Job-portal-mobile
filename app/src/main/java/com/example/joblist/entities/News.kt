package com.example.joblist.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "news")
data class News(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    var title: String,
    var content: String,
    var createdDate: String,
) : Parcelable
