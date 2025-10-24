package ipt.pt.dam2025.teste_002.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.funapp.R
import java.io.File

class CameraFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var takePhotoButton: Button
    private lateinit var photoFile: File
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        imageView = view.findViewById(R.id.imageView)
        takePhotoButton = view.findViewById(R.id.btnTakePhoto)

        takePhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        return view
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            photoFile = File.createTempFile("photo_", ".jpg", requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
            val photoURI = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(Uri.fromFile(photoFile))
        }
    }
}
