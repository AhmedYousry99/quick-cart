package com.senseicoder.quickcart.features.main.ui.search.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.SearchFooterLayoutBinding
import com.senseicoder.quickcart.databinding.SearchItemBinding

class FavoritesDiffUtil : DiffUtil.ItemCallback<ProductDTO>() {
    override fun areItemsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
        return oldItem == newItem
    }
}

class SearchAdapter(val function: (id: String) -> Unit) : ListAdapter<ProductDTO, RecyclerView.ViewHolder>(
    FavoritesDiffUtil()
) {

    private var isLoadingAdded = false

    // ViewHolder for the regular items
    inner class MyViewHolder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    // ViewHolder for the footer (loading indicator)
    inner class FooterViewHolder(val binding: SearchFooterLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        // Check if this is the footer view position
        return if (position == itemCount - 1 && isLoadingAdded) FOOTER_VIEW_TYPE else ITEM_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == ITEM_VIEW_TYPE) {
            // Inflate the normal item layout
            val binding = SearchItemBinding.inflate(layoutInflater, parent, false)
            MyViewHolder(binding)
        } else {
            // Inflate the footer layout (for loading indicator)
            val footerBinding = SearchFooterLayoutBinding.inflate(layoutInflater, parent, false)
            FooterViewHolder(footerBinding)
        }
    }

    override fun getItemCount(): Int {
        // Return data size + 1 if loading footer is added
        Log.d(TAG, "Item count: ${super.getItemCount() + if (isLoadingAdded) 1 else 0}")
        return super.getItemCount() + if (isLoadingAdded) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val product = getItem(position) // Use getItem() for ListAdapter
            holder.binding.apply {
                titleSearchItem.text = product.title
                priceSearchItem.text = priceWithCurrency(product)
                sizesSearchItem.text = "${sizesSearchItem.context.getString(R.string.available_sizes)}${product.variants.joinToString { variant -> variant.selectedOptions.filter { it.name == "Size" }.joinToString (prefix = " "){ it.value } }}"
                Glide.with(holder.binding.root.context).load(product.images.firstOrNull()?.url).placeholder(R.drawable.generic_placeholder).into(imageViewSearchItem)
                cardViewSearchItem.setOnClickListener {
                    function.invoke(product.id)
                }
            }
        }
        // No need to bind anything for FooterViewHolder, as it only shows the loading indicator
    }

    private fun priceWithCurrency(product: ProductDTO): String{
        val currency = SharedPrefsService.getSharedPrefString(Constants.CURRENCY, Constants.CURRENCY_DEFAULT)
        return product.priceRange.let {
            val priceMinimum = it.minVariantPrice.amount
            val priceMaximum = it.maxVariantPrice.amount
            val priceMinimumEmpty = priceMinimum.isBlank()
            val priceMaximumEmpty = priceMaximum.isBlank()
            Log.d(TAG, "bind: minimum: ${priceMinimum}, maximum: ${priceMaximum}\n priceMinimumEmpty: $priceMinimumEmpty, priceMaximumEmpty: $priceMaximumEmpty")
            if ((!priceMinimumEmpty && !priceMaximumEmpty) && (priceMinimum != priceMaximum)) {
                "${priceMinimum} - ${priceMaximum} $currency"
            }else{
                if(!priceMinimumEmpty){
                    "${priceMinimum} $currency"
                }else if (!priceMaximumEmpty){
                    "${priceMaximum} $currency"
                }else{
                    "Unknown Price"
                }
            }
        }
    }
    // Function to add a loading footer
    fun addLoadingFooter() {
        if (!isLoadingAdded) {
            isLoadingAdded = true
            notifyItemInserted(itemCount) // Add footer at the end of the list
        }
    }

    // Function to remove the loading footer
    fun removeLoadingFooter() {
        if (isLoadingAdded) {
            isLoadingAdded = false
            notifyItemRemoved(itemCount) // Remove footer when loading is complete
        }
    }

    fun updateData(newList: List<ProductDTO>) {
        removeLoadingFooter()
        submitList(currentList + newList) // Use submitList to update the list
    }

    companion object {
        private const val TAG = "MyAdapter"
        private const val ITEM_VIEW_TYPE = 1
        private const val FOOTER_VIEW_TYPE = 2
    }
}