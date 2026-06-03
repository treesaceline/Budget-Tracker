package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetEntryDao {
    @Query("SELECT * FROM budget_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<BudgetEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: BudgetEntry)

    @Delete
    suspend fun deleteEntry(entry: BudgetEntry)

    @Query("DELETE FROM budget_entries")
    suspend fun deleteAllEntries()
}
