package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BudgetEntry
import com.example.data.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BudgetRepository

    val allEntries: StateFlow<List<BudgetEntry>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BudgetRepository(database.budgetEntryDao())
        allEntries = repository.allEntries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Input States
    private val _entryName = MutableStateFlow("")
    val entryName: StateFlow<String> = _entryName.asStateFlow()

    private val _entryAmount = MutableStateFlow("")
    val entryAmount: StateFlow<String> = _entryAmount.asStateFlow()

    private val _isIncome = MutableStateFlow(true)
    val isIncome: StateFlow<Boolean> = _isIncome.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onNameChange(name: String) {
        _entryName.value = name
        _errorMessage.value = null
    }

    fun onAmountChange(amount: String) {
        // Prevent typing non-numeric characters but allow decimal numbers
        val cleanAmount = amount.replace(",", "")
        if (cleanAmount.isEmpty() || cleanAmount.toDoubleOrNull() != null || cleanAmount == ".") {
            _entryAmount.value = cleanAmount
        }
        _errorMessage.value = null
    }

    fun setIncomeMode(isIncome: Boolean) {
        _isIncome.value = isIncome
    }

    fun addEntry() {
        val nameTrimmed = _entryName.value.trim()
        val amountStr = _entryAmount.value.trim()

        if (nameTrimmed.isEmpty()) {
            _errorMessage.value = "Please enter an entry name."
            return
        }

        val parsedAmount = amountStr.toDoubleOrNull()
        if (parsedAmount == null || parsedAmount <= 0) {
            _errorMessage.value = "Please enter a valid positive amount."
            return
        }

        viewModelScope.launch {
            repository.insert(
                BudgetEntry(
                    name = nameTrimmed,
                    amount = parsedAmount,
                    isIncome = _isIncome.value
                )
            )
            // Reset input values
            _entryName.value = ""
            _entryAmount.value = ""
            _errorMessage.value = null
        }
    }

    fun deleteEntry(entry: BudgetEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
