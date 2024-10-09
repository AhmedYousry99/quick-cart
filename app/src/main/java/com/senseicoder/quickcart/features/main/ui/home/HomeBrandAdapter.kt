package com.senseicoder.quickcart.features.main.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.databinding.ItemBrandBinding

class HomeBrandAdapter(
    private val context: Context,
    private val onItemBrandClicked : OnItemBrandClicked
) : ListAdapter<DisplayBrand, HomeBrandAdapter.ViewHolder>(DiffUtilsHomeBrand()){

    private lateinit var binding : ItemBrandBinding

    inner class ViewHolder(var binding: ItemBrandBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemBrandBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.imageBrand.setImageFromUrl(getItem(position).image)

        //holder.binding.brandName.text = getItem(position).title

        holder.binding.cardBrand.setOnClickListener {
            onItemBrandClicked.brandClicked(getItem(position).title)
        }
    }

}

class DiffUtilsHomeBrand() : DiffUtil.ItemCallback<DisplayBrand>(){
    override fun areItemsTheSame(oldItem: DisplayBrand, newItem: DisplayBrand): Boolean {
        return oldItem === newItem
    }
    override fun areContentsTheSame(oldItem: DisplayBrand, newItem: DisplayBrand): Boolean {
        return oldItem == newItem
    }
}

interface OnItemBrandClicked {
    fun brandClicked(brand : String)
}
//fun ImageView.setImageFromUrl(url : String){
//    Glide.with(context)
//        .load(url)
//        .apply(
//            RequestOptions().override(
//                this.width,
//                this.height
//            )
//        )
//        .placeholder(R.drawable.ic_launcher_foreground)
//        .error(R.drawable.ic_launcher_background)
//        .into(this)
//}
fun ImageView.setImageFromUrl(url: String) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions()
                .override(150, 150)  // Set a fixed size for the images
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .fitCenter()
        )
        .into(this)
}