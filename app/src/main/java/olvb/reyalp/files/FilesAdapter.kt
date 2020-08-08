package olvb.reyalp.files

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FilesAdapter: ListAdapter<File, FilesAdapter.ViewHolder>(DiffCallback()) {

    var listener: OnInteractionListener? = null
    private var positionOfSelected = RecyclerView.NO_POSITION

    interface OnInteractionListener {

        fun onItemSelected(item: File, position: Int)
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

    private fun onItemClicked(item: File, position: Int) {
        notifyItemChanged(positionOfSelected)
        positionOfSelected = position
        notifyItemChanged(position)

        listener?.onItemSelected(item, position)
    }

    class DiffCallback : DiffUtil.ItemCallback<File>() {

        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.name == newItem.name
        }
    }
}