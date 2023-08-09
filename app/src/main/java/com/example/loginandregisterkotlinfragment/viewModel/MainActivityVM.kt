package com.example.loginandregisterkotlinfragment.viewModel


import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loginandregisterkotlinfragment.api.Api
import com.example.loginandregisterkotlinfragment.api.Api.Companion.KEY
import com.example.loginandregisterkotlinfragment.data.ColorsGet
import com.example.loginandregisterkotlinfragment.data.GetIconFromDirectory
import com.example.loginandregisterkotlinfragment.data.UserDataInfo
import com.example.loginandregisterkotlinfragment.data.UserDataModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityVM : ViewModel() {
    private lateinit var password: String
    var bgColor = MutableLiveData<String>()
    var icon = MutableLiveData<String>()
    private var api = Api.create()
    var map: MutableMap<String, String> = mutableMapOf()
    var isSucceed = MutableLiveData<Boolean>()
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var file = MutableLiveData<String>()
    var phone = MutableLiveData<String>()
    var tokenIsChecked = MutableLiveData<Boolean>()

    fun getBackgroundColorAndIcon() {
        val colorList = HashMap<String, String>()
        api.getColors(KEY).enqueue(object : Callback<ColorsGet?> {
            override fun onResponse(call: Call<ColorsGet?>, response: Response<ColorsGet?>) {
                for (x in 0 until response.body()!!.allColors.size) {
                    colorList[response.body()!!.allColors[x].name] =
                        response.body()!!.allColors[x].value
                }
                bgColor.value = "#${colorList["accentMain"].toString()}"
            }
            override fun onFailure(call: Call<ColorsGet?>, t: Throwable) {
                println(t.message)
            }
        })
        api.getIconMainPage(KEY).enqueue(object : Callback<GetIconFromDirectory> {
            override fun onResponse(
                call: Call<GetIconFromDirectory>, response: Response<GetIconFromDirectory>
            ) {
                if (response.isSuccessful) {
                    icon.value = Api.IMG_BASE_URL + response.body()!!.icons.icon
                }
            }
            override fun onFailure(call: Call<GetIconFromDirectory>, t: Throwable) {
                println(t.message)
            }
        })
    }
    fun checkToken(context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        val token: String = sharedPreferencesDataBase.getToken()
        tokenIsChecked.value = token.isNotEmpty()
    }
    fun checkData(email: String, password: String, context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        api.loginUser(KEY, email, password).enqueue(object : Callback<UserDataModel> {
            override fun onResponse(call: Call<UserDataModel>, response: Response<UserDataModel>) {
                if (response.isSuccessful) {
                    tokenIsChecked.value = true
                    sharedPreferencesDataBase.saveToken(response.body()!!.token)
                    println("true")
                } else Toast.makeText(context, "Something happens", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                println(t.message)
            }
        })
    }
    fun post(context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        if (map.isNotEmpty()) {
            password = map["password"].toString()
            api.postData(KEY, map).enqueue(object : Callback<UserDataModel> {
                override fun onResponse(
                    call: Call<UserDataModel>,
                    response: Response<UserDataModel>
                ) {
                    if (response.isSuccessful) {
                        isSucceed.value = true
                        sharedPreferencesDataBase.saveToken(response.body()!!.token)
                    } else {
                        isSucceed.value = false
                        println("Error")
                    }
                }
                override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                    println(t.message)
                }
            })
        }
    }
    fun getData(context: Context?) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context!!)
        val token = sharedPreferencesDataBase.getToken()
        if (token.isNotEmpty()) {
            api.checkTokenForLogin(KEY, token).enqueue(object : Callback<UserDataInfo> {
                override fun onResponse(call: Call<UserDataInfo>,response: Response<UserDataInfo>) {
                    if (response.isSuccessful) {
                        firstName.value = response.body()!!.userDataModel.first_name
                        lastName.value = response.body()!!.userDataModel.last_name
                        phone.value = response.body()!!.userDataModel.phone
                        email.value = response.body()!!.userDataModel.email
                        file.value = response.body()!!.userDataModel.image
                    } else {
                        println("Error")
                    }
                }
                override fun onFailure(call: Call<UserDataInfo>, t: Throwable) {
                    println(t.message)
                }
            })
        }
    }
    fun uploadImage(body: MultipartBody.Part, context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        val token = sharedPreferencesDataBase.getToken()
        val call = api.upload(KEY, token, body)
        call!!.enqueue(object : Callback<UserDataModel?> {
            override fun onResponse(
                call: Call<UserDataModel?>, response: Response<UserDataModel?>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Image Successfully added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Something gone incorrect", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDataModel?>, t: Throwable) {
                println(t.message)
            }
        })
    }
    fun updateUserData(map: HashMap<String, String>, context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        val token = sharedPreferencesDataBase.getToken()
        api.updateUserData(KEY, token, map).enqueue(object : Callback<UserDataModel> {
            override fun onResponse(call: Call<UserDataModel>, response: Response<UserDataModel>) {
                if (response.isSuccessful) {
                    firstName.value = response.body()!!.first_name
                    lastName.value = response.body()!!.last_name
                    phone.value = response.body()!!.phone
                    email.value = response.body()!!.email
                    Toast.makeText(context, "Data is loaded", Toast.LENGTH_SHORT).show()
                } else println("ERROR")
            }

            override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                println(t.message)
            }
        })
    }
}
