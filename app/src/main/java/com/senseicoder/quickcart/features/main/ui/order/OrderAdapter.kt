package com.senseicoder.quickcart.features.main.ui.order

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.ItemOrderBinding // Import the generated binding class
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class OrderAdapter(
    private val context: Context,
    private val listener: (index: Int) -> Unit
) : ListAdapter<Order, OrderViewHolder>(OrderDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val current = getItem(position)

        // Log current order details for debugging
        Log.d("OrderAdapter", "Binding order ID: ${current.id}, Total Price: ${current.totalPriceAmount}, Currency: EGP")

        // Bind data to the layout
     //   holder.binding.totalPrice.text = String.format("%.2f EGP - %d item", current.totalPriceAmount.toDouble(), current.products.size)

        holder.binding.totalPrice.text = formatTotalPrice(current.totalPriceAmount.toDouble())


        holder.binding.dateCreated.text = formatToYearMonthDayHourMinuteAmPm(current.processedAt)
        holder.binding.imageProductOrder.setImageResource(R.drawable.bag)

        // Handle item click
        holder.binding.orderConstrainLayout.setOnClickListener {
            listener.invoke(position)
            Log.i("TAG", "Order clicked: ${current.id}")
        }

        // Handle details click
        holder.binding.details.setOnClickListener {
            listener.invoke(position)
            Log.i("TAG", "Details clicked for order: ${current.id}")
        }
    }
    private fun formatTotalPrice(totalPrice: Double): String {
        val currencyData = SharedPrefsService.getCurrencyData() // Get current currency settings
        val code = currencyData.first ?: "EGP" // Default to EGP if no currency found
        val rate = currencyData.third?.toDouble() ?: 1.0 // Convert rate to Double, default to 1.0 if null

        // Convert the total price based on the current rate
        val newTotalPrice = totalPrice * rate
        return String.format("%.2f %s", newTotalPrice, code) // Format price to two decimal places and append currency code
    }
}

class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

class OrderDiffUtil : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}

fun formatToYearMonthDayHourMinuteAmPm(dateString: String): String {
    val zonedDateTime = ZonedDateTime.parse(dateString)
    val cairoTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Africa/Cairo"))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
    return cairoTime.format(formatter)
}
