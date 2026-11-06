package com.example.luminascout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class ChatMessage(val text: String, val fromUser: Boolean)

@Composable
fun AIAssistantScreen(onBackClick: () -> Unit) {
    val messages = remember { mutableStateListOf(ChatMessage("Welcome to LuminaScout! Ask about lighting, camera settings, or planning a shoot. These are starter tips, not live AI responses.", false)) }
    var draft by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(10.dp))
            Text("LuminaScout Assistant", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(messages) { message ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.86f),
                        shape = RoundedCornerShape(16.dp),
                        color = if (message.fromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ) {
                        Text(message.text, Modifier.padding(14.dp), color = if (message.fromUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text("Ask a photography question") },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )
            Spacer(Modifier.width(8.dp))
            IconButton(enabled = draft.isNotBlank(), onClick = {
                val question = draft.trim()
                messages.add(ChatMessage(question, true))
                messages.add(ChatMessage(starterPhotographyAdvice(question), false))
                draft = ""
            }) { Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary) }
        }
    }
}

// Temporary offline guidance. Replace with a secure backend request for real AI chat.
private fun starterPhotographyAdvice(question: String): String {
    val q = question.lowercase()
    return when {
        "sunset" in q || "golden hour" in q -> "For golden hour, try shooting during the hour before sunset. Start around ISO 100–400, f/2.8–f/5.6 for portraits, and adjust shutter speed to avoid blur. Check your exposure as light changes."
        "wind" in q -> "Wind can move hair, clothing, and your camera. Use a faster shutter speed for moving subjects, find shelter where possible, and secure stands or reflectors."
        "night" in q || "low light" in q -> "For low light, open your aperture, raise ISO as needed, and keep shutter speed fast enough for your subject. A tripod helps for stationary scenes."
        "portrait" in q || "maternity" in q -> "For portraits, start with a wide aperture such as f/2.8–f/4 if your lens allows it, focus on the eyes, and look for soft, directional light."
        "iso" in q || "aperture" in q || "shutter" in q || "settings" in q -> "Camera settings depend on the scene. For daylight portraits, try ISO 100–400, f/2.8–f/5.6, and 1/250s or faster. Adjust based on your lens, subject movement, and exposure."
        "weather" in q || "cloud" in q || "rain" in q -> "Cloudy skies often provide flattering soft light. Check the live Weather tab for current wind and rain before deciding on an outdoor shoot."
        else -> "I can currently offer starter tips about golden hour, portraits, camera settings, wind, and low light. Open-ended AI chat is coming once the secure AI backend is connected."
    }
}
