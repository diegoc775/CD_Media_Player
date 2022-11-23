package com.example.cdmediaplayer.HelperClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmediaplayer.MainActivity
import com.example.cdmediaplayer.MusicPlayerActivity
import com.example.cdmediaplayer.R
import java.io.File
//By Diego Cobos
//RecyclerView Adapter for the displaying of the internal file system
class RecyclerViewAdapter(private var filesAndFolders:List<File>, private var context: Context):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false);
        return ViewHolder(view);
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        var selectedFile = filesAndFolders.get(position)

        if(selectedFile.isDirectory){
            holder.flText.text = selectedFile.name
            holder.flIcon.setImageResource(R.drawable.music_album_icon)
        }
        else{
            if(!selectedFile.name.subSequence(0,1).isDigitsOnly()){
                holder.flText.text = selectedFile.name.subSequence(0,selectedFile.name.length-4)
            }
            else{
                holder.flText.text = selectedFile.name.subSequence(2,selectedFile.name.length-4)
            }

           holder.flIcon.setImageResource(R.drawable.song_icon)
        }
        holder.itemView.setOnClickListener{
            if(selectedFile.isDirectory){
                val intent = Intent(context,  MainActivity::class.java)
                val path = selectedFile.absolutePath
                intent.putExtra("path", path)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val arrOfStr: List<String> = path.split("/")
                intent.putExtra("title", arrOfStr[arrOfStr.size-1])
                context.startActivity(intent)
            }
            else{
                var songPlaylist = ArrayList<File>()
                for(tempFile in filesAndFolders){
                    if(!tempFile.isDirectory && tempFile.extension == "mp3"){
                        songPlaylist += tempFile
                    }
                }

                val intent = Intent(context,  MusicPlayerActivity::class.java)
                intent.putExtra("songList", songPlaylist)
                intent.putExtra("songPosition", position)
                context.startActivity(intent)

            }
        }
    }

    override fun getItemCount(): Int {
        return filesAndFolders.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
       var flIcon = ItemView.findViewById<ImageView>(R.id.icon_view)
       var flText = ItemView.findViewById<TextView>(R.id.file_name_text_view)
    }
}