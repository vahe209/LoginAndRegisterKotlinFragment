package com.example.loginandregisterkotlinfragment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.loginandregisterkotlinfragment.R
import com.example.loginandregisterkotlinfragment.databinding.FragmentRegisterBinding
import com.example.loginandregisterkotlinfragment.viewModel.MainActivityVM
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mainActivityVM: MainActivityVM
    private var map = HashMap<String, String>()
    private var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityVM = ViewModelProvider(this)[MainActivityVM::class.java]
        binding.registerBtn.setOnClickListener {
            if (isNotEmpty() && checkEmail(binding.email.text.toString())&&isValidPass(binding.password.text.toString(), binding.confPass.text.toString())) {
                map = mapOf(
                    ("first_name" to binding.firstName.text.toString()),
                    ("last_name" to binding.lastName.text.toString()),
                    ("email" to binding.email.text.toString()),
                    ("phone" to binding.phone.text.toString()),
                    ("password" to binding.password.text.toString())
                ) as HashMap<String, String>
                mainActivityVM.map.apply {
                    this.putAll(map)
                    mainActivityVM.post(requireContext())
                }
                mainActivityVM.isSucceed.observe(viewLifecycleOwner) {
                    if (it == true) {
                        bundle.putSerializable("map", map)
                        createFragment()
                    }
                }
            }
        }
    }

    @Suppress("UNREACHABLE_CODE")
    private fun isValidPass(password: String, confPass:String):Boolean{
        if (confPass == password) {
            val regex = "^(?=.*\\d)" +
                    "(?=.*[a-z])(?=.*[A-Z])" +
                    "(?=.*[!@#$%^&*()_+~`<>?:{}])" +
                    "(?=\\S+$).{8,20}$"
            val p: Pattern = Pattern.compile(regex)
            val m: Matcher = p.matcher(password)
            return if (m.matches()) {
                true
            } else {
                Toast.makeText(
                    requireContext(), "Password must be contain Z-z, 1 capital letter and symbol", Toast.LENGTH_SHORT).show()
                false
            }
             return true
        }else{
            Toast.makeText(requireContext(), "Passwords must be same", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    private fun checkEmail(email:String):Boolean {
        val regex = "^[A-Za-z\\d+_.-]+@(.+)$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(email)
        return if (matcher.matches()) {
            true
        } else {
            Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show()
            false
        }
    }
    private fun isNotEmpty(): Boolean {
        return if (
            binding.firstName.text.isNotEmpty() &&
            binding.lastName.text.isNotEmpty() &&
            binding.email.text.isNotEmpty() &&
            binding.phone.text.isNotEmpty() &&
            binding.password.text.isNotEmpty() &&
            binding.confPass.text.isNotEmpty()){
            true
        }else {
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            false
        }
    }
    private fun createFragment() {
        val fragment = AccountPageFragment()
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_fragment_layout, fragment)
        fragment.arguments = (bundle)
        ft.commit()
    }
}
