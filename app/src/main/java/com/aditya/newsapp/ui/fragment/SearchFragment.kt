package com.aditya.newsapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aditya.newsapp.R
import com.aditya.newsapp.adapter.NewsAdapter
import com.aditya.newsapp.databinding.FragmentSearchNewsBinding
import com.aditya.newsapp.ui.activity.NewsActivity
import com.aditya.newsapp.utils.Constant
import com.aditya.newsapp.utils.Helper
import com.aditya.newsapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration


class SearchFragment : Fragment() {

    private lateinit var binding:FragmentSearchNewsBinding
    val viewModel by lazy {
        (activity as NewsActivity).viewModel
    }
    lateinit var newsAdapter: NewsAdapter
    val TAG="SearchFragment"
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
            val isTotalMoreThanVisible=totalItemCount>= Constant.QUERY_PAGE_SIZE

            val shouldPaginate=isNotLoadingAndNotLastPage&&isLastItem&&isNotBeginning&&isTotalMoreThanVisible&&isScrolling

            if(shouldPaginate){
                Helper.customLog("Pagination","$TAG -scrollListener - pagination called - page number ->${viewModel.searchingPageNumber} ")
                viewModel.getSearchNews(binding.etSearch.text.toString())
                isScrolling=false
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsAdapter= NewsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      binding= FragmentSearchNewsBinding.inflate(inflater,container,false)

       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        subscribeToObserver()
    }
    private fun subscribeToObserver() {
        viewModel.searchNews.observe(viewLifecycleOwner){response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let {data->
                        newsAdapter.submitList(data.articles.toList())
                        val totalPages=data.totalResults/ Constant.QUERY_PAGE_SIZE +2
                        isLastPage=viewModel.searchingPageNumber==totalPages
                    }

                }
                is Resource.Loading->{
                    showProgressBar()
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let {
                        Helper.customToast(requireContext() ,"An Error Occurred: $it")
                        Helper.customLog("SearchNews","$TAG - error - message->$it")
                    }
                }
            }
        }
    }

    private fun initUi() {
        binding.rvSearchNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(requireContext())
            addOnScrollListener(this@SearchFragment.scrollListener)
        }


        newsAdapter.onItemClick={
            val bundle=Bundle().apply {
                putParcelable(Constant.article,it)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job?=null
        binding.etSearch.addTextChangedListener {
            job?.cancel()
            job=lifecycleScope.launch {
                binding.etSearch.let {
                        if(it.text.toString().isNotEmpty()){
                            Helper.customLog("SearchText","$TAG - initUi - addTextChangedListener - api fire for text->${it.text} - length->${it.text.length}")
                            viewModel.searchNewsResponse=null
                            setConstraintAsDialogWay()
                            delay(Duration.ofMillis(Constant.Search_Delay_Time))
                            viewModel.getSearchNews(it.text.toString())
                        }
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.isGone=true
        isLoading=false
    }
    private fun showProgressBar() {
        binding.paginationProgressBar.isGone=false
        isLoading=true
    }
    private fun setConstraintAsPaginationWay() {
        val constraintSet= ConstraintSet()
        constraintSet.clone(binding.root)
        constraintSet.clear(binding.paginationProgressBar.id , ConstraintSet.TOP)
        constraintSet.applyTo(binding.root)
    }

    private fun setConstraintAsDialogWay(){
        val constraintSet= ConstraintSet()
        constraintSet.clone(binding.root)
        constraintSet.connect(binding.paginationProgressBar.id , ConstraintSet.TOP , ConstraintSet.PARENT_ID , ConstraintSet.TOP)
        constraintSet.applyTo(binding.root)
    }

}