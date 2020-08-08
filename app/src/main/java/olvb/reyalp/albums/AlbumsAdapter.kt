package olvb.reyalp.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import olvb.reyalp.model.Album


class AlbumsAdapter : ListAdapter<Album, AlbumsAdapter.ViewHolder>(AlbumsAdapter.DiffCallback()) {

    var listener: OnInteractionListener? = null
    private var positionOfSelected = RecyclerView.NO_POSITION

    interface OnInteractionListener {

        fun onItemSelected(item: Album, position: Int)
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
            text = item.title
            isSelected = positionOfSelected == position
            setOnClickListener {
                onItemClicked(item, position)
            }
        }
    }

    private fun onItemClicked(item: Album, position: Int) {
        notifyItemChanged(positionOfSelected)
        positionOfSelected = position
        notifyItemChanged(position)

        listener?.onItemSelected(item, position)
    }

    class DiffCallback : DiffUtil.ItemCallback<Album>() {

        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.artist == newItem.artist
        }
    }
}