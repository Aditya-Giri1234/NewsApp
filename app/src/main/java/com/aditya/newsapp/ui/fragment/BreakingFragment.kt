package com.aditya.newsapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aditya.newsapp.R
import com.aditya.newsapp.adapter.NewsAdapter
import com.aditya.newsapp.databinding.FragmentBreakingNewsBinding
import com.aditya.newsapp.ui.activity.NewsActivity
import com.aditya.newsapp.utils.Constant
import com.aditya.newsapp.utils.Constant.QUERY_PAGE_SIZE
import com.aditya.newsapp.utils.Helper
import com.aditya.newsapp.utils.Resource


class BreakingFragment : Fragment() {

    lateinit var binding: FragmentBreakingNewsBinding
    val TAG = "BreakingFragment"
    val viewModel by lazy {
        (activity as NewsActivity).viewModel
    }
    lateinit var newsAdapter: NewsAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
                setConstraintAsPaginationWay()
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition=layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount=layoutManager.childCount
            val totalItemCount=layoutManager.itemCount

            val isNotLoadingAndNotLastPage=!isLoading&&!isLastPage
            val isLastItem=firstVisibleItemPosition+visibleItemCount>=totalItemCount
            val isNotBeginning= firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible=totalItemCount>=QUERY_PAGE_SIZE

            val shouldPaginate=isNotLoadingAndNotLastPage&&isLastItem&&isNotBeginning&&isTotalMoreThanVisible&&isScrolling

            Helper.customLog("Pagination","$TAG -scrollListener - firstVisibleItemPosition->$firstVisibleItemPosition - visibleItemCount->$visibleItemCount - totalItemCount->$totalItemCount - isLoading->$isLoading - isLastPage->$isLastPage - isNotLoadingAndNotLastPage->$isNotLoadingAndNotLastPage  - isLastItem->$isLastItem - isNotBeginning->$isNotBeginning - isTotalMoreThanVisible->$isTotalMoreThanVisible - shouldPaginate->$shouldPaginate")

            if(shouldPaginate){
                Helper.customLog("Pagination","$TAG -scrollListener - pagination called - page number ->${viewModel.breakingPageNumber} ")
                viewModel.getBreakingNews("in")
                isScrolling=false
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsAdapter = NewsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { data ->
                        newsAdapter.submitList(data.articles.toList())
                        val totalPages=data.totalResults/ QUERY_PAGE_SIZE +2
                        isLastPage=viewModel.breakingPageNumber==totalPages
                    }

                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Helper.customToast(requireContext() ,"An Error Occurred: $it")
                        Helper.customLog("BreakingNews", "$TAG - error - message->$it")
                    }
                }
            }
        }
    }


    private fun initUi() {
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@BreakingFragment.scrollListener)
        }
//        viewModel.getBreakingNews("in") // this is one time so that set into in view model not here
        setConstraintAsDialogWay()
        newsAdapter.onItemClick = {
            val bundle = Bundle().apply {
                putParcelable(Constant.article, it)
            }
            findNavController().navigate(
                R.id.action_breakingFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun hideProgressBar() {
        isLoading=false
        binding.paginationProgressBar.isGone = true
    }

    private fun showProgressBar() {
        isLoading=true
        binding.paginationProgressBar.isGone = false
    }
    private fun setConstraintAsPaginationWay() {
        val constraintSet=ConstraintSet()
        constraintSet.clone(binding.root)
        constraintSet.clear(binding.paginationProgressBar.id , ConstraintSet.TOP)
        constraintSet.applyTo(binding.root)
    }

    private fun setConstraintAsDialogWay(){
        val constraintSet=ConstraintSet()
        constraintSet.clone(binding.root)
        constraintSet.connect(binding.paginationProgressBar.id , ConstraintSet.TOP , ConstraintSet.PARENT_ID , ConstraintSet.TOP)
        constraintSet.applyTo(binding.root)
    }

}