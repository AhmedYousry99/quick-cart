package com.senseicoder.quickcart.features.main.ui.order

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.databinding.ItemOrderBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class OrderAdapter(
    val context: Context,
    private val listener: (index: Int) -> Unit,
) : ListAdapter<Order, OrdersViewHolder>(
    OrdersDiffUtil()
) {

    private lateinit var binding: ItemOrderBinding
    private var convertedPrices: MutableMap<String, Double> = mutableMapOf()
    private var currencyUnit: String = "EGP"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val current = getItem(position)
        val convertedTotalPrice = convertedPrices["totalPrice${current.id}"] ?: current.totalPriceAmount.toDouble()
        val convertedSubTotalPrice = convertedPrices["subTotalPrice${current.id}"] ?: current.subTotalPriceAmount.toDouble()

        binding.totalPrice.text = String.format("%.2f %s - %d items", convertedTotalPrice, currencyUnit, current.products.size)
        binding.dateCreated.text = formatToYearMonthDayHourMinuteAmPm(current.processedAt)


        binding.orderConstrainLayout.setOnClickListener {
            listener.invoke(position)
            Log.i("TAG", "onBindViewHolder: id current " + current.id)
        }

        binding.details.setOnClickListener {
            listener.invoke(position)
            Log.i("TAG", "onBindViewHolder: id current " + current.id)
        }
    }

    fun updateCurrentCurrency(rate: Double, unit: String) {
        currencyUnit = unit
        currentList.forEach { order ->
            convertedPrices["totalPrice${order.id}"] = order.totalPriceAmount.toDouble() * rate
            convertedPrices["subTotalPrice${order.id}"] = order.subTotalPriceAmount.toDouble() * rate
        }
        notifyDataSetChanged()
    }
}

class OrdersViewHolder(val layout: ItemOrderBinding) : RecyclerView.ViewHolder(layout.root)
class OrdersDiffUtil : DiffUtil.ItemCallback<Order>() {
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
