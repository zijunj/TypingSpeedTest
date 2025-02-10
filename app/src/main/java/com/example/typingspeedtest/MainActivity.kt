package com.example.typingspeedtest

import android.content.res.XmlResourceParser
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.xmlpull.v1.XmlPullParser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val words = loadWordsFromXml()

        setContent {
            TypingSpeedTest(words)
        }
    }

    private fun loadWordsFromXml(): List<String> {
        val words = mutableListOf<String>()
        val parser: XmlResourceParser = resources.getXml(R.xml.typingwords)

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.TEXT) {
                words.add(parser.text.trim())
            }
            parser.next()
        }
        return words
    }
}
