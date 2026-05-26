package com.krono.app.core.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
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
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.AvTimer
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.ExposurePlus1
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

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
        val CloseSmall: ImageVector   = MaterialSymbolCloseSmall
        val Menu: ImageVector         = Icons.Rounded.Menu
        val ChevronRight: ImageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight
    }

    // ── Ações ───────────────────────────────────────────────
    object Action {
        val Add: ImageVector          = Icons.Rounded.Add
        val Delete: ImageVector       = Icons.Rounded.Delete
        val Play: ImageVector         = Icons.Rounded.PlayArrow
        val Pause: ImageVector        = Icons.Rounded.Pause
        val Reset: ImageVector        = Icons.Rounded.Refresh
        val StopFilled: ImageVector   = Icons.Rounded.Stop
        val Next: ImageVector         = Icons.Rounded.SkipNext
        val Lap: ImageVector          = Icons.Rounded.Flag
        val Pip: ImageVector          = MaterialSymbolPip
        val Replay: ImageVector       = Icons.Rounded.Replay
        val More: ImageVector         = Icons.Rounded.MoreVert
        val Check: ImageVector        = Icons.Rounded.Check
        val Palette: ImageVector      = Icons.Rounded.Palette
        val Settings: ImageVector     = Icons.Rounded.Settings
        val Sparkle: ImageVector      = Icons.Rounded.AutoAwesome
        val Light: ImageVector        = Icons.Rounded.LightMode
        val Volume: ImageVector       = Icons.AutoMirrored.Rounded.VolumeUp
        val Focus: ImageVector        = Icons.Rounded.TrackChanges
        val Download: ImageVector     = Icons.Rounded.Download
        val Notification: ImageVector = Icons.Rounded.Notifications
        val NotificationOff: ImageVector = Icons.Rounded.NotificationsOff
        val Search: ImageVector       = Icons.Rounded.Search
        val SearchOff: ImageVector    = Icons.Rounded.SearchOff
        val Share: ImageVector        = Icons.Rounded.Share
        val PlusOne: ImageVector      = Icons.Rounded.ExposurePlus1
        val FormatSize: ImageVector   = Icons.Rounded.FormatSize
        val TypeSpecimen: ImageVector = Icons.Rounded.TextFields
        val Glyphs: ImageVector       = Icons.Rounded.Translate
        val FormatPaint: ImageVector  = Icons.Rounded.FormatPaint
        val MobileVibrate: ImageVector = Icons.Rounded.Vibration
        val NotificationSound: ImageVector = MaterialSymbolNotificationSound
        val KeyboardFull: ImageVector = Icons.Rounded.Keyboard
        val Autorenew: ImageVector    = Icons.Rounded.Autorenew
        val ListAlt: ImageVector      = Icons.AutoMirrored.Rounded.ListAlt
        val ListAltAdd: ImageVector   = Icons.AutoMirrored.Rounded.PlaylistAdd
        val AddCircle: ImageVector    = Icons.Rounded.AddCircle
        val Drag: ImageVector         = Icons.Rounded.DragIndicator
        val ExpandLess: ImageVector   = Icons.Rounded.ExpandLess
        val ExpandMore: ImageVector   = Icons.Rounded.ExpandMore
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
        val Person: ImageVector       = Icons.Rounded.Person
        val MobileAlert: ImageVector  = Icons.Rounded.WarningAmber
        val MobileArrowDown: ImageVector = Icons.Rounded.Download
        val MobileCheck: ImageVector  = Icons.Rounded.CheckCircle
    }

    // ── Recursos Específicos ───────────────────────────────
    object Feature {
        val Overlay: ImageVector      = MaterialSymbolPipExit
        val Timer: ImageVector        = Icons.Rounded.Timer
        val Pomodoro: ImageVector     = Icons.Rounded.Timelapse
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
        val TicketGate: ImageVector  = Icons.Rounded.ConfirmationNumber
    }
}

private val MaterialSymbolCloseSmall: ImageVector by lazy {
    materialSymbol(
        name = "MaterialSymbols.Rounded.CloseSmall",
        pathData = "M480-424 364-308q-11 11-28 11t-28-11q-11-11-11-28t11-28l116-116-116-115q-11-11-11-28t11-28q11-11 28-11t28 11l116 116 115-116q11-11 28-11t28 11q12 12 12 28.5T651-595L535-480l116 116q11 11 11 28t-11 28q-12 12-28.5 12T595-308L480-424Z"
    )
}

