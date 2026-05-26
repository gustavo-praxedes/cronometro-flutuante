# Plano: Fix Vibração

## Causa
Manifest sem permissão → App s/ acesso HW. Pipeline de código OK.

## Alteração

| Arquivo | Ação | Código |
|---|---|---|
| `app/src/main/AndroidManifest.xml` | Add tag antes `<application>` | `<uses-permission android:name="android.permission.VIBRATE" />` |
