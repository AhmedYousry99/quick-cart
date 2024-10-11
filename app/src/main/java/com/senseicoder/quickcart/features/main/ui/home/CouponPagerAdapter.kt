package com.senseicoder.quickcart.features.main.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.quickcart.core.model.CouponsForDisplay
import com.senseicoder.quickcart.core.model.PriceRule
import com.senseicoder.quickcart.databinding.ItemCouponBinding

class CouponPagerAdapter(
    private val coupons: List<CouponsForDisplay>,
    private val onCouponLongClicked: (PriceRule) -> Unit
) : RecyclerView.Adapter<CouponPagerAdapter.CouponViewHolder>() {

    // ViewHolder class to hold and manage item views
    inner class CouponViewHolder(private val binding: ItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coupon: CouponsForDisplay) {
            binding.imageCoupon.setImageResource(coupon.imageResId)
            binding.imageCoupon.setOnLongClickListener {
                onCouponLongClicked(coupon.priceRule)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(coupons[position])
    }

    override fun getItemCount(): Int {
        return coupons.size
    }
}
