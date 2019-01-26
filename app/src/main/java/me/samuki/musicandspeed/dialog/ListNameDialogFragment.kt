package me.samuki.musicandspeed.dialog

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_list_name.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.main.MainActivity
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.base.BaseDialogFragment
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.utility.BundleConstants
import me.samuki.musicandspeed.helpers.KeyboardHelper


class ListNameDialogFragment : BaseDialogFragment() {

    private val vm by lazy {
        provideActivityViewModel<MainActivity, MainActivityViewModel>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_list_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        vm?.newListCanBeCreated?.observe(this, Observer {
            it?.let { canBeCreated ->
                if (canBeCreated) {
                    context?.let { context ->
                        KeyboardHelper.hideSoftKeyboard(context, view)
                    }
                    val intent = Intent(context, ListCreationActivity::class.java)
                    intent.putExtra(BundleConstants.LIST_NAME, listName.text.toString())
                    startActivity(intent)
                    dismiss()
                } else {
                    errorOnInput()
                }
            }
        })
    }

    private fun initViews() {
        initInput()
        initButtons()
    }

    private fun initInput() {
        listName?.let {
            it.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    it.setTextColor(ContextCompat.getColor(it.context, R.color.colorPrimary))
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            it.requestFocus()
            KeyboardHelper.showSoftKeyboard(it.context, it)
        }
    }

    private fun initButtons() {
        positive?.onClick {
            listName?.text?.toString()?.let {
                if (it.isNotEmpty()) {
                    vm?.setNewListName(it)
                }
            }
        }
        negative?.onClick {
            dismiss()
        }
    }

    private fun errorOnInput() {
        listName?.let {
            it.setTextColor(ContextCompat.getColor(it.context, R.color.errorRed))
            it.requestFocus()
            KeyboardHelper.showSoftKeyboard(it.context, it)
        }
    }

}