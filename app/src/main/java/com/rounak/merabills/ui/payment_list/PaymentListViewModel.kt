package com.rounak.merabills.ui.payment_list

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rounak.merabills.constants.Constants
import com.rounak.merabills.data.db.entities.Payment
import com.rounak.merabills.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel(), Observable {
    private val uiPaymentList:MutableList<Payment> = mutableListOf()

    var dropdownPaymentTypes:MutableList<String> = mutableListOf()

    private val _uiMsg = Channel<String>()
    val uiMsg = _uiMsg.receiveAsFlow()

    private val _uiPayments: MutableStateFlow<MutableList<Payment>> = MutableStateFlow(mutableListOf())
    val uiPayments: StateFlow<MutableList<Payment>> = _uiPayments.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _totalAmount: MutableStateFlow<Int> = MutableStateFlow(0)
    val totalAmount: StateFlow<Int> = _totalAmount.asStateFlow()

    init {
        getAllPayments()
    }

    private fun updatePaymentsUI(){
        val updatedPaymentList:MutableList<Payment> = mutableListOf()
        updatedPaymentList.addAll(uiPaymentList)
        _totalAmount.value = uiPaymentList.sumOf {
            it.amount
        }
        _uiPayments.value = updatedPaymentList
    }

    fun addPayment(amount:Int,paymentType:String,provider:String,txnRef:String){
        val paymentId:Int = when(paymentType){
            Constants.CASH -> 1
            Constants.BANK_TRANSFER -> 2
            Constants.CREDIT_CARD -> 3
            else -> throw Exception("Invalid Payment Id")
        }
        val payment = Payment(
            id = paymentId,
            amount = amount,
            paymentType = paymentType,
            provider = provider,
            txnRef = txnRef
        )

        uiPaymentList.add(payment)
        prepareDropDownList()
        updatePaymentsUI()
    }

    private fun showLoader(b:Boolean){
        _loading.value = b
    }

    private fun prepareDropDownList(){
        val tempList = uiPaymentList.sortedBy {
            it.id
        }.map { it.paymentType }

        dropdownPaymentTypes = mutableListOf()
        if(!tempList.contains(Constants.CASH)){
            dropdownPaymentTypes.add(Constants.CASH)
        }

        if(!tempList.contains(Constants.BANK_TRANSFER)){
            dropdownPaymentTypes.add(Constants.BANK_TRANSFER)
        }

        if(!tempList.contains(Constants.CREDIT_CARD)){
            dropdownPaymentTypes.add(Constants.CREDIT_CARD)
        }
    }



    private fun getAllPayments(){
        showLoader(true)
        viewModelScope.launch {
            val dbPayments:List<Payment> = repository.getAllPayments()
            uiPaymentList.clear()
            uiPaymentList.addAll(dbPayments)
            prepareDropDownList()
            updatePaymentsUI()
            showLoader(false)
        }
    }

    fun saveAllPayments(){
        showLoader(true)
        viewModelScope.launch {
            repository.deleteAllPayments()
            repository.saveAllPayments(paymentList = uiPaymentList)
            showLoader(false)
            _uiMsg.send("Saved Successfully")
        }
    }

    fun deletePayment(payment: Payment){
        showLoader(true)
        uiPaymentList.remove(payment)
        prepareDropDownList()
        updatePaymentsUI()
        showLoader(false)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}