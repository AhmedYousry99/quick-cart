package com.senseicoder.quickcart.features.main.ui.favorite.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.toTwoDecimalPlaces
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.FavoriteItemBinding

class FavoritesDiffUtil : DiffUtil.ItemCallback<FavoriteDTO>() {
    override fun areItemsTheSame(oldItem: FavoriteDTO, newItem: FavoriteDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoriteDTO, newItem: FavoriteDTO): Boolean {
        return oldItem == newItem
    }
}

class FavoritesAdapter(
    private val deleteListener: (favorite: FavoriteDTO) -> Unit,
    private val onItemClickListener: (id: String) -> Unit,
) : ListAdapter<FavoriteDTO, FavoritesAdapter.FavoriteViewHolder>(FavoritesDiffUtil()) {

    /*private var lastPosition = -1
    private val handler = Handler(Looper.getMainLooper())*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = getItem(position)
        holder.bind(current)

        /*if (position > lastPosition) {
            holder.itemView.visibility = View.INVISIBLE
            handler.postDelayed({
                val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_in_animation)
                holder.itemView.startAnimation(animation)
                holder.itemView.visibility = View.VISIBLE
            }, (position * 170).toLong())
            lastPosition = position
        }*/

        holder.binding.deleteFavoriteItem.setOnClickListener {
            deleteListener.invoke(current)
        }
        holder.binding.cardViewFavoriteItem.setOnClickListener {
            onItemClickListener.invoke(current.id)
        }
    }

    class FavoriteViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: FavoriteDTO) {
            val priceMinimumEmpty = favorite.priceMinimum.isBlank()
            val priceMaximumEmpty = favorite.priceMaximum.isBlank()
            val currency = SharedPrefsService.getSharedPrefString(Constants.CURRENCY, Constants.CURRENCY_DEFAULT)
            val price = favorite.let {
                Log.d(TAG, "bind: minimum: ${it.priceMinimum}, maximum: ${it.priceMaximum}\n priceMinimumEmpty: $priceMinimumEmpty, priceMaximumEmpty: $priceMaximumEmpty")
                if ((!priceMinimumEmpty && !priceMaximumEmpty) && (it.priceMinimum != it.priceMaximum)) {
                    "${it.priceMinimum.toTwoDecimalPlaces()} - ${it.priceMaximum.toTwoDecimalPlaces()} $currency"
                }else{
                    if(!priceMinimumEmpty){
                        "${it.priceMinimum.toTwoDecimalPlaces()} $currency"
                    }else if (!priceMaximumEmpty){
                        "${it.priceMaximum.toTwoDecimalPlaces()} $currency"
                    }else{
                        "Unknown Price"
                    }
                }
            }
            binding.titleFavoriteItem.text = favorite.title
            binding.descriptionFavoritePrice.text = favorite.description
            binding.priceFavoriteItem.text = price
            Glide.with(binding.root.context).load(favorite.image.firstOrNull())
                .apply(RequestOptions().placeholder(R.drawable.generic_placeholder).error(R.drawable.generic_placeholder))
                .into(binding.imageViewFavoriteItem)
        }
    }


companion object{
    private const val TAG = "FavoriteAdapter"
}

}