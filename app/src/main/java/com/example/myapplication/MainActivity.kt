package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.view.KeyEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    lateinit var myWebView : WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myWebView = findViewById<WebView>(R.id.web_view)
      //  myWebView.loadUrl("https://www.w3schools.com/")
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = object : WebViewClient(){
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                showMyDialog("Error",
                    "No internet connection. Please check your connection.",
                    this@MainActivity)
                super.onReceivedError(view, request, error)
            }
        }

        myWebView.addJavascriptInterface(WebAppInterface(this), "Android")

        val unencodedHtml = "<html><body><input type=\"button\" value=\"Say hello\" onClick=\"showAndroidToast('Hello Android!')\" />\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "    function showAndroidToast(toast) {\n" +
                "        Android.showToast(toast);\n" +
                "    }\n" +
                "</script>this is my web view</body></html>"
        val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
        myWebView.loadData(encodedHtml, "text/html", "base64")
    }

    private fun showMyDialog(title: String, message: String, context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton("Cancel") { _, _ ->
            this@MainActivity.finish()
        }
        dialog.setNeutralButton("Settings") { _, _ ->
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
        dialog.setPositiveButton("Retry") { _, _ ->
            this@MainActivity.recreate()
        }
        dialog.create().show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()){
            myWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}