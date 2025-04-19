package kr.co.music.client.spotify.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("expires_in")
    val expiresIn: Int
)