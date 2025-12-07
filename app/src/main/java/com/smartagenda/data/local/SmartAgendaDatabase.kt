package com.smartagenda.data.local

import androidx.room.*
import com.smartagenda.data.model.Event
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object pour les événements
 */
@Dao
interface EventDao {
    
    @Query("SELECT * FROM events WHERE date = :date ORDER BY time ASC")
    fun getEventsForDate(date: String): Flow<List<Event>>
    
    @Query("SELECT * FROM events WHERE date >= :startDate ORDER BY date ASC, time ASC")
    fun getUpcomingEvents(startDate: String): Flow<List<Event>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)
    
    @Query("DELETE FROM events WHERE date < :cutoffDate")
    suspend fun deleteOldEvents(cutoffDate: String)
    
    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()
}

/**
 * Type converters pour Room
 */
class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): java.time.LocalDateTime? {
        return value?.let { java.time.LocalDateTime.ofEpochSecond(it, 0, java.time.ZoneOffset.UTC) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: java.time.LocalDateTime?): Long? {
        return date?.toEpochSecond(java.time.ZoneOffset.UTC)
    }
}

/**
 * Base de données Room pour SmartAgenda
 */
@Database(
    entities = [Event::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SmartAgendaDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
