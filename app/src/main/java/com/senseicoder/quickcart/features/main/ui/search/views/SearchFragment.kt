package com.senseicoder.quickcart.features.main.ui.search.views

import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentSearchBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.features.main.ui.search.adapters.SearchAdapter
import com.senseicoder.quickcart.features.main.ui.search.viewmodel.SearchViewModel
import com.senseicoder.quickcart.features.main.ui.search.viewmodel.SearchViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private val searchQuery = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val maxPrice = MutableStateFlow<Int>(10000)

    private lateinit var suggestionsAdapter: CursorAdapter

    private lateinit var searchAdapter: SearchAdapter
    private var loading = false
    private var pastVisibileItem = 0
    private var visibileItemCount = 0
    private var totalItemCount = 0
    private var mLayoutManager: LinearLayoutManager? = null

    private var clickedSuggestion: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SearchViewModelFactory(
            ProductsRepo.getInstance()
        )
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        setupRecycler()
        setupSearchView()
        setupSuggestionsAdapter()
        subscribeToObservables()
    }

    private fun showListEmpty() {
        binding.apply {
            loadingSearchGroup.visibility = View.GONE
            emptyListSearchGroup.visibility = View.VISIBLE
            noInternetSearchGroup.visibility = View.GONE
        }
    }

    private fun subscribeToObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.searchResults.collect {
                    when (it) {
                        ApiState.Init -> {
                            Log.d(TAG, "subscribeToObservables: init")
                        }
                        ApiState.Loading -> {
                            showListLoading()
                        }
                        is ApiState.Success -> {
                            Log.d(TAG, "subscribeToObservables: working: ${it.data}")
                            if(it.data.isEmpty())
                                showListEmpty()
                            else{
                                showListSuccess()
                                searchAdapter.submitList(it.data)
                            }
                        }
                        is ApiState.Failure -> {
                            showSnackbar(it.msg)
                        }
                    }
                }
            }
        }
        setupQueryObservable()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.suggestionResults.collect {
                    when (it) {
                        ApiState.Init -> {

                        }
                        ApiState.Loading -> {
                            suggestionsAdapter.cursor?.close()
                        }

                        is ApiState.Success -> {
                            updateSuggestions(it.data)
                        }

                        is ApiState.Failure -> {
                            showSnackbar(getString(R.string.suggestion_list_error))
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.listResult.collect {
                    when (it) {
                        ApiState.Init -> {
                            // Handle initial state
                        }

                        ApiState.Loading -> {
                            searchAdapter.addLoadingFooter()
                        }

                        is ApiState.Success -> {
                            searchAdapter.updateData(it.data)
                            loading = false
                        }

                        is ApiState.Failure -> {
                            loading = false
                            searchAdapter.removeLoadingFooter()
                        }
                    }
                }
            }
        }
    }

    private fun showListSuccess() {
        binding.apply {
            loadingSearchGroup.visibility = View.GONE
            emptyListSearchGroup.visibility = View.GONE
            noInternetSearchGroup.visibility = View.GONE
            recyclerViewSearch.visibility = View.VISIBLE
        }
    }

    private fun setupRecycler() {
        searchAdapter = SearchAdapter() {
            if(NetworkUtils.isConnected(requireContext())){
                ViewModelProvider(requireActivity())[MainActivityViewModel::class.java].setCurrentProductId(
                    it
                )
                findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment)
            }
            else
                showSnackbar(getString(R.string.no_internet_connection))
        }
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.apply {
            layoutManager = mLayoutManager
            adapter = searchAdapter
        }
        setupPagination()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(clickedSuggestion == newText && viewModel.searchResults.value is ApiState.Success){
                    searchAdapter.submitList((viewModel.searchResults.value as ApiState.Success).data.filter { it.title == newText })
                }else
                {   binding.noInternetSearchGroup.visibility = View.GONE
                    searchQuery.tryEmit(newText ?: "")
                }
                return true
            }
        })
        binding.searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = suggestionsAdapter.cursor
                cursor.moveToPosition(position)
                clickedSuggestion = cursor.getString(cursor.getColumnIndexOrThrow("suggestion"))
                binding.searchView.setQuery(clickedSuggestion, true)
                return true
            }
        })
    }

    private fun setupSuggestionsAdapter() {
        val from = arrayOf("suggestion")
        val to = intArrayOf(android.R.id.text1)
        suggestionsAdapter = SimpleCursorAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        binding.searchView.suggestionsAdapter = suggestionsAdapter
    }

    @OptIn(FlowPreview::class)
    private fun setupQueryObservable() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.CREATED) {
                searchQuery.debounce(500)
                    .collect { query ->
                        if(NetworkUtils.isConnected(requireContext()))
                        {
                            binding.noInternetSearchGroup.visibility = View.GONE
                            if (query.length > 2 && query.isNotBlank()) {
                                viewModel.searchProducts(query)
                            }
                        }
                        else
                            showNoInternetGroup()
//                                showSnackbar(getString(R.string.no_internet_connection))

                    }
            }
        }
    }

    private fun setupPagination() {
        binding.recyclerViewSearch.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibileItemCount = mLayoutManager!!.childCount
                    totalItemCount = mLayoutManager!!.itemCount
                    pastVisibileItem = mLayoutManager!!.findFirstVisibleItemPosition()
                    Log.d(
                        TAG,
                        "onScrolled: $dy, $visibileItemCount, $totalItemCount, $pastVisibileItem"
                    )
                    if (visibileItemCount + pastVisibileItem >= totalItemCount && !loading && viewModel.searchResults.value is ApiState.Success) {
                        if ((viewModel.searchResults.value as ApiState.Success).let {
                                Log.d(TAG, "onScrolled: ${it.data}")
                            it.data.size == it.data.firstOrNull()?.totalCount }) {
                            loading = true
/*                            Toast.makeText(
                                requireActivity(),
                                "This is the last item!",
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                    }
                }
            }
        })
    }

    private fun showNoInternetGroup(){
        binding.apply {
            loadingSearchGroup.visibility = View.GONE
            emptyListSearchGroup.visibility = View.GONE
            noInternetSearchGroup.visibility = View.VISIBLE
        }
    }
    private fun showListLoading() {
        binding.apply {
            recyclerViewSearch.visibility = View.GONE
            loadingSearchGroup.visibility = View.VISIBLE
            emptyListSearchGroup.visibility = View.GONE
            noInternetSearchGroup.visibility = View.GONE
        }
    }

    private fun updateSuggestions(suggestions: List<String>) {
        Log.d(TAG, "updateSuggestions: called - $suggestions")
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "suggestion"))
        suggestions.forEachIndexed { index, suggestion ->
            cursor.addRow(arrayOf(index, suggestion))
        }
        suggestionsAdapter.changeCursor(cursor)
    }

    /*    private fun getSuggestions(query: String): List<String> {
            viewModel.searchResults.value.let { results ->
                return (results as ApiState.Success).data.filter { it.title.contains(query, ignoreCase = true) }
                    .map { it.title }
            }
        }*/

    companion object {
        private const val TAG = "SearchFragment"
    }
}