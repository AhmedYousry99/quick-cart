package com.senseicoder.quickcart.features.main.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.senseicoder.quickcart.CurrencyManager
import com.senseicoder.quickcart.CurrencySharedPref
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.databinding.ProductItemBinding

class CategoryAdapter(
    private val onItemProductClicked: OnItemProductClicked
) : ListAdapter<DisplayProduct, CategoryAdapter.ViewHolder>(DiffUtilsCategory()) {

    private lateinit var binding: ProductItemBinding

    inner class ViewHolder(var binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ProductItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.imageProduct.setImageFromUrl(getItem(position).image)

        holder.binding.nameProduct.title(getItem(position).title)

        holder.binding.priceProduct.setPrice(holder.itemView.context, getItem(position).price.toDouble())

        holder.binding.cardProduct.setOnClickListener {
            onItemProductClicked.productClicked(getItem(position).id)
        }
    }

}

class DiffUtilsCategory() : DiffUtil.ItemCallback<DisplayProduct>() {

    override fun areItemsTheSame(oldItem: DisplayProduct, newItem: DisplayProduct): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DisplayProduct, newItem: DisplayProduct): Boolean {
        return oldItem == newItem
    }

}

interface OnItemProductClicked {
    fun productClicked(id: Long)
}

fun ImageView.setImageFromUrl(url : String){
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions().override(
                this.width,
                this.height
            )
        )
        .placeholder(R.drawable.ic_launcher_foreground)
        .error(R.drawable.ic_launcher_background)
        .into(this)
}

fun TextView.setPrice(context: Context, price: Double) {
    val cManager = CurrencyManager(CurrencySharedPref.sharedPreferences)
    val pair = cManager.getCurrencyPair()  // Fetch the currency code and rate
    val code = pair.first  // Currency code (e.g., EGP)
    val rate = pair.second  // Exchange rate

    // Convert the product price based on the current rate
    val newPrice = price * rate
    text = buildString {
        append(String.format("%.2f", newPrice))  // Format price to two decimal places
        append(" $code")  // Append the currency code (as "EGP")
    }
}

//to get title of product
fun TextView.title(titleProduct : String){
    val titleList = titleProduct.split("|")

    val title = if (titleList.size == 1) {
        titleList[0]
    }else {
        titleList[1]
    }

    text = buildString {
        append(title)
    }

}
