package com.senseicoder.quickcart.features.main.ui.product_details.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.model.graph_product.FeaturedImage
import com.senseicoder.quickcart.databinding.ItemCouponBinding
import com.senseicoder.quickcart.databinding.ProductDetailsPagerItemBinding


class ProductDetailsPagerAdapter(private var images: List<FeaturedImage>) : RecyclerView.Adapter<ProductDetailsPagerAdapter.CouponViewHolder>() {

    lateinit var binding: ProductDetailsPagerItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        binding = ProductDetailsPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(images[position].url)
    }

    fun updateList(images :List<FeaturedImage>){
        this.images = images
    }

    override fun getItemCount(): Int = images.size

    class CouponViewHolder(val binding: ProductDetailsPagerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        // Binds the coupon image resource to the ImageView
        fun bind(url: String) {
            Glide.with(itemView .context).load(url).placeholder(R.drawable.generic_placeholder).into(binding.imageCoupon)
        }
    }
}
