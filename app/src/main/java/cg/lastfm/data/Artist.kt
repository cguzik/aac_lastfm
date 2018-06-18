package cg.lastfm.data

import android.support.v7.util.DiffUtil

import com.google.gson.annotations.SerializedName

data class Artist(

        @SerializedName("mbid")
        val id: String?,

        val name: String,
        val listeners: Long,

        val url: String?,

        @SerializedName("image")
        val imageURLS: List<ImageURL>?,

        @SerializedName("bio")
        val biography: ArtistBiography?,
        val stats: ArtistStats?,
        val similar: ArtistMatches?
) {

    fun getImageURL(size: ImageURL.Size): ImageURL {
        for (imageURL in imageURLS!!) {
            if (size == imageURL.size) {
                return imageURL
            }
        }
        throw RuntimeException("Size " + size.name + " not available for artist " + name)
    }

    companion object {
        var DIFF_CALLBACK: DiffUtil.ItemCallback<Artist> = object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return if (oldItem.id == "") oldItem.name === newItem.name else oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem == newItem
            }
        }
    }
}
