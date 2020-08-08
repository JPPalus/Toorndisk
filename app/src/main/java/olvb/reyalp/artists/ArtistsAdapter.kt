package olvb.reyalp.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import olvb.reyalp.model.Artist


class ArtistsAdapter : ListAdapter<Artist, ArtistsAdapter.ViewHolder>(ArtistsAdapter.DiffCallback()) {

    var listener: OnInteractionListener? = null
    private var positionOfSelected = RecyclerView.NO_POSITION

    interface OnInteractionListener {

        fun onItemSelected(item: Artist, position: Int)
    }

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.textView.apply {
            text = item.name
            isSelected = positionOfSelected == position
            setOnClickListener {
                onItemClicked(item, position)
            }
        }
    }

    private fun onItemClicked(item: Artist, position: Int) {
        notifyItemChanged(positionOfSelected)
        positionOfSelected = position
        notifyItemChanged(position)

        listener?.onItemSelected(item, position)
    }

    class DiffCallback : DiffUtil.ItemCallback<Artist>() {

        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name
        }
    }
}