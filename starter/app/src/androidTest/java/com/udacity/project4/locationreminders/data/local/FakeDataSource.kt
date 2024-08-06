package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value:Boolean){
        shouldReturnError = value
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError){
            return Result.Error("shouldReturnError was set to true in the FakeDataSource")
        }
        if (reminders == null) {
            return Result.Error("reminders == null in the FakeDateSource")
        }

        return Result.Success(ArrayList(reminders!!))
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return Result.Error("shouldReturnError was set to true in the FakeDataSource")
        }
        if (reminders == null) {
            return Result.Error("reminders == null in the FakeDateSource")
        }


        val foundReminder = reminders?.find { it.id == id }

        if (foundReminder == null) {
            return Result.Error("Reminder with id $id could not be found in the FakeDataSoruce")
        }

        return Result.Success(foundReminder)
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}