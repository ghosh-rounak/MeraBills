package com.rounak.merabills.data.repository

import com.rounak.merabills.data.db.AppDao
import com.rounak.merabills.data.db.entities.Payment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val dao: AppDao
) {
    suspend fun saveAllPayments(paymentList: List<Payment>) {
        dao.insertAllPayments(paymentList = paymentList)
    }

    suspend fun getAllPayments(): List<Payment> = dao.getAllPayments()

    suspend fun deleteAllPayments() {
        dao.deleteAllPayments()
    }
}