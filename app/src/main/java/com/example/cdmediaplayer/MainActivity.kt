package com.example.cdmediaplayer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmediaplayer.HelperClasses.RecyclerViewAdapter
import com.example.cdmediaplayer.HelperClasses.Utils
import java.io.File
//By Diego Cobos
//This project is an internal file system media player for any device that
//does not come equipped with a default option.

//Activity where all needed files are displayed, filtered, and sent to the
//recyclerView Adapter.
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {

        }
        setContentView(R.layout.activity_main)

        if(!(Utils()isPermissionGranted(this))){
            requestPermission()
        }
        else{
            val actionBar: ActionBar?
            actionBar = supportActionBar
            val colorDrawable = ColorDrawable(Color.parseColor("#081E3F"))
            actionBar?.setBackgroundDrawable(colorDrawable)

            val bundle = intent.extras
            val innerDirectoryPath:String? = bundle?.getString("path")
            if(innerDirectoryPath == null){
                val txtNone = findViewById<TextView>(R.id.txtNoMusic)
                var path :String = Environment.getExternalStorageDirectory().path
                var recyclerView = findViewById<RecyclerView>(R.id.fileRecyclerView)
                ////////////////////////
                path = path+"/Music"
                ///////////////////////
                var root = File(path)
                val filesAndFolders = root.listFiles()
                var filteredFAndF: List<File> = arrayListOf<File>()

                for(tempFile in filesAndFolders){
                    if(tempFile.isDirectory || tempFile.extension == "mp3"){
                        filteredFAndF += tempFile
                    }
                }

                if(filteredFAndF == null || filteredFAndF.size == 0){
                    txtNone.setVisibility(View.VISIBLE)
                    Toast.makeText(this, "Empty folder!!!!!!!!!!!!!", Toast.LENGTH_LONG).show()
                    return
                }
                recyclerView.layoutManager = (LinearLayoutManager(this))
                recyclerView.adapter = RecyclerViewAdapter(filteredFAndF, this)
            }
            else{
                val txtNone = findViewById<TextView>(R.id.txtNoMusic)
                val title = bundle.getString("title")
                this.setTitle(title)
                var path :String = innerDirectoryPath
                var recyclerView = findViewById<RecyclerView>(R.id.fileRecyclerView)
                var root = File(path)
                var filesAndFolders = root.listFiles()
                var filteredFAndF: List<File> = arrayListOf<File>()
                for(tempFile in filesAndFolders){
                    if(tempFile.isDirectory || tempFile.extension == "mp3"){
                        filteredFAndF += tempFile
                    }
                }
                if(filteredFAndF == null || filteredFAndF.size == 0){
                    txtNone.setVisibility(View.VISIBLE)
                    return
                }
                filteredFAndF.sorted()
                recyclerView.layoutManager = (LinearLayoutManager(this))
                recyclerView.adapter = RecyclerViewAdapter(filteredFAndF, this)
            }
        }
    }

    private fun requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
           Toast.makeText(this, "permission is required. allow in settings", Toast.LENGTH_LONG).show()
            return
        }
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),111 )
    }
}