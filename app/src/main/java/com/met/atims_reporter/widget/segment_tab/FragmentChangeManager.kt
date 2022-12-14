package com.met.atims_reporter.widget.segment_tab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.util.*

class FragmentChangeManager(
    fm: FragmentManager,
    containerViewId: Int,
    fragments: ArrayList<Fragment>
) {
    private val mFragmentManager: FragmentManager
    private val mContainerViewId: Int
    /** Fragment切换数组  */
    private val mFragments: ArrayList<Fragment>
    /** 当前选中的Tab  */
    var currentTab = 0
        private set

    /** 初始化fragments  */
    private fun initFragments() {
        for (fragment in mFragments) {
            mFragmentManager.beginTransaction().add(mContainerViewId, fragment).hide(fragment)
                .commit()
        }
        setFragments(0)
    }

    /** 界面切换控制  */
    fun setFragments(index: Int) {
        for (i in mFragments.indices) {
            val ft: FragmentTransaction = mFragmentManager.beginTransaction()
            val fragment: Fragment = mFragments[i]
            if (i == index) {
                ft.show(fragment)
            } else {
                ft.hide(fragment)
            }
            ft.commit()
        }
        currentTab = index
    }

    val currentFragment: Fragment
        get() = mFragments[currentTab]

    init {
        mFragmentManager = fm
        mContainerViewId = containerViewId
        mFragments = fragments
        initFragments()
    }
}