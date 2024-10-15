package com.senseicoder.quickcart.features.main.ui.favorite

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.global.setupRefreshLayout
import com.senseicoder.quickcart.core.global.showErrorSnackbar
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepoImpl
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentFavoriteBinding
import com.senseicoder.quickcart.features.main.ui.favorite.adapters.FavoritesAdapter
import com.senseicoder.quickcart.features.main.ui.favorite.viewmodel.FavoriteViewModel
import com.senseicoder.quickcart.features.main.ui.favorite.viewmodel.FavoriteViewModelFactory
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = FavoriteViewModelFactory(
            FavoriteRepoImpl.getInstance()
        )
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainBackBtnFavorite.setOnClickListener {
            findNavController().popBackStack()
        }
        adapter = FavoritesAdapter ({
            ConfirmationDialogFragment(DialogType.DEL_FAV) {
                viewModel.removeFromFavorite(it)
            }.show(childFragmentManager, null)
        }) {
            if(NetworkUtils.isConnected(requireContext())){
                ViewModelProvider(requireActivity())[MainActivityViewModel::class.java].setCurrentProductId(it.apply {
                    Log.d(
                        TAG,
                        "onViewCreated: $it"
                    ) })
                findNavController().navigate(R.id.action_favoriteFragment_to_productDetailsFragment)
            }else{
                showErrorSnackbar(getString(R.string.no_internet_connection))
            }
        }
        binding.favoriteRecycler.apply {
            adapter = this@FavoriteFragment.adapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        setupRefreshLayout(binding.swipeRefreshLayoutFavorite, binding.favoriteRecycler)
        Log.d(TAG, "onViewCreated: created")
        setupListeners()
        subscribeToObservables()
        getFavoritesIfNetworkAvailable()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).apply {
            hideBottomNavBar()
            toolbarVisibility(false)
            Log.d(TAG, "onViewCreated: started")
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: ")
    }

    private fun setupListeners() {
        binding.apply {
            swipeRefreshLayoutFavorite.setOnRefreshListener {
                Log.d(TAG, "onCreate: refreshing")
                getFavoritesIfNetworkAvailable()
            }
        }
    }

    private fun subscribeToObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.favorites.collectLatest {
                    when (it) {
                        ApiState.Init -> {

                        }
                        ApiState.Loading -> {
                            showLoading()
                        }
                        is ApiState.Failure -> {
                            showFailure()
                        }
                        is ApiState.Success -> {
                            if (it.data.isEmpty()) {
                                showEmpty()
                            } else {
                                adapter.submitList(it.data)
                                showSuccess()
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.isFavorite.collect {
                    when (it) {
                        ApiState.Init -> {}
                        ApiState.Loading -> {
                            showLoading()
                        }
                        is ApiState.Failure -> {
                            showSuccess()
                            showErrorSnackbar(getString(R.string.favorite_removed_unsuccessfully), 2000)
                        }
                        is ApiState.Success -> {
                            showSnackbar(getString(R.string.favorite_removed_successfully), color = R.color.secondary)
                        }
                    }
                }
            }
        }
    }

    private fun getFavoritesIfNetworkAvailable(){
        if(NetworkUtils.isConnected(requireContext())){
            viewModel.getFavorites()
        }else{
            showNoInternet()
        }
    }

    private fun showEmpty() {
        binding.apply {
            swipeRefreshLayoutFavorite.isRefreshing = false
            favoriteRecycler.visibility = View.GONE
            loadingFavoriteGroup.visibility = View.GONE
            noInternetFavoriteGroup.visibility = View.GONE
            emptyFavoriteGroup.visibility = View.VISIBLE
        }
    }

    private fun showFailure() {
        binding.apply {
            swipeRefreshLayoutFavorite.isRefreshing = false
            favoriteRecycler.visibility = View.GONE
            loadingFavoriteGroup.visibility = View.GONE
            noInternetFavoriteGroup.visibility = View.GONE
            showErrorSnackbar(getString(R.string.unknown))
        }
    }

    private fun showSuccess() {
        binding.apply {
            swipeRefreshLayoutFavorite.isRefreshing = false
            favoriteRecycler.visibility = View.VISIBLE
            loadingFavoriteGroup.visibility = View.GONE
            noInternetFavoriteGroup.visibility = View.GONE
            emptyFavoriteGroup.visibility = View.GONE
        }
    }

    private fun showLoading() {
        binding.apply {
            favoriteRecycler.visibility = View.GONE
            loadingFavoriteGroup.visibility = View.VISIBLE
            noInternetFavoriteGroup.visibility = View.GONE
            emptyFavoriteGroup.visibility = View.GONE
        }
    }

    private fun showNoInternet() {
        binding.apply {
            swipeRefreshLayoutFavorite.isRefreshing = false
            favoriteRecycler.visibility = View.GONE
            loadingFavoriteGroup.visibility = View.GONE
            noInternetFavoriteGroup.visibility = View.VISIBLE
            emptyFavoriteGroup.visibility = View.GONE
        }
    }
    companion object{
        private const val TAG = "FavoriteFragment"
    }
}