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
    var words by remember { mutableStateOf(initialWords.shuffled().take(10).toMutableList()) } // Display 10 words
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var wordsTyped by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf<Long?>(null) } // Start time is null initially

    val coroutineScope = rememberCoroutineScope()

    // Coroutine to change words every 5 seconds if not typed
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            if (words.isNotEmpty()) { // Shuffle words only if words remain
                words = words.shuffled().take(10).toMutableList()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Title and Reset Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Typing Speed Test", style = MaterialTheme.typography.headlineMedium)

            // Reset Button
            Button(onClick = {
                words = initialWords.shuffled().take(10).toMutableList() // Reset words
                userInput = TextFieldValue("") // Clear input field
                wordsTyped = 0 // Reset typed word count
                startTime = null // Reset timer
            }) {
                Text("Reset")
            }
        }

        // Words Per Minute Calculation
        val elapsedMinutes = if (startTime != null) {
            ((System.currentTimeMillis() - startTime!!) / 60000.0).coerceAtLeast(0.016)
        } else {
            0.0 // Prevents calculation before typing starts
        }

        val wpm = if (elapsedMinutes > 0) (wordsTyped / elapsedMinutes).toInt() else 0

        Text(text = "Words Per Minute: $wpm", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))

        // Display words using LazyColumn
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(words) { word ->
                Text(text = word, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
            }
        }

        // Typing input field
        TextField(
            value = userInput,
            onValueChange = { newValue ->
                if (startTime == null) { // Start timer when the first word is typed
                    startTime = System.currentTimeMillis()
                }

                userInput = newValue
                val typedWord = newValue.text.trim()
                if (words.contains(typedWord)) {
                    words = words.filterNot { it == typedWord }.toMutableList() // Remove typed word
                    wordsTyped++
                    userInput = TextFieldValue("")

                    // Add a new word dynamically when a word is typed
                    coroutineScope.launch {
                        delay(100) // Smooth transition
                        if (words.size < 10) { // Ensure at least 10 words are visible
                            words = (words + initialWords.filterNot { words.contains(it) }.shuffled().take(1)).toMutableList()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            label = { Text("Type here...") }
        )
    }
}
