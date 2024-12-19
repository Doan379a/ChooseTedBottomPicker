package com.example.chot_tv.ok_chucnang_tudo


import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chot_tv.R
import com.example.chot_tv.databinding.ActivityBieenTapBinding
import com.example.chot_tv.tedbottompicker.TedBottomPicker
import gun0912.tedimagepicker.builder.TedImagePicker


class BieenTapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBieenTapBinding
    private var isFrame1OnTop = false
    private val colors = arrayOf(R.color.color_red, R.color.color_green, R.color.color_yellow)
    private var currentColorIndex = 0
    private lateinit var items: MutableList<Item>
    private lateinit var itemstext: MutableList<TextItem>
    private var item: Item? = null
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
    private var selectedItem: Item? = null
    private var currentSlotIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBieenTapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeColor()
        items = mutableListOf()
        itemstext = mutableListOf()

        binding.btnChonmau.setOnClickListener {
            changeColor()
        }
        binding.btnThemText.setOnClickListener {
            val textContent = binding.editText.text.toString().trim()
            if (textContent.isNotEmpty()) {
                val newItem = TextItem(textContent)
                binding.editText.setText("")
                itemstext.add(newItem)
                updateSceneView()
            }
        }
        binding.imageSlot1.controller.settings.apply {
            setMaxZoom(5f)
            setMinZoom(0.01f)
            setPanEnabled(true)
            setDoubleTapEnabled(false)
            setZoomEnabled(true)
            setRotationEnabled(true)
            setOverzoomFactor(1000f)
            setFillViewport(false)
            setOverscrollDistance(1000f, 1000f)
        }
        binding.buttonPickImage.setOnClickListener {
            selectImage()
        }
        binding.btnThemAnh.setOnClickListener {
            selectImagenen()
        }
        binding.buttonNoi.setOnClickListener {
            if (isFrame1OnTop) {
                binding.frame1.bringToFront()
                binding.frame1.alpha = 1f // Đưa frame1 lên trên và đặt alpha là 1
            } else {
                binding.sceneView.bringToFront()
//                binding.sceneView.alpha = 1f // Đưa sceneView lên trên và đặt alpha là 1
//                binding.frame1.alpha = 0.5f // Giảm alpha của frame1
            }
            isFrame1OnTop = !isFrame1OnTop // Thay đổi trạng thái
        }

        binding.btnThemHieuUng.setOnClickListener {
            // Adding bottom-left image
            val bottomLeftImageView = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    200, // width in pixels (you can change this)
                    200, // height in pixels
                    Gravity.BOTTOM or Gravity.START // bottom-left corner
                ).apply {
                    marginStart = 16 // margin from the left (optional)
                    bottomMargin = 16 // margin from the bottom (optional)
                }
                setImageResource(R.drawable.frame4) // Set your image resource
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            binding.frame1.addView(bottomLeftImageView)

            // Adding bottom-right image
            val bottomRightImageView = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    200, // width in pixels (you can change this)
                    200, // height in pixels
                    Gravity.BOTTOM or Gravity.END // bottom-right corner
                ).apply {
                    marginEnd = 16 // margin from the right (optional)
                    bottomMargin = 16 // margin from the bottom (optional)
                }
                setImageResource(R.drawable.icon4_frame1) // Set your image resource
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            binding.frame1.addView(bottomRightImageView)
        }
        setupSpinner()
        setupSeekBar()
        showControlPanel(false)
    }
    fun showControlPanel(isVisible: Boolean) {
        binding.controlPanel.visibility = if (isVisible) View.VISIBLE else View.GONE
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
    private fun changeColor() {
        val color = ContextCompat.getColor(this, colors[currentColorIndex])
        binding.shapeImageView.setColorFilter(color)
        binding.frameLayout.setBackgroundColor(color)
        // Chuyển sang màu tiếp theo
        currentColorIndex = (currentColorIndex + 1) % colors.size
    }

    private fun selectImage() {
        TedBottomPicker.with(this)
            .show { uri ->
                uri?.let {
                    // Sử dụng Glide để tải ảnh với kích thước tối ưu
                    Glide.with(this)
                        .asBitmap()
                        .load(it)
                        .apply(
                            RequestOptions()
                                .fitCenter() // Giữ nguyên tỷ lệ mà không cắt
                        )
                        .override(300, 300)
                       // Kích thước tối đa (có thể tùy chỉnh)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val drawable: Drawable =
                                    BitmapDrawable(resources, resource) // Tạo drawable từ bitmap
                                val newItem =
                                    Item(drawable = drawable) // Tạo đối tượng item ảnh mới
                                item = newItem
                                items.add(newItem) // Thêm item ảnh mới vào danh sách

                                updateSceneView()
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // Không cần làm gì khi ảnh bị hủy tải
                            }
                        })
                }
            }
    }
    private fun selectImagenen() {
        TedImagePicker.with(this)
            .start { uri ->
                uri?.let {
                    // Sử dụng Glide để tải ảnh với kích thước tối ưu
                    Glide.with(this)
                        .asBitmap()
                        .load(it)
                        .apply(
                            RequestOptions()
                                .fitCenter() // Giữ nguyên tỷ lệ mà không cắt
                        )
                        .override(1000, 1000) // Kích thước tối đa (có thể tùy chỉnh)
                        .into(binding.imageSlot1)
                }
            }
    }

    fun setupSpinner() {
        val colorNames =
            listOf("Đỏ", "Xanh lá", "Xanh dương", "Vàng", "Hồng", "Xanh dương nhạt", "Trắng", "Đen")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colorNames)
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

    private fun updateSceneView() {
        val allItems = mutableListOf<BaseItem>()
        allItems.addAll(items)
        allItems.addAll(itemstext)
        binding.sceneView.setItems(allItems)
    }
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
