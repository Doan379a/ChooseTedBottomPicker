package com.example.chot_tv.test_chucnang_tudo

import android.R
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chot_tv.databinding.ActivitiMainFrameBinding
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.TedRxImagePicker
import android.graphics.Color
import android.widget.AdapterView
import androidx.core.content.res.ResourcesCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitiMainFrameBinding
    private lateinit var items: MutableList<Item> // Danh sách các item (ảnh)
    private lateinit var itemsframe: MutableList<Item_frame> // Danh sách các item (ảnh)
    private lateinit var itemstext: MutableList<TextItem> // Danh sách các item (văn bản)
    private val slotPositions = mutableListOf<Pair<Float, Float>>()
    private var item: Item? = null // Biến item để giữ ảnh hiện tại
    private var selectedItem: Item? = null
    private var selectedView: View? = null

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

    private var currentSlotIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiMainFrameBinding.inflate(layoutInflater) // Khởi tạo binding
        setContentView(binding.root)

        items = mutableListOf() // Khởi tạo danh sách item (ảnh)
        itemstext = mutableListOf() // Khởi tạo danh sách item (văn bản)
        itemsframe = mutableListOf()
        binding.buttonText.setOnClickListener {
            val textContent = binding.editText.text.toString().trim()
            if (textContent.isNotEmpty()) {
                val newItem = TextItem(textContent)
                binding.editText.setText("")
                itemstext.add(newItem)
                updateSceneView()
            }
        }
        binding.root.post {
            initSlotPositions()
        }
        Log.d("Locationde", "${slotPositions}")
        // Xử lý sự kiện khi nhấn nút chọn ảnh
        binding.buttonPickImage.setOnClickListener {
            selectImage()
        }
        binding.selectImageButton.setOnClickListener {
            selectFrameImage()
        }
        setupSpinner()
        setupSeekBar()
        showControlPanel(false)
        binding.nenframe.setOnClickListener {
            selectView(binding.nenframe)
        }
        // Get the drawable resource as a Drawable
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.checkbox_on_background, null)

// Convert the drawable to BitmapDrawable
        val bitmapDrawable = drawable as BitmapDrawable

// Set the image in the ImageView
        binding.imageSlot1.setImageDrawable(bitmapDrawable)

    }

    private fun selectView(view: View) {
        if (selectedView == view) return
        selectedView = view
        bringViewToFront(view)
    }

    fun clicknenframe() {
        binding.nenframe.setOnClickListener {
            selectView(binding.nenframe)
        }
    }

    fun clicksenview() {
        binding.sceneView.setOnClickListener {
            selectView(binding.sceneView)
        }
    }

    private fun bringViewToFront(view: View) {
        view.bringToFront()
        // Cập nhật lại layout để hiển thị thay đổi
        view.requestLayout()
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

    private fun initSlotPositions() {
        slotPositions.clear()
        slotPositions.add(getFixedPosition(binding.imageSlot1))
        slotPositions.add(getFixedPosition(binding.imageSlot2))
        slotPositions.add(getFixedPosition(binding.imageSlot3))
        Log.d("Locationde", "Slot positions: $slotPositions")
    }

    private fun selectFrameImage() {
        if (currentSlotIndex >= slotPositions.size) {
            // Đã thêm đủ 3 frame
            return
        }

        TedImagePicker.with(this)
            .start { uri ->
                uri?.let {
                    Glide.with(this)
                        .asBitmap()
                        .load(it)
                        .override(500, 500)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val drawable: Drawable = BitmapDrawable(resources, resource)
                                val newFrameItem = Item_frame(drawable)

                                // Lấy vị trí của slot hiện tại
                                val (x, y) = slotPositions[currentSlotIndex]

                                // Thêm frame item vào SceneView tại vị trí cụ thể
                                binding.sceneView.addFrameItem(newFrameItem, x, y)

                                itemsframe.add(newFrameItem)
                                currentSlotIndex++

                                updateSceneView()
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                }
            }
    }

    private fun getFixedPosition(view: View): Pair<Float, Float> {
        val location = IntArray(2)

        view.getLocationOnScreen(location) // Hoặc sử dụng getLocationInWindow nếu cần
        val x = location[0].toFloat()
        val y = location[1].toFloat()
        Log.d("Locationde", "X: $x, Y: $y")
        return Pair(x, y)
    }


    // Phương thức chọn ảnh từ thư viện bằng TedImagePicker
    // Phương thức chọn ảnh từ thư viện bằng TedRxImagePicker
    @SuppressLint("CheckResult")
    private fun selectImage() {
        TedRxImagePicker.with(this)
            .max(5, "amnh dat toi da")// Số lượng ảnh tối đa có thể chọn
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
                            .override(500, 500) // Kích thước tối đa (có thể tùy chỉnh)
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
                                    updateSceneView() // Cập nhật SceneView
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
        allItems.addAll(itemsframe)
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
