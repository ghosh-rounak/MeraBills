package com.rounak.merabills.ui.payment_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rounak.merabills.R
import com.rounak.merabills.data.db.entities.Payment
import com.rounak.merabills.databinding.RvItemPaymentBinding
import javax.inject.Inject

class PaymentsAdapter @Inject constructor(
) : RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder>() {

    private lateinit var ctx: Context
    private lateinit var deleteClickListener:(Payment, Int)->Unit

    internal fun setOnItemClickListener(
        deleteClickListener:(Payment, Int)->Unit
    ){
        this.deleteClickListener = deleteClickListener
    }

    internal fun setContext(ctx: Context){
        this.ctx = ctx
    }

    private val differCallback = object : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : RvItemPaymentBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.rv_item_payment,parent,false)
        return PaymentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(
            differ.currentList[position],
            deleteClickListener
        )
    }




    inner class PaymentViewHolder(
        private val binding: RvItemPaymentBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(
            payment: Payment,
            deleteClickListener:(Payment, Int)->Unit
        ){
            binding.payment = payment
            binding.chip.text = "${payment.paymentType}: ${ctx.getString(R.string.rupee_sign)}${payment.amount}"

            binding.chip.setOnCloseIconClickListener {
                deleteClickListener(payment,bindingAdapterPosition)
            }

            binding.executePendingBindings()
        }
    }

}

