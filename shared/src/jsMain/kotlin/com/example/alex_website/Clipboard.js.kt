package com.example.alex_website

actual fun copyToClipboard(text: String) {
    js("navigator.clipboard.writeText(text)")
}
