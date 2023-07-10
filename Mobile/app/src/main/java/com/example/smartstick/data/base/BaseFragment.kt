package com.example.smartstick.data.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.smartstick.R

abstract class BaseFragment<VB:ViewBinding> : Fragment() {
    abstract val TAG : String
    private var _binding : VB ?= null
    protected val binding get() = _binding!!
    abstract fun getViewBinding(): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    abstract fun setUp()

    protected fun setUpAppBar(
        appbarVisibility: Boolean = false,
        title: String? = null,
    ) {
        activity?.findViewById<ConstraintLayout>(R.id.app_toolbar)?.let { toolbar ->
            toolbar.visibility = if (appbarVisibility) View.VISIBLE else View.GONE
        }

        activity?.findViewById<TextView>(R.id.text_pageTitle)?.let { pageTitle ->
            pageTitle.text = title ?: ""
        }
    }

    protected fun log(value: Any) {
        Log.e(TAG, value.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}