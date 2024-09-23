package com.rounak.merabills.ui.payment_list

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.rounak.merabills.R
import com.rounak.merabills.constants.Constants
import com.rounak.merabills.data.db.entities.Payment
import com.rounak.merabills.databinding.FragmentPaymentListBinding
import com.rounak.merabills.ui.payment_list.adapter.PaymentsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PaymentListFragment(
    var viewModel: PaymentListViewModel? = null
) : Fragment() {
    @Inject
    lateinit var paymentsAdapter: PaymentsAdapter

    private lateinit var binding:FragmentPaymentListBinding

    private var addPaymentDialog: AlertDialog? = null
    private var loaderAlertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        paymentsAdapter.setOnItemClickListener(
            deleteClickListener = { payment: Payment, position: Int ->
                paymentItemCloseIconClicked(
                    payment = payment,
                    position = position
                )
            }
        )
        paymentsAdapter.setContext(requireActivity())
        paymentsAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_list, container, false)
        setDataBinding()
        initLoaderAlertDialog()
        initRv()
        createCollectors()
        setOnClicks()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initLoaderAlertDialog() {
        if (loaderAlertDialog != null) {
            return
        }
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.loader_dialog, null)
        dialogBuilder.setView(dialogView)
        loaderAlertDialog = dialogBuilder.create()
        loaderAlertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loaderAlertDialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        loaderAlertDialog!!.setCancelable(false)
        loaderAlertDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun showAlertLoader(b:Boolean){
        if(b){
            loaderAlertDialog?.show()
        }else{
            loaderAlertDialog?.dismiss()
        }
    }

    private fun setDataBinding() {
        viewModel = viewModel ?: ViewModelProvider(this)[PaymentListViewModel::class.java]
        binding.paymentListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun showUIMsg(msg:String){
        Snackbar.make(
            binding.root,
            msg,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setOnClicks(){
        binding.savePaymentsBtn.setOnClickListener {
            viewModel!!.saveAllPayments()
        }

        binding.addPaymentBtn.setOnClickListener {
            if(viewModel!!.dropdownPaymentTypes.isEmpty()){
                showUIMsg(msg = "Please remove a payment type to proceed")
                return@setOnClickListener
            }
            showAddPaymentDialog()
        }
    }

    private fun createCollectors() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main.immediate) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel!!.uiPayments.collectLatest { paymentList: MutableList<Payment> ->
                        paymentsAdapter.differ.submitList(paymentList)
                    }
                }

                launch {
                    viewModel!!.loading.collectLatest { isLoading: Boolean ->
                        showAlertLoader(isLoading)
                    }
                }
                launch {
                    viewModel!!.totalAmount.collectLatest { amountTotal: Int ->
                        binding.total.text = amountTotal.toString()
                    }
                }
                launch {
                    viewModel!!.uiMsg.collectLatest { msg ->
                        showUIMsg(msg = msg)
                    }
                }
            }
        }


    }

    private fun showAddPaymentDialog() {
        dismissAddPaymentDialog()
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.add_payment_dialog, null)
        dialogBuilder.setView(dialogView)
        addPaymentDialog = dialogBuilder.create()
        addPaymentDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addPaymentDialog!!.setCancelable(false)
        addPaymentDialog!!.setCanceledOnTouchOutside(false)

        val okBtn = dialogView.findViewById<Button>(R.id.okBtn)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancelBtn)
        val inputAmountField = dialogView.findViewById<TextInputLayout>(R.id.inputAmountField)
        val dropdown = dialogView.findViewById<TextInputLayout>(R.id.dropdownField)
        val provider = dialogView.findViewById<TextInputLayout>(R.id.extraInputField1)
        val txnRef = dialogView.findViewById<TextInputLayout>(R.id.extraInputField2)

        val items = viewModel!!.dropdownPaymentTypes
        val adapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, items)
        (dropdown.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        okBtn.setOnClickListener {
            val amountInput:String = inputAmountField.editText?.text.toString()
            val paymentTypeInput:String = dropdown.editText?.text.toString()
            var providerInput = ""
            var txnRefInput = ""
            if(paymentTypeInput != Constants.CASH){
                providerInput = provider.editText?.text.toString()
                txnRefInput = txnRef.editText?.text.toString()
            }
            val allInputValid:Boolean

            val amountInputValid:Boolean
            val paymentTypeInputValid:Boolean
            val providerInputValid:Boolean
            val txnRefInputValid:Boolean

            if(amountInput.isBlank()){
                amountInputValid = false
                inputAmountField.error = "Amount can't be blank"
            }else if(amountInput.toInt() < 1){
                amountInputValid = false
                inputAmountField.error = "Amount can't be less than 1"
            }else{
                amountInputValid = true
            }

            if(paymentTypeInput.isBlank()){
                paymentTypeInputValid = false
                dropdown.error = "Please select a payment type"
            }else{
                paymentTypeInputValid = true
            }

            if(paymentTypeInput.isNotBlank() && (paymentTypeInput != Constants.CASH)){
                if(providerInput.isBlank()){
                    providerInputValid = false
                    provider.error = "Please select a provider"
                }else{
                    providerInputValid = true
                }

                if(txnRefInput.isBlank()){
                    txnRefInputValid = false
                    txnRef.error = "Please enter transaction reference"
                }else{
                    txnRefInputValid = true
                }
            }else{
                providerInputValid = true
                txnRefInputValid = true
            }

            allInputValid = amountInputValid && paymentTypeInputValid && providerInputValid && txnRefInputValid

            if(allInputValid){
                dismissAddPaymentDialog()
                viewModel!!.addPayment(
                    amount = amountInput.toInt(),
                    paymentType = paymentTypeInput,
                    provider = providerInput,
                    txnRef = txnRefInput
                )
            }

        }

        cancelBtn.setOnClickListener {
            dismissAddPaymentDialog()
        }

        inputAmountField.editText?.doOnTextChanged { _, _, _, _ ->
            inputAmountField.error = null
        }

        dropdown.editText?.doOnTextChanged { input, _, _, _ ->
            provider.editText?.setText("")
            provider.error = null
            txnRef.editText?.setText("")
            txnRef.error = null
            provider.isVisible = (input.toString() != Constants.CASH)
            txnRef.isVisible = (input.toString() != Constants.CASH)
            dropdown.error = null
        }

        provider.editText?.doOnTextChanged { _, _, _, _ ->
            provider.error = null
        }

        txnRef.editText?.doOnTextChanged { _, _, _, _ ->
            txnRef.error = null
        }

        provider.isVisible = false
        txnRef.isVisible = false

        addPaymentDialog?.show()
    }


    private fun initRv() {
        binding.paymentsRv.apply {
            layoutManager = GridLayoutManager(requireActivity(),1, RecyclerView.VERTICAL, false)
            this.adapter = this@PaymentListFragment.paymentsAdapter
        }
    }



    private fun paymentItemCloseIconClicked(payment: Payment, position: Int) {
        viewModel!!.deletePayment(payment = payment)
    }

    private fun dismissAddPaymentDialog(){
        addPaymentDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissAddPaymentDialog()
        showAlertLoader(false)
    }

}