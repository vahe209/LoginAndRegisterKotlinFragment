package com.example.loginandregisterkotlinfragment.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.loginandregisterkotlinfragment.R
import com.example.loginandregisterkotlinfragment.api.Api.Companion.IMG_BASE_URL
import com.example.loginandregisterkotlinfragment.databinding.FragmentAccountPageBinding
import com.example.loginandregisterkotlinfragment.pathUtil.RealPathUtil
import com.example.loginandregisterkotlinfragment.viewModel.MainActivityVM
import com.example.loginandregisterkotlinfragment.viewModel.SharedPreferencesDataBase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

@Suppress("DEPRECATION")
class AccountPageFragment : Fragment() {
    private lateinit var binding: FragmentAccountPageBinding
    private lateinit var mainActivityVM: MainActivityVM
    private lateinit var map: HashMap<String, String>
    private lateinit var email: String
    private var uri: Uri? = null
    var path: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { 
        binding = FragmentAccountPageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityVM = ViewModelProvider(this)[MainActivityVM::class.java]
        val sharedPreferencesDataBase = SharedPreferencesDataBase(requireContext())
        if (arguments != null) {
            map = ((arguments?.getSerializable("map") as HashMap<String, String>))
            binding.firstName.setText(map["first_name"])
            binding.lastName.setText(map["last_name"])
            binding.email.setText(map["email"])
            binding.phone.setText(map["phone"])
        } else {
            putData()
        }
        binding.logOut.setOnClickListener {
            sharedPreferencesDataBase.delToken()
            val fragment = LoginFragment()
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_fragment_layout, fragment)
            ft.commit()
        }
        binding.changeUserDataBtn.text = "change user data"
        binding.changeUserDataBtn.setOnClickListener {
            binding.editLayout.isVisible = true
            binding.cancelChangesUserDataBtn.isVisible = true
            makeEditTextChangeable(true)
        }
        binding.cancelChangesUserDataBtn.setOnClickListener {
            binding.editLayout.isVisible = false
            binding.cancelChangesUserDataBtn.isVisible = false
            makeEditTextChangeable(false)
            putData()
        }
        makeEditTextChangeable(false)
        binding.submitBtn.setOnClickListener {
            if (binding.submitEdit.text.isNotEmpty()) {
                val map = mapOf(
                    "first_name" to binding.firstName.text.toString(),
                    "last_name" to binding.lastName.text.toString(),
                    "email" to binding.email.text.toString(),
                    "phone" to binding.phone.text.toString()
                )
                mainActivityVM.checkData(
                    email,
                    binding.submitEdit.text.toString(),
                    requireContext()
                ).apply {
                    mainActivityVM.updateUserData(map as HashMap<String, String>, requireContext())
                        .apply {
                            putData()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Write password", Toast.LENGTH_SHORT).show()
            }
        }
        binding.addImgPng.setOnClickListener {
            loadImage()
        }
        binding.savePng.setOnClickListener {
            uploadImage()
        }
    }
    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        launcher.launch(intent)
    }
    private var launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data
            uri = data!!.data
            path = RealPathUtil.getRealPath(requireContext(), uri)
            try {
                @Suppress("DEPRECATION") val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                binding.accountImg.setImageBitmap(bitmap)
                binding.savePng.isVisible = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 100) {
                binding.accountImg.setImageURI(data!!.data)
            }
        }
    }

    private fun putData() {
        mainActivityVM.getData(context).apply {
            mainActivityVM.firstName.observe(viewLifecycleOwner) {
                binding.firstName.setText(it.toString())
            }
            mainActivityVM.lastName.observe(viewLifecycleOwner) {
                binding.lastName.setText(it.toString())
            }
            mainActivityVM.email.observe(viewLifecycleOwner) {
                binding.email.setText(it.toString())
                email = it.toString()
            }
            mainActivityVM.phone.observe(viewLifecycleOwner) {
                binding.phone.setText(it.toString())
            }
            mainActivityVM.file.observe(viewLifecycleOwner) {
                Glide.with(requireContext()).load(IMG_BASE_URL + it)
                    .into(binding.accountImg)
            }
        }
    }

    private fun makeEditTextChangeable(isClicked: Boolean) {
        binding.firstName.isFocusableInTouchMode = isClicked
        binding.lastName.isFocusableInTouchMode = isClicked
        binding.email.isFocusableInTouchMode = isClicked
        binding.phone.isFocusableInTouchMode = isClicked
        if (!isClicked) {
            binding.firstName.clearFocus()
            binding.lastName.clearFocus()
            binding.email.clearFocus()
            binding.phone.clearFocus()
        }
    }

    private fun uploadImage() {
        val f = File(path!!)
        val reqFile =f.asRequestBody(context?.contentResolver!!.getType(uri!!)?.toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", f.name, reqFile)
        mainActivityVM.uploadImage(body, requireContext())
    }
}




