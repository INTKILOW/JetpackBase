package top.intkilow.feat.page.qr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import top.intkilow.architecture.nav.NavControllerHelper
import top.intkilow.architecture.ui.SnackbarUtil
import top.intkilow.architecture.utils.StatusBarUtil
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.feat.databinding.FeatQrscanPageBinding
import top.intkilow.feat.page.photo.choose.CHOOSE_PHOTO_RESULT_DATA
import top.intkilow.feat.qrscan.Decoder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


const val SCAN_RESULT_DATA = "scan_result_data"

class QRScanPage : Fragment() {

    private val qrScanModel: QRScanModel by viewModels()

    private var camera: Camera? = null
    private var thread: Thread? = null
    private val decoder = Decoder()
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val CAMERA_CODE = 31
    private val SD_CODE = 32
    private val RC_CHOOSE_PHOTO = 33
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: FeatQrscanPageBinding

    companion object {
        var borderColor = ViewUtils.colorPrimary
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        StatusBarUtil.setStatusBarColor(window = activity?.window,false)
        binding = FeatQrscanPageBinding.inflate(inflater, container, false)


        binding.lifecycleOwner = viewLifecycleOwner
        binding.homeModel = qrScanModel


        qrScanModel.flashMode.observe(viewLifecycleOwner, {
            camera?.cameraControl?.enableTorch(it)
        })
        binding.viewfinderView.setColor(borderColor, borderColor, borderColor)
        if(ViewUtils.needStatusBar){
            val layoutParams = binding.backLayout.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = ViewUtils.getStatusBarHeight(binding.backLayout.context)
        }

        binding.backLayout.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.featurePhoto.setOnClickListener {
            context?.let {
                val sdPermission = Manifest.permission.READ_EXTERNAL_STORAGE
                val hasSDPermission = hasPermission(it, sdPermission)
                if (hasSDPermission) {
                    requestPermissions(arrayOf(sdPermission), SD_CODE)
                } else {
                    photo()
                }
            }
        }
        binding.featureFlash.setOnClickListener {
            qrScanModel.flashMode.value?.let {
                qrScanModel.flashMode.value = (!it)
            }
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        context?.let {
            val cameraPermission = Manifest.permission.CAMERA
            val hasCameraPermission = hasPermission(it, cameraPermission)
            if (hasCameraPermission) {
                initCamera(it)
            } else {
                requestPermissions(arrayOf(cameraPermission), CAMERA_CODE)
            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (null != data) {

                data.data?.let {
                    qrScanModel.analysisFromPhoto.value = true
                    qrScanModel.parseQRFromUri(it).observe(viewLifecycleOwner) { data ->
                        if (data.isNullOrBlank()) {
                            qrScanModel.analysisFromPhoto.value = false
                            SnackbarUtil.toast(binding.previewView, "解码失败!")
                        } else {
                            finish(data)
                        }
                    }
                } ?: run {
                    qrScanModel.analysisFromPhoto.value = true
                }


            } else {
                qrScanModel.analysisFromPhoto.value = false
            }


        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var pass = true
        grantResults.forEach {
            if (it != PermissionChecker.PERMISSION_GRANTED) {
                pass = false
            }
        }
        if (!pass) {
            Snackbar.make(binding.previewView, "请求权限失败", 1).show()
        } else {
            if (requestCode == CAMERA_CODE) {
                context?.let { initCamera(it) }
            } else if (requestCode == SD_CODE) {
                photo()
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        thread?.interrupt()
    }


    private fun initCamera(context: Context) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, mAnalyzer)
        camera = cameraProvider.bindToLifecycle(
            viewLifecycleOwner,
            cameraSelector, imageAnalysis, preview
        )

    }

    private val mAnalyzer = ImageAnalysis.Analyzer { imageProxy ->
        qrScanModel.analysisFromPhoto.value?.let {
            if (it) {
                imageProxy.close()
                return@Analyzer
            }
        }

        val result = decoder.decode(imageProxy)
        if (null != result) {
            activity?.runOnUiThread {
                finish(result.text)
            }
        } else {
            imageProxy.close()
        }

    }


    private fun finish(result: String) {

        NavControllerHelper.setSavedStateHandle(
            this,
            SCAN_RESULT_DATA,
            result,
            arguments?.getInt("destinationId", -1) ?: -1
        )
        findNavController().navigateUp()
    }


    private fun photo() {
        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO)
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(context, permission)
    }
}