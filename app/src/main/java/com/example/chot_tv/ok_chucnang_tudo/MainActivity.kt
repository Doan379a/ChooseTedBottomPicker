package com.example.chot_tv.ok_chucnang_tudo

import android.R
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chot_tv.databinding.ActivitiMainBinding
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.TedRxImagePicker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitiMainBinding
    private lateinit var items: MutableList<Item> // Danh sách các item (ảnh)
    private lateinit var itemstext: MutableList<TextItem> // Danh sách các item (văn bản)
    private var item: Item? = null // Biến item để giữ ảnh hiện tại
    private var selectedItem: Item? = null
    private val colorList = listOf(
        "#FF0000", // Đỏ
        "#00FF00", // Xanh lá
        "#0000FF", // Xanh dương
        "#FFFF00", // Vàng
        "#FF00FF", // Hồng
        "#00FFFF", // Xanh dương nhạt
        "#FFFFFF", // Trắng
        "#000000"  // Đen
    )
    private val fixedPositions = listOf(
        Pair(50f, 100f),
        Pair(150f, 200f),
        Pair(250f, 300f),
        Pair(350f, 400f),
        Pair(450f, 500f),
        Pair(550f, 600f),
        Pair(650f, 700f),
        Pair(750f, 800f),
        Pair(850f, 900f),
        Pair(950f, 1000f)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiMainBinding.inflate(layoutInflater) // Khởi tạo binding
        setContentView(binding.root)

        items = mutableListOf() // Khởi tạo danh sách item (ảnh)
        itemstext = mutableListOf() // Khởi tạo danh sách item (văn bản)

        // Xử lý sự kiện khi nhấn nút nhập text
        binding.buttonText.setOnClickListener {
            val textContent = binding.editText.text.toString().trim()

            if (textContent.isNotEmpty()) {
                val newItem = TextItem(textContent)
                binding.editText.setText("")
                itemstext.add(newItem)
                updateSceneView()
            }
        }

        // Xử lý sự kiện khi nhấn nút chọn ảnh
        binding.buttonPickImage.setOnClickListener {
            selectImage()
        }

        setupSpinner()
        setupSeekBar()
        showControlPanel(false)

    }

    fun showControlPanel(isVisible: Boolean) {
        binding.controlPanel.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun setupSpinner() {
        val colorNames =
            listOf("Đỏ", "Xanh lá", "Xanh dương", "Vàng", "Hồng", "Xanh dương nhạt", "Trắng", "Đen")
        val colorAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, colorNames)
        binding.colorSpinner.adapter = colorAdapter

        // Các khởi tạo khác...

        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedItem?.let {
                    val color = Color.parseColor(colorList[position]) // Lấy mã màu tương ứng
                    it.borderColor = color // Cập nhật màu cho item được chọn
                    binding.sceneView.invalidate() // Cập nhật SceneView
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Không làm gì khi không có item nào được chọn
            }
        }
    }

    fun setupSeekBar() {
        binding.sceneView.setOnItemSelectedListener { item ->
            selectedItem = item as? Item // Cập nhật selectedItem
            selectedItem?.let {
                binding.seekBar.progress = it.borderWidth.toInt() // Cập nhật giá trị SeekBar
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedItem?.let {
                    it.borderWidth = progress.toFloat()
                    binding.sceneView.invalidate() // Cập nhật SceneView
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Phương thức chọn ảnh từ thư viện bằng TedImagePicker
//    private fun selectImage() {
//        TedImagePicker.with(this)
//            .start { uri ->
//                uri?.let {
//                    // Sử dụng Glide để tải ảnh với kích thước tối ưu
//                    Glide.with(this)
//                        .asBitmap()
//                        .load(it)
//                        .apply(
//                            RequestOptions()
//                            .fitCenter() // Giữ nguyên tỷ lệ mà không cắt
//                        )
//                        .override(300, 300) // Kích thước tối đa (có thể tùy chỉnh)
//                        .into(object : CustomTarget<Bitmap>() {
//                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                                val drawable: Drawable = BitmapDrawable(resources, resource) // Tạo drawable từ bitmap
//                                val newItem = Item(drawable = drawable) // Tạo đối tượng item ảnh mới
//
//                                item = newItem
//                                items.add(newItem) // Thêm item ảnh mới vào danh sách
//
//                                // Cập nhật SceneView với cả danh sách item và itemstext
//                                updateSceneView()
//                            }
//
//                            override fun onLoadCleared(placeholder: Drawable?) {
//                                // Không cần làm gì khi ảnh bị hủy tải
//                            }
//                        })
//                }
//            }
//    }
    @SuppressLint("CheckResult")
    private fun selectImage() {
        TedRxImagePicker.with(this)
            .startMultiImage() // Cho phép chọn nhiều ảnh
            .subscribe({ uris ->
                // uris là danh sách các Uri của ảnh đã chọn
                uris?.let {
                    it.forEach { uri ->
                        // Sử dụng Glide để tải từng ảnh với kích thước tối ưu
                        Glide.with(this)
                            .asBitmap()
                            .load(uri)
                            .apply(RequestOptions().fitCenter()) // Giữ nguyên tỷ lệ mà không cắt
                            .override(200, 200) // Kích thước tối đa (có thể tùy chỉnh)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    val drawable: Drawable = BitmapDrawable(
                                        resources,
                                        resource
                                    ) // Tạo drawable từ bitmap
                                    val newItem =
                                        Item(drawable = drawable) // Tạo đối tượng item ảnh mới


                                    item = newItem
                                    items.add(newItem) // Thêm item ảnh mới vào danh sách

                                    // Cập nhật SceneView với cả danh sách item và itemstext
                                    updateSceneView()
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    // Không cần làm gì khi ảnh bị hủy tải
                                }
                            })
                    }
                }
            }, { error ->
                // Xử lý lỗi nếu có
                error.printStackTrace()
            })
    }

    // Phương thức cập nhật SceneView với cả hình ảnh và văn bản
    private fun updateSceneView() {
        val allItems = mutableListOf<BaseItem>()
        allItems.addAll(items)
        allItems.addAll(itemstext)
        binding.sceneView.setItems(allItems)
    }

    // Phương thức được gọi khi một item bị xóa
    fun onItemDeleted(deletedItemId: String) {
        // Xóa item khỏi danh sách items của MainActivity
        items.removeAll { it.id == deletedItemId }
        itemstext.removeAll { it.id == deletedItemId }
        updateSceneView()
    }

    fun onItemedit(itemId: String, newText: String) {
        itemstext.find { it.id == itemId }?.let { item ->
            item.text = newText
            item.drawable = item.createDrawableFromText(newText)
            Log.d("MainActivityss", "Item with ID: $itemId updated to new text: $newText")
        }

        updateSceneView() // Cập nhật lại SceneView
    }

}
