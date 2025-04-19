package kr.co.music.client.spotify

import feign.Headers
import kr.co.music.client.spotify.dto.PlaylistTracksResponse
import kr.co.music.client.spotify.dto.SpotifyTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "spotifyAccountClient",
    url = "\${spotify.account.url}"
)
interface SpotifyAccountClient {
    @PostMapping("/api/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getToken(
        @RequestParam("grant_type") grantType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String
    ): SpotifyTokenResponse
}

@FeignClient(
    name = "spotifyApiClient",
    url = "\${spotify.api.url}"
)
interface SpotifyApiClient {
    @GetMapping("/v1/playlists/{playlistId}/tracks")
    fun getPlaylistTracks(
        @RequestHeader("Authorization") authorization: String,
        @RequestParam("market") market: String = "KR",
        @RequestParam("playlistId") playlistId: String
    ): PlaylistTracksResponse
}