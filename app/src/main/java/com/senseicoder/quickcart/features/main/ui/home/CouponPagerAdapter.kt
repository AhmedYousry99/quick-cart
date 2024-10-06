package com.senseicoder.quickcart.features.main.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.quickcart.R


class CouponPagerAdapter(private val coupons: List<Int>) : RecyclerView.Adapter<CouponPagerAdapter.CouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(coupons[position])
    }

    override fun getItemCount(): Int = coupons.size

    class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val couponImage: ImageView = itemView.findViewById(R.id.image_coupon)

        // Binds the coupon image resource to the ImageView
        fun bind(couponImageRes: Int) {
            couponImage.setImageResource(couponImageRes)
        }
    }
}
