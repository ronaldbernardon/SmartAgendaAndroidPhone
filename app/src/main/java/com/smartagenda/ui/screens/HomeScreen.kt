package com.smartagenda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartagenda.data.model.Event
import com.smartagenda.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📅 SmartAgenda v1.0") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chargement...")
                    }
                }
                
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "❌ Erreur",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(uiState.error ?: "Erreur inconnue")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Réessayer")
                        }
                    }
                }
                
                uiState.dailySummary != null -> {
                    val summary = uiState.dailySummary!!
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date du jour
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        uiState.currentDate,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${summary.totalEvents} événement(s) aujourd'hui",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        
                        // Widget UV
                        if (summary.uvIndex != null) {
                            item {
                                UVWidget(
                                    uvIndex = summary.uvIndex,
                                    uvLevel = summary.uvLevel ?: "Faible"
                                )
                            }
                        }
                        
                        // Widget Météo
                        if (summary.weatherDescription != null) {
                            item {
                                WeatherWidget(
                                    tempMin = summary.tempMin ?: 0.0,
                                    tempMax = summary.tempMax ?: 0.0,
                                    description = summary.weatherDescription,
                                    icon = summary.weatherIcon ?: "☀️"
                                )
                            }
                        }
                        
                        // Liste des événements
                        if (summary.events.isNotEmpty()) {
                            items(summary.events) { event ->
                                EventCard(event)
                            }
                        } else {
                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "✅ Aucun événement aujourd'hui",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UVWidget(
    uvIndex: Double,
    uvLevel: String
) {
    val (backgroundColor, textColor, emoji) = when {
        uvIndex <= 2 -> Triple(Color(0xFFA5D6A7), Color(0xFF2E7D32), "🟢")
        uvIndex <= 5 -> Triple(Color(0xFFFFF9C4), Color(0xFFF57F17), "🟡")
        uvIndex <= 7 -> Triple(Color(0xFFFFCC80), Color(0xFFE65100), "🟠")
        uvIndex <= 10 -> Triple(Color(0xFFEF9A9A), Color(0xFFC62828), "🔴")
        else -> Triple(Color(0xFFCE93D8), Color(0xFF6A1B9A), "🟣")
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "☀️ Indice UV",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    uvLevel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    emoji,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    String.format("%.1f", uvIndex),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun WeatherWidget(
    tempMin: Double,
    tempMax: Double,
    description: String,
    icon: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "🌡️ Météo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1976D2)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    icon,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${tempMin.toInt()}° / ${tempMax.toInt()}°",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    // Déterminer si l'événement est passé
    val now = Date()
    val eventDateTime = try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRENCH).parse(event.startDate)
    } catch (e: Exception) {
        null
    }
    val isPast = eventDateTime?.before(now) ?: false
    
    // Couleur selon la catégorie
    val categoryColor = when (event.category?.lowercase()) {
        "travail" -> Color(0xFF3498DB)
        "personnel" -> Color(0xFF27AE60)
        "réunion" -> Color(0xFF9B59B6)
        "loisirs" -> Color(0xFFF39C12)
        "santé" -> Color(0xFFE74C3C)
        else -> Color(0xFF95A5A6)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) Color(0xFFF5F5F5) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Barre de couleur à gauche
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(
                        color = if (isPast) Color(0xFFBDC3C7) else categoryColor,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isPast) Color(0xFF7F8C8D) else MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (event.time != null) {
                        Text(
                            event.time,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isPast) Color(0xFFBDC3C7) else categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (!event.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPast) Color(0xFFBDC3C7) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge catégorie
                    Surface(
                        color = if (isPast) Color(0xFFECF0F1) else categoryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            event.category ?: "Non défini",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPast) Color(0xFF95A5A6) else categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Badge récurrent
                    if (event.recurring == true) {
                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "🔄 Récurrent",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Badge rappel
                    if (event.reminder != null && event.reminder != "none") {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "🔔 Rappel",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF57C00),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Badge "Passé"
                    if (isPast) {
                        Surface(
                            color = Color(0xFFECF0F1),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "✓ Passé",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF95A5A6),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
