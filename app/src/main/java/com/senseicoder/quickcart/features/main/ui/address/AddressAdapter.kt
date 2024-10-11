package com.senseicoder.quickcart.features.main.ui.address

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.databinding.RowOfAddressBinding

class AddressAdapter(
    var list: List<AddressOfCustomer>,
    val listener: OnAddressClickListener
) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    lateinit var binding: RowOfAddressBinding
    companion object{
        private const val TAG = "AddressAdapter"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = RowOfAddressBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = list[position]
        holder.binding.apply {
            if (position == 0) {
                imgDefault.apply {
                    setImageResource(R.drawable.ic_favorite_filled)
                    setOnClickListener(null)
                }
                imgBtnDelete.apply {
                    visibility = android.view.View.GONE
                    setOnClickListener(null)
                }
            } else {
                imgDefault.apply {
                    setImageResource(R.drawable.ic_favorite_border)
                    setOnClickListener {
                        listener.onDefaultClick(current)
                    }
                }
                imgBtnDelete.apply {
                    visibility = android.view.View.VISIBLE
                    setOnClickListener {
                        listener.onDeleteClick(current)
                    }
                }
            }
            txtFirstNameAndSecond.text = String.format("${current.firstName} ${current.lastName}")
            txtNameOFCountryAndCity.text = String.format("${current.city}, ${current.country}")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<AddressOfCustomer>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    class ViewHolder(val binding: RowOfAddressBinding) : RecyclerView.ViewHolder(binding.root)
}