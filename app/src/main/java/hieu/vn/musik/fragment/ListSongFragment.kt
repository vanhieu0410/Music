package hieu.vn.musik.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hieu.vn.musik.ISongController
import hieu.vn.musik.entities.Song
import hieu.vn.musik.R

class ListSongFragment : Fragment() {
    private var controller: ISongController? = null
    private var recyclerViewLF: RecyclerView? = null
    var listFavourAdapter: ListFavourAdapter? = null
    var listSongLike: ArrayList<Song> = ArrayList()

    private val itemClick: ListFavourAdapter.ItemClick = object: ListFavourAdapter.ItemClick {
        override fun onClick(song: Song?) {
            Log.d("xxx", "ItemClick in list Favour Fragment " + song?.name)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = context as ISongController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_song_fragment, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        listFavourAdapter = ListFavourAdapter(listSongLike)
        listFavourAdapter?.setItemClick(itemClick)
        recyclerViewLF?.layoutManager = LinearLayoutManager(context)
        recyclerViewLF?.adapter = listFavourAdapter
    }

    private fun initView(view: View) {
        recyclerViewLF = view.findViewById(R.id.recyclerviewFavourList)
    }
}