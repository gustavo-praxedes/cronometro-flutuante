---
name: ui-ux-pro/mobile
description: "Mobile UI specialist. Use for iOS (SwiftUI, Expo SwiftUI), Android (Jetpack Compose, Expo Compose), React Native, and Expo Router apps. Covers navigation, touch targets, safe areas, offline, performance, platform conventions, and native component patterns."
tags: [mobile, ios, android, react-native, expo, swiftui, jetpack-compose]
---

# Mobile Specialist

## Scope
iOS · Android · React Native · Expo · SwiftUI · Jetpack Compose · Cross-platform

## Quick Decision

| Task | Read |
|---|---|
| Platform selection | `references/platform-decision.md` |
| iOS / SwiftUI patterns | `references/ios.md` |
| Android / Compose patterns | `references/android.md` |
| React Native / Expo | `references/react-native.md` |
| Performance rules | `references/performance.md` |

## Platform Decision Tree

```
Need OTA updates + web team shared code → React Native + Expo
High-performance custom UI              → Flutter
iOS only, native-first                  → SwiftUI
Android only, native-first              → Jetpack Compose
Cross-platform via Expo                 → @expo/ui (wraps both)
```

## Universal Mobile Rules (All Platforms)

```
Touch targets:  44pt iOS / 48dp Android / 44px CSS
Safe areas:     always account for (top + bottom insets)
Overflow:       no horizontal scroll on any screen
Body text:      min 16px / 16sp
Thumb zone:     primary CTAs in bottom reachable area
Offline:        define behavior for all states
```

## iOS Key Rules

- SF Symbols via `expo-image` source `"sf:name"` (not expo-symbols)
- `contentInsetAdjustmentBehavior="automatic"` on every ScrollView/FlatList
- `borderCurve: 'continuous'` for rounded corners (not circular)
- Sheets: `.sheet(item:)` preferred over `.sheet(isPresented:)`
- Stack navigation always in `_layout.tsx`
- `Link.Preview` on navigation links when possible
- Haptics: use conditionally, iOS only (`expo-haptics`)
- `ScrollView` as first child inside Stack route (almost always)

## Android Key Rules

- Material Design 3 patterns
- `LazyColumn` instead of ScrollView for lists
- `Host` wrapper required for all Jetpack Compose trees
- `<Host matchContents>` for intrinsic sizing
- `<Host style={{ flex: 1 }}>` when parent of LazyColumn
- Icons: Android XML vector drawables from Material Symbols
- System back button always works

## React Native / Expo Performance

```
Lists:      FlatList / FlashList — never ScrollView for long lists
renderItem: useCallback + React.memo — prevents re-render all rows
Keys:       stable IDs — never array index
Animations: native driver — never JS thread
Logs:       strip all console.log in production
Storage:    SecureStore / Keychain — never AsyncStorage for tokens
```

## Expo Router Structure

```
app/
  _layout.tsx          ← NativeTabs root
  (index,search)/
    _layout.tsx        ← Stack per tab
    index.tsx
    search.tsx
    i/[id].tsx         ← shared detail screen
```

## Never Use (Removed from RN)

```
❌ Picker (use community package)
❌ WebView (use react-native-webview)
❌ SafeAreaView from react-native (use react-native-safe-area-context)
❌ AsyncStorage (use @react-native-async-storage or SecureStore)
❌ expo-permissions (use individual expo-* packages)
❌ expo-av (use expo-audio + expo-video separately)
```

## Mandatory Checklist Before Shipping

- [ ] Touch targets ≥ 44pt/48dp
- [ ] Safe area: top + bottom
- [ ] No horizontal overflow
- [ ] Offline handled
- [ ] Tokens in secure storage
- [ ] Lists optimized (FlatList/LazyColumn)
- [ ] Logs stripped
- [ ] Tested on low-end device
- [ ] Accessibility labels present

## Scripts (mobile/scripts/)

| Script | Use |
|---|---|
| `mobile_audit.py` | 50+ automated checks on RN/Flutter code |
| `verify_ui.sh` | Android ADB: screenshot + UI hierarchy + JS logs |

```bash
# Audit project code
python mobile/scripts/mobile_audit.py ./src

# Verify Android UI (emulator must be running)
bash mobile/scripts/verify_ui.sh after_login

# Full docs → references/mobile-audit.md
```
