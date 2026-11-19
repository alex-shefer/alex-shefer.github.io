package com.example.alex_website

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun copyToClipboard(text: String)
