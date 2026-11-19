package com.example.alex_website

private fun writeClipboard(text: String): Unit = js("{ navigator.clipboard.writeText(text); }")

actual fun copyToClipboard(text: String) = writeClipboard(text)
