package com.androiddevs.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.pojo.Source

class Converters {

    @TypeConverter
    fun toSource(s: String): Source{
        return Source(s, s)
    }

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }
}