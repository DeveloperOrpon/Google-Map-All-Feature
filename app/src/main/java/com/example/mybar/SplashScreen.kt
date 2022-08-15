package com.example.mybar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        var animation= AnimationUtils.loadAnimation(applicationContext,R.anim.up_animation)
        tvWelcome.animation=animation
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)

    }
}