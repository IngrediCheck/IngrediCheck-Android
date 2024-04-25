package com.sanket.ingredicheck.adapters

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.sanket.ingredicheck.databinding.DietaryItemBinding
import com.sanket.ingredicheck.model.Dietary


class DietaryAdapter(
    private val context: Context,
    private val itemList: List<Dietary>) : RecyclerView.Adapter<DietaryAdapter.ItemViewHolder>() {

    var clickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = DietaryItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.root.setOnClickListener {
            clickListener?.onClick()
        }
        item.annotatedText?.let {
            val spannable = parseAnnotatedText(it)
            holder.binding.text.text= spannable
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ItemViewHolder(val binding: DietaryItemBinding) : RecyclerView.ViewHolder(binding.root)

    public interface OnItemClickListener{
        fun onClick()
    }



    private fun parseAnnotatedText(text: String): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text)
        var start = text.indexOf("**")
        var end = text.indexOf("**", start + 2)
        while (start in 0..<end) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start + 2,
                end,
                0
            )

            // Remove the delimiters
            spannable.delete(end, end + 2)  // remove the closing **
            spannable.delete(start, start + 2)  // remove the opening **

            start = spannable.toString().indexOf("**", start)
            end = spannable.toString().indexOf("**", start + 2)
        }

        return spannable
    }
}
