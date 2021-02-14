package go.goskate.goskate.helper.dialogs

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import go.goskate.goskate.R
import go.goskate.goskate.ui.viewmodel.MapsViewModel
import kotlinx.android.synthetic.main.custom_dialog_post.view.*

class CaptureFilesNewSpotDialogFragment : DialogFragment() {

    private val REQUEST_VIDEO_CAPTURE = 101
    val REQUEST_IMAGE_CAPTURE = 102
    val REQUEST_PERMISION_CODE = 100
    private val mapsViewModel: MapsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.custom_dialog_post, container, false)

        rootView.openVideoConstraintLayout.setOnClickListener {
            permission().observe(this, {
                if (it) {
                    selectImageProfile()
                }
            })
        }

        rootView.openCameraPhotoConstraintLayout.setOnClickListener {
            permission().observe(this, {
                if (it) {
                    dispatchTakePictureIntent()
                }
            })
        }

        rootView.openVideoConstraintLayout.setOnClickListener {
            permission().observe(this, {
                if (it) {
                }
            })
        }
        return rootView

    }

    private fun selectImageProfile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            mapsViewModel.imagesNewSpot.value = imageBitmap
            dismiss()
        }

    }


    private fun permission(): MutableLiveData<Boolean> {
        val permissionResponse = MutableLiveData<Boolean>()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionResponse.value = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        }
        return permissionResponse
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (REQUEST_PERMISION_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "permiso concedido", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "permiso denegado", Toast.LENGTH_LONG).show()
            }
        }
    }
}