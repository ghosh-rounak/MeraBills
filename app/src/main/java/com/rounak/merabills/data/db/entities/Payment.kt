package com.rounak.merabills.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_payments")
data class Payment(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "amount") val amount: Int,

    @ColumnInfo(name = "payment_type") val paymentType: String,

    @ColumnInfo(name = "provider") val provider: String,

    @ColumnInfo(name = "txn_ref") val txnRef: String
)