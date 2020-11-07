package hieu.vn.musik.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class PagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    private val fragmentList: MutableList<Fragment> =
        ArrayList()

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragmentList[0]
            1 -> fragmentList[1]
            else -> fragmentList[2]
        }
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun getFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}
