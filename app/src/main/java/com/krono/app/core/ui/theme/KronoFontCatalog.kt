package com.krono.app.core.ui.theme

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import com.krono.app.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object KronoFontCatalog {
    val embeddedAppFonts = listOf(
        KronoFontOption.CHIVO,
        KronoFontOption.CHIVO_LIGHT
    )

    val embeddedOverlayFonts = listOf(
        KronoFontOption.CHIVO_MONO
    )

    val downloadableFonts = listOf(
        KronoFontOption.JETBRAINS_MONO,
        KronoFontOption.FIRA_CODE,
        KronoFontOption.ANONYMOUS_PRO,
        KronoFontOption.ROBOTO_MONO,
        KronoFontOption.COMMIT_MONO,
        KronoFontOption.AZERET_MONO
    )

    val downloadableMonoFonts = downloadableFonts

    fun appOptions(availableGoogleFonts: Set<String>): List<KronoFontOption> =
        embeddedAppFonts + downloadableFonts.filter { it.name in availableGoogleFonts }

    fun overlayOptions(availableGoogleFonts: Set<String>): List<KronoFontOption> =
        embeddedOverlayFonts + downloadableMonoFonts.filter { it.name in availableGoogleFonts }

    suspend fun discoverAvailableGoogleFonts(context: Context, knownAvailable: Set<String>): Set<String> {
        val found = knownAvailable.toMutableSet()
        downloadableFonts
            .filterNot { it.name in knownAvailable }
            .forEach { option ->
                if (requestGoogleFont(context.applicationContext, option.label)) {
                    found += option.name
                }
            }
        return found
    }

    @Suppress("DEPRECATION")
    private suspend fun requestGoogleFont(context: Context, familyName: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            val request = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "name=$familyName&weight=400",
                R.array.com_google_android_gms_fonts_certs
            )
            FontsContractCompat.requestFont(
                context,
                request,
                object : FontsContractCompat.FontRequestCallback() {
                    override fun onTypefaceRetrieved(typeface: Typeface) {
                        if (continuation.isActive) continuation.resume(true)
                    }

                    override fun onTypefaceRequestFailed(reason: Int) {
                        if (continuation.isActive) continuation.resume(false)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
}
