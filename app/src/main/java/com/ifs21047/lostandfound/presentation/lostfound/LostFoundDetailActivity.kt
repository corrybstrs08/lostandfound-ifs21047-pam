package com.ifs21047.lostandfound.presentation.lostfound

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21047.delcomtodo.data.remote.response.LostFoundResponse
import com.ifs21047.lostandfound.R
import com.ifs21047.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21047.lostandfound.databinding.ActivityLostfoundDetailBinding
import com.ifs21047.lostfounds.data.model.LostFound
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21047.lostfounds.presentation.ViewModelFactory
import com.ifs21047.lostfounds.presentation.lostfound.LostFoundViewModel

class LostFoundDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostfoundDetailBinding
    private val viewModel by viewModels<LostFoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isChanged: Boolean = false
    private var isSave: Boolean = false
    private var LostFound: LostFoundEntity? = null

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostFoundManageActivity.RESULT_CODE) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostfoundDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }

    private fun setupAction() {
        val todoId = intent.getIntExtra(KEY_TODO_ID, 0)
        if (todoId == 0) {
            finish()
            return
        }

        observeGetLostFound(todoId)

        binding.appbarLostFoundDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, isChanged)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }

    private fun observeGetLostFound(todoId: Int) {
        viewModel.getLostFound(todoId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }

                is MyResult.Success -> {
                    showLoading(false)
                    loadTodo(result.data.data.todo)
                }

                is MyResult.Error -> {
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }

    private fun loadTodo(todo: LostFoundResponse?) {
        if (todo != null) {
            showComponent(true)

            binding.apply {
                tvLostFoundDetailTitle.text = todo.title
                tvLostFoundDetailDate.text = "Diposting pada: ${todo.createdAt}"
                tvLostFoundDetailDesc.text = todo.description

                viewModel.getLocalLostFound(todo.id).observeOnce {
                    if(it != null){
                        LostFound = it
                        setSave(true)
                    }else{
                        setSave(false)
                    }
                }

                cbLostFoundDetailIsFinished.isChecked = todo.isCompleted == 1

                val statusText = if (todo.status.equals("found", ignoreCase = true)) {
                    // Jika status "found", maka gunakan warna hijau
                    highlightText("Found", Color.GREEN)
                } else {
                    // Jika status "lost", maka gunakan warna kuning
                    highlightText("Lost", Color.YELLOW)
                }
                // Menetapkan teks status yang sudah disorot ke TextView
                tvStatusDetail.text = statusText

                cbLostFoundDetailIsFinished.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.putLostFound(
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
                                        this@LostFoundDetailActivity,
                                        "Gagal menyelesaikan todo: " + todo.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@LostFoundDetailActivity,
                                        "Gagal batal menyelesaikan todo: " + todo.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            is MyResult.Success -> {
                                if (isChecked) {
                                    Toast.makeText(
                                        this@LostFoundDetailActivity,
                                        "Item Lost and Found " + todo.title + "berhasil diselesaikan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@LostFoundDetailActivity,
                                        "Batal menyelesaikan item" + todo.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                if ((todo.isCompleted == 1) != isChecked) {
                                    isChanged = true
                                }
                            }

                            else -> {}
                        }
                    }
                }

                ivLostFoundDetailActionSave.setOnClickListener {
                    if(isSave){
                        setSave(false)
                        if(LostFound != null){
                            viewModel.deleteLocalLostFound(LostFound!!)
                        }
                        Toast.makeText(
                            this@LostFoundDetailActivity,
                            "LostFound berhasil dihapus dari daftar favorite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        LostFound = LostFoundEntity(
                            id = todo.id,
                            title = todo.title,
                            description = todo.description,
                            isCompleted = todo.isCompleted,
                            cover = todo.cover,
                            createdAt = todo.createdAt,
                            updatedAt = todo.updatedAt,
                            status = "", // Anda perlu memberikan nilai default untuk status
                            userId = 0 // Anda perlu memberikan nilai default untuk userId
                        )

                        setSave(true)
                        viewModel.insertLocalLostFound(LostFound!!)
                        Toast.makeText(
                            this@LostFoundDetailActivity,
                            "LostFound berhasil ditambahkan ke daftar favorite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                ivLostFoundDetailActionDelete.setOnClickListener {
                    val builder = AlertDialog.Builder(this@LostFoundDetailActivity)

                    builder.setTitle("Konfirmasi Hapus Item Lost & Found")
                        .setMessage("Anda yakin ingin menghapus Item ini?")

                    builder.setPositiveButton("Ya") { _, _ ->
                        observeDelete(todo.id)
                    }

                    builder.setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss() // Menutup dialog
                    }

                    val dialog = builder.create()
                    dialog.show()
                }

                ivLostFoundDetailActionEdit.setOnClickListener {
                    val lostFound = LostFound(
                        todo.id,
                        todo.title,
                        todo.description,
                        todo.status,
                        todo.isCompleted == 1,
                        todo.cover
                    )

                    val intent = Intent(
                        this@LostFoundDetailActivity,
                        LostFoundManageActivity::class.java
                    )
                    intent.putExtra(LostFoundManageActivity.KEY_IS_ADD, false)
                    intent.putExtra(LostFoundManageActivity.KEY_TODO, lostFound)
                    launcher.launch(intent)
                }
            }
        }else {
            // Tampilkan pesan atau lakukan tindakan lainnya jika objek todo null
            Toast.makeText(
                this@LostFoundDetailActivity,
                "Tidak ditemukan item yang dicari",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setSave(status: Boolean){
        isSave = status
        if(status){
            binding.ivLostFoundDetailActionSave
                .setImageResource(R.drawable.save)
        }else{
            binding.ivLostFoundDetailActionSave
                .setImageResource(R.drawable.savety)
        }
    }

    private fun highlightText(text: String, color: Int): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(ForegroundColorSpan(color), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    private fun observeDelete(todoId: Int) {
        // Menampilkan loading spinner dan menyembunyikan komponen lainnya
        showComponent(false)
        showLoading(true)

        // Mengamati proses penghapusan
        viewModel.delete(todoId).observeOnce { result ->
            when (result) {
                is MyResult.Error -> {
                    // Jika terjadi kesalahan, tampilkan pesan kesalahan dan kembalikan ke tampilan sebelumnya
                    showComponent(true)
                    showLoading(false)
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Gagal menghapus item: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MyResult.Success -> {
                    // Jika penghapusan berhasil, tampilkan pesan sukses dan kembalikan ke tampilan sebelumnya
                    showLoading(false)
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Berhasil menghapus item",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Setelah menghapus item, kirimkan status perubahan dan selesaikan activity
                    val resultIntent = Intent()
                    resultIntent.putExtra(KEY_IS_CHANGED, true)
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }

                else -> {}
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLostFoundDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showComponent(status: Boolean) {
        binding.llLostFoundDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }

    companion object {
        const val KEY_TODO_ID = "todo_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
    }
}