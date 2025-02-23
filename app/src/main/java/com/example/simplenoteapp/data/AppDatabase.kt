package com.example.simplenoteapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.simplenoteapp.data.freespace.FreeSpace
import com.example.simplenoteapp.data.freespace.FreeSpaceDao
import com.example.simplenoteapp.data.note.Note
import com.example.simplenoteapp.data.note.NoteDao

@Database(entities = [Note::class, FreeSpace::class], version = 2,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun freeSpaceDao(): FreeSpaceDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // ここに、テーブル追加・変更・削除に必要なSQL文を記述
                // 例: database.execSQL("ALTER TABLE Note ADD COLUMN new_column TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appDatabase"
                ).addMigrations(MIGRATION_1_2)

//                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance    // 戻り値
            }
        }
    }

}