package com.example.typingspeedtest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TypingSpeedTest(initialWords: List<String>) {
    var words by remember { mutableStateOf(initialWords.shuffled().take(5).toMutableList()) }
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var wordsTyped by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val coroutineScope = rememberCoroutineScope()

    // Coroutine to change words every 5 seconds if not typed
    LaunchedEffect(words) {
        while (true) {
            delay(5000)
            words = words.shuffled().take(5).toMutableList()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Typing Speed Test", style = MaterialTheme.typography.headlineMedium)

        // Words Per Minute Calculation
        val elapsedMinutes = (System.currentTimeMillis() - startTime) / 60000.0
        val wpm = if (elapsedMinutes > 0) (wordsTyped / elapsedMinutes).toInt() else 0

        Text(text = "Words Per Minute: $wpm", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(words) { word ->
                Text(text = word, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
            }
        }

        TextField(
            value = userInput,
            onValueChange = { newValue ->
                userInput = newValue
                if (words.contains(newValue.text.trim())) {
                    words = words.filterNot { it == newValue.text.trim() }.toMutableList()
                    wordsTyped++
                    userInput = TextFieldValue("")
                    coroutineScope.launch {
                        delay(500)
                        if (words.isEmpty()) {
                            words = initialWords.shuffled().take(5).toMutableList()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            label = { Text("Type here...") }
        )
    }
}
