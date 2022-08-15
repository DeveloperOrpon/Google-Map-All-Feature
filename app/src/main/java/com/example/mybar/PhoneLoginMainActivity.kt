package com.example.mybar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_phone_login_main.*

private const val TAG="PhoneLoginMainActivity"
var selectedCountycode: String ="+880"
var PhoneNumber: String =""
class PhoneLoginMainActivity : AppCompatActivity() {
    lateinit var countryCodePicker: CountryCodePicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login_main)
        etCountryCode.setOnCountryChangeListener {
            selectedCountycode=etCountryCode.selectedCountryCodeWithPlus
        }
        etPhoneNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(TAG,"beforeTextChanged : $p0")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(TAG,"onTextChanged : $p0")
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG,"afterTextChanged : $p0")
                if (p0?.length==10){
                    verificationBox.visibility=View.VISIBLE
                    inputBoxId.visibility=View.GONE
                }
            }

        })
    }
}