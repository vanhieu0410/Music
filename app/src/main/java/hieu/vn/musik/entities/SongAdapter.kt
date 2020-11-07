package hieu.vn.musik.entities

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hieu.vn.musik.R
import kotlinx.android.synthetic.main.item_song.view.*

class SongAdapter : RecyclerView.Adapter<SongAdapter.ItemSongViewHolder>() {

    var currentPosition = -1
    private var listSong: ArrayList<Song>? = null
    private var songItemClickCallback: (song: Song?) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)

        return ItemSongViewHolder(view)
    }
    override fun onBindViewHolder(holder: ItemSongViewHolder, position: Int) {
        holder.bindDataSong(listSong?.get(position))
        if (currentPosition == position) {
            holder.setSelected()
        } else {
            holder.unSelected()
        }
    }

    override fun getItemCount(): Int = listSong?.size ?: 0

    fun setData(listSong: ArrayList<Song>) = apply {
        this.listSong = listSong
    }

    fun setSongItemClick(songItemClickCallback: (song: Song?) -> Unit) = apply {
        this.songItemClickCallback = songItemClickCallback
    }


    inner class ItemSongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var song: Song? = null

        init {
            itemView.setOnClickListener {
                song?.let { song ->
                    songItemClickCallback.invoke(song)
                }
                setSelected()
                notifyItemChanged(currentPosition)
                currentPosition = adapterPosition
            }
        }

        fun bindDataSong(song: Song?) {
            this.song = song
            if (song?.image != null) {
                itemView.imageSong.setImageURI(Uri.parse(song.image))
            } else {
                itemView.imageSong.setImageResource(R.drawable.song1)
            }
            itemView.nameSong.text = song?.name
            itemView.txtSongArtist.text = song?.artist
            itemView.txtRow.text = (adapterPosition + 1).toString()
        }

        fun unSelected() {
            itemView.nameSong.setTextColor(Color.BLACK)
            itemView.txtSongArtist.setTextColor(Color.BLACK)
        }

        fun setSelected() {
            itemView.nameSong.setTextColor(Color.BLUE)
            itemView.txtSongArtist.setTextColor(Color.BLUE)
        }

    }

}

