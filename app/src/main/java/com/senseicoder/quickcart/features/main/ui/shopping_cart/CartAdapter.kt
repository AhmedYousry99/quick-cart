package com.senseicoder.quickcart.features.main.ui.shopping_cart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.updateCurrency
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.RowOfDraftorderBinding

class CartAdapter(private val listener: OnCartItemClickListener) :
    ListAdapter<ProductOfCart, CartViewHolder>(CartDiffUtilClass()) {
private val currencyData = SharedPrefsService.getCurrencyData()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = RowOfDraftorderBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentPosition: ProductOfCart = getItem(position)
        holder.binding.apply {
            setUpListener(this, position)
            //TODO ADD VALUE OF SIZE AND SHOW IT
            Log.d(TAG, "onBindViewHolder: ${currentPosition.size}")
            txtValueOfSize.text = currentPosition.size
            txtValueOfSize.text = String.format(currentPosition.size.toString())
//            txtValueOfSize.visibility = ViewGroup.GONE
            txtPrice.text = String.format("${currentPosition.variantPrice?.updateCurrency(currencyData.third).toString()} ${currencyData.second}")
            txtNameOfProduct.text = currentPosition.productTitle
            txtCount.text = String.format(currentPosition.quantity.toString())
            valueOfAvailable.text = String.format("In Stoke: ${currentPosition.stoke}")
            Glide.with(imgProduct).load(currentPosition.productImageUrl)
                .placeholder(R.drawable.appicon).into(imgProduct)
        }
    }

    companion object {
        private const val TAG = "RecyclerView.FavoriteAdapter"
    }

    private fun setUpListener(binding: RowOfDraftorderBinding, position: Int) {
        binding.apply {
            val currentPosition = getItem(position)
            val stoke = currentPosition.stoke?.toInt()
            root.setOnClickListener {
                listener.onProductClick(currentPosition)
            }
            imgBtnAddOne.setOnClickListener {
                if (stoke != null) {
                    if (stoke > currentPosition.quantity) {
                        imgBtnMinusOne.isEnabled=true
                        currentPosition.quantity += 1
                        binding.txtCount.text = String.format(currentPosition.quantity.toString())
                        notifyItemChanged(position)
                        listener.onPlusClick(currentPosition)
                    }else if(stoke == currentPosition.quantity){
                        imgBtnAddOne.isEnabled = false
                    }
                }

            }
            imgBtnMinusOne.setOnClickListener {
                if (currentPosition.quantity > 1) {
                    imgBtnAddOne.isEnabled=true
                    currentPosition.quantity -= 1
                    binding.txtCount.text = String.format(currentPosition.quantity.toString())
                    notifyItemChanged(position)
                    listener.onMinusClick(currentPosition)
                } else
                    listener.onDeleteClick(currentPosition)
            }
            imgBtnDeleteFromChart.setOnClickListener {
                listener.onDeleteClick(currentPosition)
            }
        }
    }
}


class CartViewHolder(val binding: RowOfDraftorderBinding) : RecyclerView.ViewHolder(binding.root)

class CartDiffUtilClass : DiffUtil.ItemCallback<ProductOfCart>() {
    override fun areItemsTheSame(oldItem: ProductOfCart, newItem: ProductOfCart): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ProductOfCart, newItem: ProductOfCart): Boolean {
        return oldItem.quantity == newItem.quantity
    }
}