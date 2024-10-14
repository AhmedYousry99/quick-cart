package com.senseicoder.quickcart.features.main.ui.product_details.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.model.ReviewDTO
import com.senseicoder.quickcart.databinding.RowOfReviewBinding

class ReviewAdapter(val list: List<ReviewDTO>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    lateinit var binding: RowOfReviewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = RowOfReviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
        val current = list[position]
        holder.binding.apply {
            txtDesc.text = current.description
            txtName.text = current.name
            ratingBarProductDetails.rating = current.rating.toFloat()
            img.setImageResource(imageList[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: RowOfReviewBinding) : RecyclerView.ViewHolder(binding.root)

    private val imageList: List<Int> by lazy {
        listOf(
            R.drawable.persona,
            R.drawable.personb,
            R.drawable.personc,
            R.drawable.persond,
            R.drawable.persone,
            R.drawable.personf,
            R.drawable.persong,
            R.drawable.personh,
            R.drawable.personi,
            R.drawable.j,

            )
    }
}