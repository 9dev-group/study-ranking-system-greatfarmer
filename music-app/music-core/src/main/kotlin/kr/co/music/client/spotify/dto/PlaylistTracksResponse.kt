package kr.co.music.client.spotify.dto

data class PlaylistTracksResponse(
    val href: String,
    val items: List<Item>
)

data class Item(
    val added_at: String,
    val added_by: AddedBy,
    val is_local: Boolean,
    val track: Track
)

data class AddedBy(
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val type: String,
    val uri: String
)

data class ExternalUrls(
    val spotify: String
)

data class Track(
    val name: String,
    val duration_ms: Int,
    val artists: List<Artist>,
    val album: Album,
    val popularity: Int,
    val external_urls: ExternalUrls
)

data class Artist(
    val name: String,
    val external_urls: ExternalUrls
)

data class Album(
    val name: String,
    val release_date: String,
    val external_urls: ExternalUrls
)