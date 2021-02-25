package top.intkilow.feat.page.photo.choose

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import top.intkilow.architecture.ui.SnackbarUtil
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.feat.databinding.FeatureChoosePhotoBinding
import top.intkilow.feat.page.photo.adapter.PhotoAdapter
import top.intkilow.feat.vo.PhotoVO
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val CHOOSE_PHOTO_RESULT_DATA = "choose_photo_result_data"

class ChoosePhoto : Fragment() {

    // 权限
    val PHOTO_REQUEST_CODE = 1
    val PHOTO_TAKE_CODE = 0

    private val maxDefault = 9
    private val choosePhotoModel by viewModels<ChoosePhotoModel>()

    // 拍照 结果
    val REQUEST_TAKE_PHOTO = 3

    // 拍照 路径
    var currentPhotoPath: String = ""

    var recyclerView: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { context ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) {
                choosePhotoModel.init()
            } else {
                this.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PHOTO_REQUEST_CODE)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val binding =
                FeatureChoosePhotoBinding.inflate(inflater, container, false)
        // 默认可选数量
        val max = arguments?.getInt("max", maxDefault) ?: maxDefault
        choosePhotoModel.chooseSize.value = max

        choosePhotoModel.photoList.observe(viewLifecycleOwner) {
            if (null == it) {
                return@observe
            }
            val adapter = PhotoAdapter(it, { size ->
                choosePhotoModel.selectSize.value = size
            }) { position ->

                if (position == 0) {
                    // 拍照
                    context?.let { context ->
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                            ) -> {
                                dispatchTakePictureIntent()
                            }
                            else -> {
                                this.requestPermissions(arrayOf(Manifest.permission.CAMERA),
                                        PHOTO_TAKE_CODE)
                            }
                        }
                    }
                } else {
                    // TODO: 2020/12/16 预览
                }

            }
            recyclerView = binding.recyclerView
            adapter.selectSize = max
            binding.recyclerView.adapter = adapter
        }

        binding.confirmButton.setOnClickListener {

            val adapter =
                    binding.recyclerView.adapter
            if (adapter is PhotoAdapter) {
                val savedStateHandle: SavedStateHandle? = arguments?.getInt("destinationId", -1)
                        ?.let {
                            if (it > 0) {

                                findNavController().getBackStackEntry(it).savedStateHandle
                            } else {
                                findNavController().previousBackStackEntry?.savedStateHandle
                            }

                        }
                        ?: findNavController().previousBackStackEntry?.savedStateHandle

                savedStateHandle?.apply {
                    getLiveData<LinkedList<PhotoVO>>(CHOOSE_PHOTO_RESULT_DATA).apply {
                        value = adapter.selectData
                    }
                }
            }
            findNavController().navigateUp()

        }


        binding.choosePhotoModel = choosePhotoModel
        binding.lifecycleOwner = viewLifecycleOwner
        context?.let { context ->
            // 设置bar
            val layoutParams = binding
                    .toolbar.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = ViewUtils.getStatusBarHeight(context)
            binding.toolbar.layoutParams = layoutParams

        }


        return binding.root
    }


    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA).format(Date())

        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun dispatchTakePictureIntent() {
        context?.let { context ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(context.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile(context)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        recyclerView?.let {
                            SnackbarUtil.toast(it, "error = ${ex.message}")
                        }
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.FileProvider",
                                it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //拍照成功 resultCode == RESULT_OK
        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            choosePhotoModel.saveToGallery(currentPhotoPath).observe(viewLifecycleOwner) {
                it?.let {
                    val adapter = recyclerView?.adapter
                    if (adapter is PhotoAdapter) {
                        adapter.selectData.add(it)
                        adapter.notifyDataSetChanged()
                        choosePhotoModel.selectSize.value = adapter.selectData.size
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var pass = true
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                pass = false
                return@forEach
            }
        }
        if (pass) {
            if (requestCode == PHOTO_REQUEST_CODE) {
                // 授权成功 文件读写权限
                choosePhotoModel.init()
            }
            if (requestCode == PHOTO_TAKE_CODE) {
                dispatchTakePictureIntent()
            }
        }
    }


}