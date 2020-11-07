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
    var controller: ISongController? = null
    var recyclerViewLF: RecyclerView? = null
    var listSong: List<Song>? = null
    var listFavourAdapter: ListFavourAdapter? = null
    var listSongLike: MutableList<Song> = mutableListOf<Song>()

    private val itemClick: ListFavourAdapter.ItemClick = object: ListFavourAdapter.ItemClick {


        override fun onClick(song: Song?) {
            Log.d("xxx", "ItemClick in list Favour Fragment " + song?.name)

        }


    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = context as ISongController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListSongFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): ListSongFragment {
            val fragment = ListSongFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}