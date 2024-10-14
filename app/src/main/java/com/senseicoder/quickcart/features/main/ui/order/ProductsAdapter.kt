package com.senseicoder.quickcart.features.main.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.entity.product.Product
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.ProductLayoutBinding

class ProductsAdapter(
    val context: Context,
    private val listener: (id: String) -> Unit,
) : ListAdapter<Product, ProductsAdapter.ViewHolder>(ProductsDiffUtil()) {

    private lateinit var binding: ProductLayoutBinding
    private var convertedPrices: MutableMap<String, Double> = mutableMapOf()
    private var currencyUnit: String = "EGP"

    fun updateCurrency() {
        // Fetch user preference currency data
        val currencyData = SharedPrefsService.getCurrencyData()
        val rate = currencyData.third?.toDouble() ?: 1.0
        currencyUnit = currencyData.first ?: "EGP" // Default to EGP if no currency unit found

        // Update prices based on the current currency rate
        currentList.forEach { product ->
            convertedPrices[product.id] = product.price.toDouble() * rate
            product.convertedPrice = convertedPrices[product.id] // Update converted price
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ProductLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        // Use the converted price if available, otherwise use original price
        val convertedPrice = convertedPrices[current.id] ?: current.price.toDouble()

        // Bind product with the updated price and currency
        holder.bind(current, convertedPrice, currencyUnit, listener)
    }

    inner class ViewHolder(private val binding: ProductLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            product: Product,
            convertedPrice: Double,
            currencyUnit: String,
            listener: (id: String) -> Unit
        ) {
            binding.brandName.text = product.title
            binding.price.setPrice(convertedPrice, currencyUnit) // Use helper method to set price
            binding.brandImage.setImageFromUrl(product.imageUrl)
            binding.brandConstrainLayout.setOnClickListener {
                listener.invoke(product.id)
            }
        }
    }
}

// Helper method to set price text dynamically in the TextView
@SuppressLint("DefaultLocale")
fun TextView.setPrice(price: Double, currencyUnit: String) {
    text = String.format("%.2f %s", price, currencyUnit) // Format price with currency unit
}

// Helper method to set image from URL using Glide
fun ImageView.setImageFromUrl(url: String) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions().override(this.width, this.height)
        )
        .placeholder(R.drawable.ic_launcher_foreground)
        .error(R.drawable.ic_launcher_background)
        .into(this)
}

class ProductsDiffUtil : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
