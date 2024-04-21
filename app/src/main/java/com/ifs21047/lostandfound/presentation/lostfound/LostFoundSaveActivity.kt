package com.ifs21047.lostandfound.presentation.lostfound

import com.ifs21047.lostandfound.R
import com.ifs21047.lostfounds.adapter.LostFoundsAdapter
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.helper.Utils.Companion.entitiesToResponses
import com.ifs21047.lostfounds.presentation.ViewModelFactory
import com.ifs21047.lostfounds.presentation.lostfound.LostFoundViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifs21047.delcomtodo.data.remote.response.LostFoundsItemResponse
import com.ifs21047.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21047.lostandfound.databinding.ActivityLostFoundSaveBinding
import com.ifs21047.lostfounds.helper.Utils.Companion.observeOnce

class LostFoundSaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostFoundSaveBinding
    private val viewModel by viewModels<LostFoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostFoundDetailActivity.RESULT_CODE) {
            result.data?.let {
                val isChanged = it.getBooleanExtra(
                    LostFoundDetailActivity.KEY_IS_CHANGED,
                    false
                )
                if (isChanged) {
                    recreate()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostFoundSaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.appbarLostFoundFavorite.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(LostFoundDetailActivity.KEY_IS_CHANGED, true)
            setResult(LostFoundDetailActivity.RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun setupView() {
        showComponentNotEmpty(false)
        showEmptyError(false)
        showLoading(true)
        binding.appbarLostFoundFavorite.overflowIcon =
            ContextCompat
                .getDrawable(this, R.drawable.ic_more_vert_24)
        observeGetLostFounds()
    }
    private fun observeGetLostFounds() {
        viewModel.getLocalLostFounds().observe(this) { lostfounds ->
            loadLostFoundsToLayout(lostfounds)
        }
    }
    private fun loadLostFoundsToLayout(lostfounds: List<LostFoundEntity>?) {
        showLoading(false)
        val layoutManager = LinearLayoutManager(this)
        binding.rvLostFoundFavoriteLostFounds.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(
            this,
            layoutManager.orientation
        )
        binding.rvLostFoundFavoriteLostFounds.addItemDecoration(itemDecoration)
        if (lostfounds.isNullOrEmpty()) {
            showEmptyError(true)
            binding.rvLostFoundFavoriteLostFounds.adapter = null
        } else {
            showComponentNotEmpty(true)
            showEmptyError(false)
            val adapter = LostFoundsAdapter()
            adapter.submitOriginalList(entitiesToResponses(lostfounds))
            binding.rvLostFoundFavoriteLostFounds.adapter = adapter
            adapter.setOnItemClickCallback(
                object : LostFoundsAdapter.OnItemClickCallback {
                    override fun onCheckedChangeListener(
                        lostfound: LostFoundsItemResponse,
                        isChecked: Boolean
                    ) {
                        adapter.filter(binding.svLostFoundFavorite.query.toString())
                        val newLostFound = LostFoundEntity(
                            id = lostfound.id,
                            title = lostfound.title,
                            description = lostfound.description,
                            isCompleted = lostfound.isCompleted, // Sesuaikan dengan isCompleted
                            cover = lostfound.cover,
                            createdAt = lostfound.createdAt,
                            updatedAt = lostfound.updatedAt,
                            status = "", // Sesuaikan dengan nilai default untuk status
                            userId = 0 // Sesuaikan dengan nilai default untuk userId
                        )

                        viewModel.putLostFound(
                            lostfound.id,
                            lostfound.title,
                            lostfound.description,
                            lostfound.status,
                            isChecked
                        ).observeOnce {
                            when (it) {
                                is MyResult.Error -> {
                                    if (isChecked) {
                                        Toast.makeText(
                                            this@LostFoundSaveActivity,
                                            "Gagal menyelesaikan LostFound: " + lostfound.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@LostFoundSaveActivity,
                                            "Gagal batal menyelesaikan LostFound: " + lostfound.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                is MyResult.Success -> {
                                    if (isChecked) {
                                        Toast.makeText(
                                            this@LostFoundSaveActivity,
                                            "Berhasil menyelesaikan LostFound: " + lostfound.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@LostFoundSaveActivity,
                                            "Berhasil batal menyelesaikan lostfound: " + lostfound.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    viewModel.insertLocalLostFound(newLostFound)
                                }
                                else -> {}
                            }
                        }
                    }
                    override fun onClickDetailListener(lostfoundId: Int) {
                        val intent = Intent(
                            this@LostFoundSaveActivity,
                            LostFoundDetailActivity::class.java
                        )
                        intent.putExtra(LostFoundDetailActivity.KEY_TODO_ID, lostfoundId)
                        launcher.launch(intent)
                    }
                })
            binding.svLostFoundFavorite.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        adapter.filter(newText)
                        binding.rvLostFoundFavoriteLostFounds
                            .layoutManager?.scrollToPosition(0)

                        return true
                    }
                })
        }
    }

    private fun showComponentNotEmpty(status: Boolean) {
        binding.svLostFoundFavorite.visibility =
            if (status) View.VISIBLE else View.GONE
        binding.rvLostFoundFavoriteLostFounds.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    private fun showEmptyError(isError: Boolean) {
        binding.tvLostFoundFavoriteEmptyError.visibility =
            if (isError) View.VISIBLE else View.GONE
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbLostFoundFavorite.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

}