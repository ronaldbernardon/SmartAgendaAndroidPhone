package com.smartagenda.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartagenda.ui.viewmodel.SetupUiState
import com.smartagenda.ui.viewmodel.SetupViewModel

@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel(),
    onSetupComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isConfigured) {
        if (uiState.isConfigured) {
            onSetupComplete()
        }
    }

    SetupContent(
        uiState = uiState,
        onServerUrlChange = viewModel::updateServerUrl,
        onPasswordChange = viewModel::updatePassword,
        onNotificationTimeChange = viewModel::updateNotificationTime,
        onTestConnection = viewModel::testConnection,
        onSaveConfiguration = viewModel::saveConfiguration,
        onClearError = viewModel::clearError,
        onClearTestResult = viewModel::clearConnectionTestResult
    )
}

@Composable
private fun SetupContent(
    uiState: SetupUiState,
    onServerUrlChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNotificationTimeChange: (Int, Int) -> Unit,
    onTestConnection: () -> Unit,
    onSaveConfiguration: () -> Unit,
    onClearError: () -> Unit,
    onClearTestResult: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configuration SmartAgenda",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = uiState.serverUrl,
            onValueChange = onServerUrlChange,
            label = { Text("URL du serveur") },
            placeholder = { Text("http://192.168.1.x:8086") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Mot de passe") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            onClick = onTestConnection,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isTestingConnection && uiState.serverUrl.isNotBlank() && uiState.password.isNotBlank()
        ) {
            if (uiState.isTestingConnection) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (uiState.isTestingConnection) "Test en cours..." else "Tester la connexion")
        }

        uiState.connectionTestResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    IconButton(onClick = onClearTestResult) {
                        Text("✕")
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Heure de notification quotidienne",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        var showTimePicker by remember { mutableStateOf(false) }

        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("${uiState.notificationHour.toString().padStart(2, '0')}:${uiState.notificationMinute.toString().padStart(2, '0')}")
        }

        if (showTimePicker) {
            TimePickerDialog(
                currentHour = uiState.notificationHour,
                currentMinute = uiState.notificationMinute,
                onTimeSelected = { hour, minute ->
                    onNotificationTimeChange(hour, minute)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    IconButton(onClick = onClearError) {
                        Text("✕")
                    }
                }
            }
        }

        Button(
            onClick = onSaveConfiguration,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && uiState.serverUrl.isNotBlank() && uiState.password.length >= 8
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (uiState.isLoading) "Enregistrement..." else "Enregistrer la configuration")
        }
    }
}

@Composable
private fun TimePickerDialog(
    currentHour: Int,
    currentMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(currentHour) }
    var selectedMinute by remember { mutableStateOf(currentMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choisir l'heure") },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Heure")
                    OutlinedTextField(
                        value = selectedHour.toString().padStart(2, '0'),
                        onValueChange = { 
                            it.toIntOrNull()?.let { hour ->
                                if (hour in 0..23) selectedHour = hour
                            }
                        },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Text(":", style = MaterialTheme.typography.headlineMedium)
                Column {
                    Text("Minute")
                    OutlinedTextField(
                        value = selectedMinute.toString().padStart(2, '0'),
                        onValueChange = { 
                            it.toIntOrNull()?.let { minute ->
                                if (minute in 0..59) selectedMinute = minute
                            }
                        },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onTimeSelected(selectedHour, selectedMinute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
