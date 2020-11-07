package hieu.vn.musik.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hieu.vn.musik.entities.Song
import hieu.vn.musik.R

class ListFavourAdapter(songList: List<Song>) :
    RecyclerView.Adapter<ListFavourAdapter.ItemViewHolder>() {
    var listSong: List<Song>
    private var itemClick: ItemClick? = null
    var currentPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.lf_item_song, parent, false)
        return ItemViewHolder(view, itemClick!!)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        holder.binDataSong(listSong[position])
        if (currentPosition == position) {
            holder.setSelectedCb()
        } else {
            holder.setUnSelectedCb()
        }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    fun setItemClick(itemClick: ItemClick?) {
        this.itemClick = itemClick
    }

    inner class ItemViewHolder(
        itemView: View,
        itemClick: ItemClick
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var song: Song? = null
        private val txtNameSongLF: TextView
        private val txtArtistLF: TextView
        private val iconMenu: ImageView
        private val iconDelete: ImageView
        fun binDataSong(song: Song) {
            this.song = song
            txtNameSongLF.setText(song.name)
            txtArtistLF.setText(song.artist)
        }

        fun setSelectedCb() {
        }

        fun setUnSelectedCb() {
        }

        init {
            txtNameSongLF = itemView.findViewById(R.id.txtNameSongLF)
            txtArtistLF = itemView.findViewById(R.id.txtArtistLF)
            iconMenu = itemView.findViewById(R.id.iconMenuLF)
            iconDelete = itemView.findViewById(R.id.imgDelete)
            itemView.setOnClickListener {
                Log.d("xxx", "oldPositionLSF: $currentPosition")
                itemClick.onClick(song)
                setSelectedCb()
                notifyItemChanged(currentPosition)
                currentPosition = adapterPosition
                Log.d("xxx", "currentPositionLSF: $currentPosition")
            }
        }
    }

    interface ItemClick {
        fun onClick(song: Song?)
    }

    init {
        listSong = songList
    }
}
