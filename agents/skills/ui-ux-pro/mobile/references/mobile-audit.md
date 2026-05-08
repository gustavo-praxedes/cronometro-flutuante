# Mobile Audit Script Reference

## Script Location
`mobile/scripts/mobile_audit.py`

## What It Does
50+ automated checks on React Native / Flutter code for mobile compliance.

## Usage

```bash
# Audit entire project directory
python mobile/scripts/mobile_audit.py ./src

# Audit single file
python mobile/scripts/mobile_audit.py ./src/screens/HomeScreen.tsx

# JSON output (for CI/CD)
python mobile/scripts/mobile_audit.py ./src --json
```

## Coverage (14 Check Categories)

| # | Category | Key Checks |
|---|---|---|
| 1 | Touch Psychology | Touch targets <44px, spacing <8px, thumb zone, gesture alternatives, haptics |
| 2 | Performance | ScrollView+map() → FlatList, React.memo, useCallback, keyExtractor, useNativeDriver |
| 3 | Navigation | Tab bar max 5, state preservation, back handling, deep links |
| 4 | Typography | System fonts, dynamic type, line height, font size limits |
| 5 | Color System | Pure black avoidance, OLED optimization, dark mode support |
| 6 | iOS Platform | SF Symbols, haptic types, SafeArea, iOS nav patterns, system colors |
| 7 | Android Platform | Ripple effects, BackHandler, Material 3, elevation, bottom nav |
| 8 | Mobile Backend | SecureStore vs AsyncStorage, offline handling, push notifications |
| 9 | iOS Typography | SF Pro font, iOS type scale (34/28/22/17/16/15/13px) |
| 10 | Android Typography | Roboto fallback, Material 3 scale, sp units |
| 11 | Modular Scale | Font size ratios (1.125/1.2/1.25/1.333/1.5) |
| 12 | Color Extended | OLED optimization, saturation, outdoor visibility, dark mode text |
| 13 | Testing | Jest, RNTL, Detox/Maestro, accessibilityLabel |
| 14 | Debugging | Error boundaries, console.log count, performance monitoring |

## Critical Issues (Block Shipping)

```
ScrollView + .map()         → use FlatList (memory explosion)
FlatList without keyExtractor → index-based bugs
Token in AsyncStorage        → use SecureStore / Keychain
Animated layout properties   → use transform/opacity only
Memory leak (no cleanup)     → useEffect subscriptions
```

## Output Format

```
[MOBILE AUDIT] 12 mobile files checked
--------------------------------------------------
[!] ISSUES (3):
  - [Performance CRITICAL] HomeScreen.tsx: ScrollView with .map()...
  - [Security] auth.ts: Storing auth tokens in AsyncStorage...
[*] WARNINGS (8):
  - [Touch Target] Card.tsx: Touch target size 38px < 44px...
[+] PASSED CHECKS: 14
STATUS: FAIL
```

## Exit Codes
- `0` — no critical issues (PASS)
- `1` — critical issues found (FAIL)

## CI/CD Integration

```yaml
# GitHub Actions example
- name: Mobile Audit
  run: python mobile/scripts/mobile_audit.py ./src --json > mobile-audit.json
  
- name: Check Results
  run: python -c "
    import json, sys
    r = json.load(open('mobile-audit.json'))
    sys.exit(0 if r['compliant'] else 1)
  "
```

## Android UI Verification (verify_ui.sh)

Requires: Android emulator running + adb in PATH

```bash
# Basic capture
bash mobile/scripts/verify_ui.sh

# Named screenshot
bash mobile/scripts/verify_ui.sh after_login

# Output in ./artifacts/
# - view.xml      (UI hierarchy dump)
# - <name>.png    (screenshot)
# - js_logs.txt   (React Native JS console)
```
