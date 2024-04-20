package com.ifs21047.lostandfound.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifs21047.delcomtodo.data.remote.response.DelcomLostFoundsResponse
import com.ifs21047.delcomtodo.data.remote.response.LostFoundsItemResponse
import com.ifs21047.lostandfound.R
import com.ifs21047.lostandfound.databinding.ActivityMainBinding
import com.ifs21047.lostfounds.adapter.LostFoundsAdapter
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21047.lostfounds.presentation.ViewModelFactory
import com.ifs21047.lostandfound.presentation.login.LoginActivity
import com.ifs21047.lostandfound.presentation.lostfound.LostFoundDetailActivity
import com.ifs21047.lostandfound.presentation.lostfound.LostFoundManageActivity
import com.ifs21047.lostfounds.presentation.main.MainViewModel
import com.ifs21047.lostandfound.presentation.profile.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostFoundManageActivity.RESULT_CODE) {
            recreate()
        }

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        showComponentNotEmpty(false)
        showEmptyError(false)
        showLoading(true)

        binding.appbarMain.overflowIcon =
            ContextCompat
                .getDrawable(this, R.drawable.ic_more_vert_24)

        observeGetAll()
    }

    private fun setupAction() {
        binding.appbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mainMenuProfile -> {
                    openProfileActivity()
                    true
                }

                R.id.mainMenuLogout -> {
                    viewModel.logout()
                    openLoginActivity()
                    true
                }

                else -> false
            }
        }

        binding.fabMainAddTodo.setOnClickListener {
            openAddLostFoundActivity()
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                openLoginActivity()
            } else {
                // load-todos
            }
        }
    }

    private fun observeGetAll() {
        viewModel.getTodos().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }

                    is MyResult.Success -> {
                        showLoading(false)
                        loadAllToLayout(result.data)
                    }

                    is MyResult.Error -> {
                        showLoading(false)
                        showEmptyError(true)
                    }
                }
            }
        }
    }

    private fun loadAllToLayout(response: DelcomLostFoundsResponse) {
        // Periksa apakah response atau data pada response null
        if (response == null) {
            // Handle null case appropriately, misalnya menampilkan pesan error atau melakukan tindakan lainnya
            Log.e("MainActivity", "response == null")
            return
        } else if (response.data == null) {
            Log.e("MainActivity", "response.data == null")
            return
        } else if (response.data.todos == null) {
            Log.e("MainActivity", "response.data.todos == null")
            return
        }

        // Lanjutkan dengan pemrosesan data
        // Dalam metode loadAllToLayout()
        val todos = response.data.todos
        val layoutManager = LinearLayoutManager(this)
        binding.rvMainTodos.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(
            this,
            layoutManager.orientation
        )
        binding.rvMainTodos.addItemDecoration(itemDecoration)

        if (todos.isEmpty()) {
            showEmptyError(true)
            binding.rvMainTodos.adapter = null
        } else {
            showComponentNotEmpty(true)
            showEmptyError(false)

            val adapter = LostFoundsAdapter()
            adapter.submitOriginalList(todos)
            binding.rvMainTodos.adapter = adapter
            adapter.setOnItemClickCallback(object : LostFoundsAdapter.OnItemClickCallback {
                override fun onCheckedChangeListener(
                    todo: LostFoundsItemResponse,
                    isChecked: Boolean
                ) {
                    adapter.filter(binding.svMain.query.toString())

                    viewModel.putTodo(
                        todo.id,
                        todo.title,
                        todo.description,
                        todo.status,
                        isChecked
                    ).observeOnce {
                        when (it) {
                            is MyResult.Error -> {
                                if (isChecked) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Gagal menyelesaikan todo: " + todo.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Gagal batal menyelesaikan todo: " + todo.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            is MyResult.Success<*> -> {
                                if (isChecked) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Berhasil menyelesaikan todo: " + todo.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Berhasil batal menyelesaikan todo: " + todo.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            else -> {}
                        }
                    }
                }

                override fun onClickDetailListener(todoId: Int) {
                    val intent = Intent(
                        this@MainActivity,
                        LostFoundDetailActivity::class.java
                    )
                    intent.putExtra(LostFoundDetailActivity.KEY_TODO_ID, todoId)
                    launcher.launch(intent)
                }
            })

            binding.svMain.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        adapter.filter(newText)
                        binding.rvMainTodos.layoutManager?.scrollToPosition(0)
                        return true
                    }
                }
            )
        }
    }

        private fun showLoading(isLoading: Boolean) {
        binding.pbMain.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun openProfileActivity() {
        val intent = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun showComponentNotEmpty(status: Boolean) {
        binding.svMain.visibility =
            if (status) View.VISIBLE else View.GONE

        binding.rvMainTodos.visibility =
            if (status) View.VISIBLE else View.GONE
    }

    private fun showEmptyError(isError: Boolean) {
        binding.tvMainEmptyError.visibility =
            if (isError) View.VISIBLE else View.GONE
    }

    private fun openLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun openAddLostFoundActivity() {
        val intent = Intent(
            this@MainActivity,
            LostFoundManageActivity::class.java
        )
        intent.putExtra(LostFoundManageActivity.KEY_IS_ADD, true)
        launcher.launch(intent)
    }
}