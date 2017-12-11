package me.samuki.musicandspeed.models

import android.graphics.Bitmap

/**
 * Created by Samuki on 11.12.2017.
 */
class AudioModel(val id: Int,
                 val audioName: String,
                 val audioArtist: String,
                 val audioDuration: Long,
                 val audioPath: String,
                 val audioCover: Bitmap) {

}