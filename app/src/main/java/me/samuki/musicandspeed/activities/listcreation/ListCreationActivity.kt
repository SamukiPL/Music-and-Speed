package me.samuki.musicandspeed.activities.listcreation

import android.arch.lifecycle.Observer
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.fragments.CreationSettingsFragment
import me.samuki.musicandspeed.activities.listcreation.fragments.CreationSongsListFragment
import me.samuki.musicandspeed.activities.listcreation.fragments.CreationSummaryFragment
import me.samuki.musicandspeed.activities.listcreation.viewmodel.ListCreationViewModel
import me.samuki.musicandspeed.base.BaseActivity
import me.samuki.musicandspeed.utility.BundleConstants


class ListCreationActivity : BaseActivity(true) {

    private val vm by lazy {
        provideViewModel<ListCreationViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_creation)
    }

    override fun onStart() {
        super.onStart()
        vm.listName = intent.getStringExtra(BundleConstants.LIST_NAME)
        vm.getSongs()
        vm.currentFragment.observe(this, Observer { currentFragment ->
            currentFragment?.let {
                setFragment(it)
            }
        })
    }

    private fun setFragment(currentFragment: CreationFragments) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction().let { transaction ->
            actionButton.hide()
            when(currentFragment) {
                CreationFragments.LIST_FRAGMENT -> CreationSongsListFragment()
                CreationFragments.SUMMARY_FRAGMENT -> CreationSummaryFragment()
                CreationFragments.SETTINGS_FRAGMENT -> CreationSettingsFragment()
                CreationFragments.END_CREATION -> null
            }?.let {
                transaction.replace(R.id.creationFragment, it)
            } ?: finish()
            transaction.commit()
            actionButton.show()
        }
    }

    enum class CreationFragments{
        LIST_FRAGMENT, SUMMARY_FRAGMENT, SETTINGS_FRAGMENT, END_CREATION
    }
}