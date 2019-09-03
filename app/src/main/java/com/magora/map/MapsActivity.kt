package com.magora.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.magora.map.ui.MapControlFragment

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, MapControlFragment.createFragment(), null)
            .commitNow()
    }
}
