package com.example.loginandregisterkotlinfragment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.loginandregisterkotlinfragment.R
import com.example.loginandregisterkotlinfragment.databinding.FragmentLoginBinding
import com.example.loginandregisterkotlinfragment.viewModel.MainActivityVM

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mainActivityVM: MainActivityVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityVM = ViewModelProvider(this)[MainActivityVM::class.java]
        mainActivityVM.getBackgroundColorAndIcon()
        mainActivityVM.icon.observe(viewLifecycleOwner) {
            Glide.with(this).load(it).into(binding.icon)
        }
        mainActivityVM.bgColor.observe(viewLifecycleOwner) {
            binding.mainActivityConstraint.setBackgroundColor(it.toColorInt())
            binding.mainActivityConstraint.isVisible = true
        }

        mainActivityVM.checkToken(requireContext()).apply {
                 mainActivityVM.tokenIsChecked.observe(viewLifecycleOwner) {
                     if (it == true) {
                         createFragment()
                     } else {
                         println("no")
                     }
                 }
             }
        binding.loginBtn.setOnClickListener {
        if (binding.emailEdText.text!!.isNotEmpty()&& binding.passwordEdText.text!!.isNotEmpty()){
            mainActivityVM.checkData(binding.emailEdText.text.toString(), binding.passwordEdText.text.toString(), requireContext()).apply {
            mainActivityVM.tokenIsChecked.observe(viewLifecycleOwner) {
                if (it) {
                    createFragment()
                }
            }
            }
        }else{
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
        }
        }
             }
    private fun createFragment() {
        val fragment = AccountPageFragment()
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_fragment_layout, fragment)
        ft.commit()
    }
}