private val MaterialSymbolPipExit: ImageVector by lazy {
    materialSymbol(
        name = "MaterialSymbols.Rounded.PipExit",
        pathData = "M160-160q-33 0-56.5-23.5T80-240v-240q0-17 11.5-28.5T120-520q17 0 28.5 11.5T160-480v240h640v-480H480q-17 0-28.5-11.5T440-760q0-17 11.5-28.5T480-800h320q33 0 56.5 23.5T880-720v480q0 33-23.5 56.5T800-160H160Zm400-263 95 95q12 12 28 12t28-12q12-12 12-28.5T711-385l-95-95h64q17 0 28.5-11.5T720-520q0-17-11.5-28.5T680-560H520q-17 0-28.5 11.5T480-520v160q0 17 11.5 28.5T520-320q17 0 28.5-11.5T560-360v-63ZM120-600q-17 0-28.5-11.5T80-640v-120q0-17 11.5-28.5T120-800h200q17 0 28.5 11.5T360-760v120q0 17-11.5 28.5T320-600H120Zm360 120Z"
    )
}

private val MaterialSymbolPip: ImageVector by lazy {
    materialSymbol(
        name = "MaterialSymbols.Rounded.Pip",
        pathData = "M120-520q-17 0-28.5-11.5T80-560q0-17 11.5-28.5T120-600h104L80-743q-12-12-12-28.5T80-800q12-12 28.5-12t28.5 12l143 144v-104q0-17 11.5-28.5T320-800q17 0 28.5 11.5T360-760v200q0 17-11.5 28.5T320-520H120Zm40 360q-33 0-56.5-23.5T80-240v-160q0-17 11.5-28.5T120-440q17 0 28.5 11.5T160-400v160h280q17 0 28.5 11.5T480-200q0 17-11.5 28.5T440-160H160Zm651.5-291.5Q800-463 800-480v-240H480q-17 0-28.5-11.5T440-760q0-17 11.5-28.5T480-800h320q33 0 56.5 23.5T880-720v240q0 17-11.5 28.5T840-440q-17 0-28.5-11.5ZM600-160q-17 0-28.5-11.5T560-200v-120q0-17 11.5-28.5T600-360h240q17 0 28.5 11.5T880-320v120q0 17-11.5 28.5T840-160H600Z"
    )
}

private val MaterialSymbolNotificationSound: ImageVector by lazy {
    materialSymbol(
        name = "MaterialSymbols.Rounded.NotificationSound",
        pathData = "M501-500ZM480-80q-33 0-56.5-23.5T400-160h160q0 33-23.5 56.5T480-80ZM200-200q-17 0-28.5-11.5T160-240q0-17 11.5-28.5T200-280h40v-280q0-83 50-147.5T420-792v-28q0-25 17.5-42.5T480-880q25 0 42.5 17.5T540-820v28q16 4 30 9.5t28 13.5q20 11 23 27t-5 30q-8 14-23 20t-31-5q-18-11-38.5-17t-43.5-6q-66 0-113 47t-47 113v280h440q17 0 28.5 11.5T800-240q0 17-11.5 28.5T760-200H200Zm497-298q-3 17-10 33-7 17-23 24t-32-1q-15-7-21-23t2-30q5-9 6-18t1-19q0-10-1.5-19.5T613-570q-6-14 0-28t20-21q15-7 30.5-.5T687-598q8 16 10.5 32.5T700-532q0 17-3 34Zm128 63q-15 47-45 87-9 12-25.5 11.5T727-348q-11-11-12-27t8-30q19-28 28-60.5t9-66.5q0-34-9-66.5T723-659q-9-14-8-30t12-27q11-11 27.5-11.5T780-716q30 40 45 87t15 97q0 50-15 97Z"
    )
}

private fun materialSymbol(name: String, pathData: String): ImageVector {
    return ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        addGroup(translationY = 960f)
        addPath(
            pathData = PathParser().parsePathString(pathData).toNodes(),
            fill = SolidColor(Color.Black)
        )
        clearGroup()
    }.build()
}


