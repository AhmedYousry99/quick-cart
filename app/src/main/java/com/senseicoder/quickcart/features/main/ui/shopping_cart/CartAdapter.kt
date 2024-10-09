package com.senseicoder.quickcart.features.main.ui.shopping_cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.databinding.RowOfDraftorderBinding

class CartAdapter(private val listener: OnCartItemClickListner) :
    ListAdapter<ProductOfCart, CartViewHolder>(FavoriteDiffUtilClass()) {

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
//            txtValueOfSize.text = currentPosition.variantTitle
            txtValueOfSize.visibility = ViewGroup.GONE
            txtPrice.text = currentPosition.variantPrice
            txtNameOfProduct.text = currentPosition.productTitle
            txtCount.text = String.format(currentPosition.quantity.toString())
            Glide.with(imgProduct).load(currentPosition.productImageUrl).placeholder( R.drawable.appicon).into(imgProduct)
        }
    }

    companion object {
        private const val TAG = "RecyclerView.FavoriteAdapter"
    }

    private fun setUpListener(binding: RowOfDraftorderBinding, position: Int) {
        binding.apply {
            val currentPosition = getItem(position)
            root.setOnClickListener {
                listener.onProductClick(currentPosition)
            }
            imgBtnAddOne.setOnClickListener {
                currentPosition.quantity += 1
                binding.txtCount.text = String.format(currentPosition.quantity.toString())
                notifyItemChanged(position)
                listener.onPlusClick(currentPosition)
            }
            imgBtnMinusOne.setOnClickListener {
                if (currentPosition.quantity > 1) {
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

class FavoriteDiffUtilClass : DiffUtil.ItemCallback<ProductOfCart>() {
    override fun areItemsTheSame(oldItem: ProductOfCart, newItem: ProductOfCart): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ProductOfCart, newItem: ProductOfCart): Boolean {
        return oldItem.quantity == newItem.quantity
    }
}