package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krono.app.BuildConfig
import com.krono.app.R
import com.krono.app.core.ui.components.SettingsRow
import com.krono.app.core.ui.theme.KronoIcons
import com.krono.app.core.ui.theme.KronoTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FORM_ID = "1FAIpQLSeH4jyM_-SGY_Qsj4NBGSBUhfQIOVjA3L9yhgvtil4QCykyEA"
private const val ENTRY_DATA = "entry.1182927844"
private const val ENTRY_NAME = "entry.1606415010"
private const val ENTRY_EMAIL = "entry.809277435"
private const val ENTRY_MESSAGE = "entry.1665262073"
private const val ENTRY_VERSION = "entry.400223559"
private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

private sealed class InlineSubmitState {
    data object Idle : InlineSubmitState()
    data object Loading : InlineSubmitState()
    data object Success : InlineSubmitState()
    data object Error : InlineSubmitState()
}

@Composable
fun BugReportPanel(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var submitState by remember { mutableStateOf<InlineSubmitState>(InlineSubmitState.Idle) }

    val emailError = email.isNotBlank() && !EMAIL_REGEX.matches(email)
    val canSubmit = message.isNotBlank() && !emailError && submitState !is InlineSubmitState.Loading

    SettingsPanelLayout(modifier = modifier) {
        SettingsGroup(title = stringResource(R.string.bug_report_title)) {
            SettingsRow(
                title = stringResource(R.string.bug_report_title),
                subtitle = stringResource(R.string.bug_report_dialog_subtitle),
                leadingIcon = KronoIcons.Status.Bug
            )

            val inputModifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KronoTokens.Settings.panelHorizontalInset)

            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 50) name = it },
                label = { Text(stringResource(R.string.bug_report_name_optional)) },
                singleLine = true,
                modifier = inputModifier,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            Spacer(Modifier.height(KronoTokens.Settings.panelSectionGap))

            OutlinedTextField(
                value = email,
                onValueChange = { if (it.length <= 50) email = it },
                label = { Text(stringResource(R.string.bug_report_email_optional)) },
                singleLine = true,
                isError = emailError,
                modifier = inputModifier,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                supportingText = {
                    if (emailError) {
                        Text(
                            stringResource(R.string.bug_report_email_invalid),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            OutlinedTextField(
                value = message,
                onValueChange = { if (it.length <= 250) message = it },
                label = { Text(stringResource(R.string.bug_report_description_required)) },
                minLines = 4,
                maxLines = 8,
                modifier = inputModifier,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            Spacer(Modifier.height(KronoTokens.Settings.panelSectionGap))

            when (submitState) {
                is InlineSubmitState.Success -> SettingsRow(
                    title = stringResource(R.string.bug_report_success),
                    leadingIcon = KronoIcons.Action.Check
                )
                is InlineSubmitState.Error -> SettingsRow(
                    title = stringResource(R.string.bug_report_error),
                    leadingIcon = KronoIcons.Status.Bug
                )
                else -> Unit
            }
        }

        Button(
            onClick = {
                scope.launch {
                    submitState = InlineSubmitState.Loading
                    val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
                    submitState = submitBugReport(
                        name = name.trim(),
                        email = email.trim(),
                        message = message.trim(),
                        version = BuildConfig.VERSION_NAME,
                        date = date
                    )
                }
            },
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(KronoTokens.Button.height),
            shape = KronoTokens.Shape.button
        ) {
            if (submitState is InlineSubmitState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(KronoTokens.Component.buttonSpinner),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(stringResource(R.string.bug_report_sending))
            } else {
                Text(
                    text = stringResource(R.string.bug_report_send_button),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private suspend fun submitBugReport(
    name: String,
    email: String,
    message: String,
    version: String,
    date: String
): InlineSubmitState = withContext(Dispatchers.IO) {
    try {
        val formUrl = "https://docs.google.com/forms/d/e/$FORM_ID/formResponse"
        val params = buildString {
            append(URLEncoder.encode(ENTRY_DATA, "UTF-8")).append("=")
                .append(URLEncoder.encode(date, "UTF-8")).append("&")
            append(URLEncoder.encode(ENTRY_NAME, "UTF-8")).append("=")
                .append(URLEncoder.encode(name, "UTF-8")).append("&")
            append(URLEncoder.encode(ENTRY_EMAIL, "UTF-8")).append("=")
                .append(URLEncoder.encode(email, "UTF-8")).append("&")
            append(URLEncoder.encode(ENTRY_MESSAGE, "UTF-8")).append("=")
                .append(URLEncoder.encode(message, "UTF-8")).append("&")
            append(URLEncoder.encode(ENTRY_VERSION, "UTF-8")).append("=")
                .append(URLEncoder.encode(version, "UTF-8"))
        }

        val conn = (URL(formUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        }
        conn.outputStream.use { it.write(params.toByteArray(Charsets.UTF_8)) }
        val code = conn.responseCode
        conn.disconnect()
        if (code in 200..399) InlineSubmitState.Success else InlineSubmitState.Error
    } catch (_: Exception) {
        InlineSubmitState.Error
    }
}
