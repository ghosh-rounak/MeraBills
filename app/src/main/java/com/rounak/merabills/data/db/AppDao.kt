package com.rounak.merabills.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rounak.merabills.data.db.entities.Payment

@Dao
interface AppDao {

    @Query("DELETE FROM tbl_payments")
    suspend fun deleteAllPayments()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayments(paymentList: List<Payment>)

    @Query("SELECT * FROM  tbl_payments")
    suspend fun getAllPayments(): List<Payment>

}