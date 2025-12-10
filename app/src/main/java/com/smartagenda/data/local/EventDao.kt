package com.smartagenda.data.local

import androidx.room.*
import com.smartagenda.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    
    @Query("SELECT * FROM events WHERE date = :date ORDER BY time ASC")
    fun getEventsForDate(date: String): Flow<List<Event>>
    
    @Query("SELECT * FROM events ORDER BY date DESC, time DESC LIMIT 100")
    fun getAllEvents(): Flow<List<Event>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)
    
    @Delete
    suspend fun deleteEvent(event: Event)
    
    @Query("DELETE FROM events WHERE date < :cutoffDate")
    suspend fun deleteOldEvents(cutoffDate: String)
    
    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()
}
