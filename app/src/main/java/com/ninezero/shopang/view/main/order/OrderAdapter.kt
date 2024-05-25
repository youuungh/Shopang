package com.ninezero.shopang.view.main.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Order
import com.ninezero.shopang.model.OrderEnums
import javax.inject.Inject

class OrderAdapter @Inject constructor(
): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orderList = mutableListOf<Order>()
    private lateinit var orderClickListener: OnOrderClickListener

    fun setOrderList(list: List<Order>, listener: OnOrderClickListener) {
        orderClickListener = listener
        orderList.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder =
        OrderViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_order,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)

        val statusText = when (order.orderStatus) {
            OrderEnums.PLACED -> holder.itemView.context.getString(R.string.order_status_placed)
            OrderEnums.CONFIRMED -> holder.itemView.context.getString(R.string.order_status_confirmed)
            OrderEnums.SHIPPED -> holder.itemView.context.getString(R.string.order_status_shipped)
            OrderEnums.DELIVERED -> holder.itemView.context.getString(R.string.order_status_delivered)
        }
        holder.binding.setVariable(BR.statusText, statusText)
    }

    override fun getItemCount(): Int = orderList.size

    inner class OrderViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) = with(binding) {
            setVariable(BR.order, order)
            setVariable(BR.onOrderClick, orderClickListener)
        }
    }

    interface OnOrderClickListener {
        fun onOrderClick(order: Order)
    }
}