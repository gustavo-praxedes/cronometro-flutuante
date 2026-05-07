package com.krono.app.core.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WebAsset
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PictureInPicture
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * KronoIcons — Central de Ícones do App
 * Centraliza o uso de vetores Material 3 para manter consistência visual.
 */
object KronoIcons {

    // ── Navegação ───────────────────────────────────────────
    object Navigation {
        val Back: ImageVector         = Icons.AutoMirrored.Rounded.ArrowBack
        val OpenExternal: ImageVector = Icons.AutoMirrored.Rounded.OpenInNew
        val Close: ImageVector        = Icons.Rounded.Close
        val Menu: ImageVector         = Icons.Rounded.Menu
        val ChevronRight: ImageVector = Icons.Rounded.KeyboardArrowRight
    }

    // ── Ações ───────────────────────────────────────────────
    object Action {
        val Add: ImageVector          = Icons.Rounded.Add
        val Delete: ImageVector       = Icons.Rounded.Delete
        val Play: ImageVector         = Icons.Rounded.PlayArrow
        val Pause: ImageVector        = Icons.Rounded.Pause
        val Reset: ImageVector        = Icons.Rounded.Refresh
        val Replay: ImageVector       = Icons.Rounded.Replay
        val More: ImageVector         = Icons.Rounded.MoreVert
        val MoreHoriz: ImageVector    = Icons.Rounded.MoreHoriz
        val Check: ImageVector        = Icons.Rounded.Check
        val Palette: ImageVector      = Icons.Rounded.Palette
        val Settings: ImageVector     = Icons.Rounded.Settings
        val Sparkle: ImageVector      = Icons.Rounded.AutoAwesome
        val Light: ImageVector        = Icons.Rounded.LightMode
        val Volume: ImageVector       = Icons.Rounded.VolumeUp
        val Focus: ImageVector        = Icons.Rounded.TrackChanges
        val Download: ImageVector     = Icons.Rounded.Download
        val Notification: ImageVector = Icons.Rounded.Notifications
    }

    // ── Status e Info ───────────────────────────────────────
    object Status {
        val Empty: ImageVector        = Icons.Rounded.HourglassEmpty
        val Info: ImageVector         = Icons.Rounded.Info
        val Bug: ImageVector          = Icons.Rounded.BugReport
        val Source: ImageVector       = Icons.Rounded.Code
        val Favorite: ImageVector     = Icons.Rounded.Favorite
        val Coffee: ImageVector       = Icons.Rounded.Coffee
        val Speed: ImageVector        = Icons.Rounded.Speed
        val Doc: ImageVector          = Icons.AutoMirrored.Rounded.Article
        val Build: ImageVector        = Icons.Rounded.Build
        val History: ImageVector      = Icons.Rounded.History
        val Update: ImageVector       = Icons.Rounded.Update
        val Unchecked: ImageVector    = Icons.Rounded.RadioButtonUnchecked
    }

    // ── Recursos Específicos ───────────────────────────────
    object Feature {
        val Overlay: ImageVector      = Icons.Rounded.PictureInPicture
        val Timer: ImageVector        = Icons.Rounded.Timer
        val Hourglass: ImageVector    = Icons.Rounded.HourglassTop
        val HourglassBottom: ImageVector = Icons.Rounded.HourglassBottom
        val Countdown: ImageVector    = Icons.Outlined.HourglassBottom
    }

    // ── Settings ─────────────────────────────────────────────
    object Settings {
        val Appearance: ImageVector  = Icons.Outlined.Palette
        val Behavior: ImageVector    = Icons.Outlined.Tune
        val Overlay: ImageVector     = Icons.Outlined.WebAsset
        val Heart: ImageVector       = Icons.Outlined.Favorite
        val History: ImageVector     = Icons.Outlined.History
        val Update: ImageVector      = Icons.Outlined.SystemUpdate
        val Info: ImageVector         = Icons.Outlined.Info
    }
}

