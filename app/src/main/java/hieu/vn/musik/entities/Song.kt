package hieu.vn.musik.entities


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: String?,
    val name: String?,
    val artist: String?,
    val duration: String?,
    val path: String?,
    val image: String?
): Parcelable






