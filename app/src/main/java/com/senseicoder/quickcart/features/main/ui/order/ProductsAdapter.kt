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
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.entity.product.Product
import com.senseicoder.quickcart.databinding.ProductLayoutBinding

class ProductsAdapter(
    val context: Context,
    private val listener: (id: String) -> Unit,
) : ListAdapter<Product, ProductsViewHolder>(
    ProductsDiffUtil()
) {

    private lateinit var binding: ProductLayoutBinding
    private var convertedPrices: MutableMap<String, Double> = mutableMapOf()
    private var currencyUnit: String = "EGP"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        binding = ProductLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val current = getItem(position)
        val convertedPrice = convertedPrices[current.id] ?: current.price.toDouble()

        holder.bind(current, convertedPrice, currencyUnit, listener)
    }

    fun updateCurrentCurrency(rate: Double, unit: String) {
        currencyUnit = unit
        currentList.forEach { product ->
            convertedPrices[product.id] = product.price.toDouble() * rate
            Log.e("price", "convertedPrices: ${convertedPrices[product.id]} ", )
            product.convertedPrice=convertedPrices[product.id]
            Log.e("price", "price: ${product.convertedPrice} ", )

        }
        notifyDataSetChanged()
    }
}

class ProductsViewHolder(val binding: ProductLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        product: Product,
        convertedPrice: Double,
        currencyUnit: String,
        listener: (id: String) -> Unit
    ) {
        binding.brandName.text = product.title
        binding.price.text = String.format("%.2f %s", convertedPrice, currencyUnit)
        Glide.with(binding.root.context).load(product.imageUrl)
            .apply(
                RequestOptions().override(200, 200)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
            )
            .into(binding.brandImage)
        binding.brandConstrainLayout.setOnClickListener {
            Log.i("TAG", "bind: Product Id${product.id}")
            listener.invoke(product.id)
        }
    }
}

class ProductsDiffUtil : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
