package com.parshuram.testytplayer

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlEditText: EditText
    private lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlEditText = findViewById(R.id.url_edit_text)
        playButton = findViewById(R.id.play_button)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.webViewClient = WebViewClient()

        playButton.setOnClickListener {
            val inputUrl = urlEditText.text.toString().trim()

            val embedUrl = if (inputUrl.contains("/embed/")) {
                inputUrl
            } else {
                val videoId = extractYouTubeId(inputUrl)
                if (videoId != null) {
                    "https://www.youtube.com/embed/$videoId"
                } else {
                    null
                }
            }

            if (embedUrl != null) {
                val finalUrl = "$embedUrl?autoplay=1&mute=1"

                val myHtml = """
                    <!DOCTYPE html>
                    <html>
                      <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1">
                      </head>
                      <body style="margin:0;padding:0;">
                        <iframe width="100%" height="100%"
                          src="$finalUrl"
                          frameborder="0"
                          allow="autoplay;"
                          allowfullscreen>
                        </iframe>
                      </body>
                    </html>
                """.trimIndent()

                webView.loadDataWithBaseURL(
                    "https://www.youtube.com",
                    myHtml,
                    "text/html",
                    "UTF-8",
                    null
                )
            } else {
                urlEditText.error = "Invalid YouTube URL!"
            }
        }
    }


    private fun extractYouTubeId(url: String): String? {
        val regex = Regex("(?:v=|be/)([a-zA-Z0-9_-]{11})")
        val match = regex.find(url)
        return match?.groups?.get(1)?.value
    }
}
