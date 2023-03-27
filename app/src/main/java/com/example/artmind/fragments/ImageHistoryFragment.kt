package com.example.artmind.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artmind.R
import com.example.artmind.adapters.ImageHistoryAdapter
import com.example.artmind.viewmodels.ImageHistoryViewModel

class ImageHistoryFragment : Fragment() {
    private lateinit var viewModel: ImageHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            viewModel = it.getSerializable(ARG_VIEW_MODEL) as ImageHistoryViewModel
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_image_history, container, false)

        // Initialize RecyclerView
        val adapter = ImageHistoryAdapter(ArrayList(),requireContext())
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.fragment_image_history_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = adapter


        val progressBar = rootView.findViewById<ProgressBar>(R.id.fragment_image_history_progress_bar)
        progressBar.visibility = View.VISIBLE


        // Observe LiveData and update adapter when data changes
        viewModel.imagesLiveData.observe(viewLifecycleOwner) { images ->
            adapter.updateData(images)
            progressBar.visibility = View.GONE
        }

        val backButton = rootView.findViewById<ImageView>(R.id.fragment_image_history_back_arrow)
        backButton.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        return rootView
    }

    companion object {
        private const val ARG_VIEW_MODEL = "view_model"

        fun newInstance(viewModel: ImageHistoryViewModel) = ImageHistoryFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_VIEW_MODEL, viewModel)
            }
        }
    }

    private fun onBackPressed() {
        val childViewContainer = activity?.findViewById<CoordinatorLayout>(R.id.activity_main_coordinator_layout)
        childViewContainer?.visibility = View.VISIBLE
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }
    override fun onDestroy() {
        super.onDestroy()
        val childViewContainer = activity?.findViewById<CoordinatorLayout>(R.id.activity_main_coordinator_layout)
        childViewContainer?.visibility = View.VISIBLE
    }

}

