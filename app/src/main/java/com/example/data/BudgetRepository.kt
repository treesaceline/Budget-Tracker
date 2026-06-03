package com.example.data

import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetEntryDao) {
    val allEntries: Flow<List<BudgetEntry>> = dao.getAllEntries()

    suspend fun insert(entry: BudgetEntry) {
        dao.insertEntry(entry)
    }

    suspend fun delete(entry: BudgetEntry) {
        dao.deleteEntry(entry)
    }

    suspend fun clearAll() {
        dao.deleteAllEntries()
    }
}
