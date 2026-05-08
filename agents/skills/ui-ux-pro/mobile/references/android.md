# Android Reference (Jetpack Compose + Expo Compose)

## expo-ui Jetpack Compose (SDK 55)

```tsx
import { Host, Column, Button, Text, LazyColumn } from "@expo/ui/jetpack-compose";
import { fillMaxWidth, paddingAll } from "@expo/ui/jetpack-compose/modifiers";

// All Compose trees: wrap in Host
// matchContents = intrinsic sizing
<Host matchContents>
  <Column
    verticalArrangement={{ spacedBy: 8 }}
    modifiers={[fillMaxWidth(), paddingAll(16)]}
  >
    <Text style={{ typography: "titleLarge" }}>Hello</Text>
    <Button onPress={() => alert("Pressed!")}>Press me</Button>
  </Column>
</Host>

// LazyColumn (scrollable lists)
// Use Host style={{ flex: 1 }} — not matchContents
<Host style={{ flex: 1 }}>
  <LazyColumn>
    {items.map((item) => (
      <Text key={item.id}>{item.title}</Text>
    ))}
  </LazyColumn>
</Host>
```

## Icons (Material Symbols)

```tsx
import { Icon } from "@expo/ui/jetpack-compose";

// Android XML vector drawables from Material Symbols
<Icon source={require('./icons/favorite.xml')} size={24} />
```

## Material Design 3 Spacing

- Base unit: 4dp
- Touch targets: minimum 48×48dp
- Content padding: 16dp horizontal
- Card elevation: 1dp (tonal) or shadow

## Fetch Docs Before Using Components

Always verify API before using:
```
https://docs.expo.dev/versions/v55.0.0/sdk/ui/jetpack-compose/{component-name}/index.md
https://docs.expo.dev/versions/v55.0.0/sdk/ui/jetpack-compose/modifiers/index.md
```

## Material 3 Key Patterns

```
Navigation: NavigationBar (bottom) or NavigationRail (tablet)
Lists: LazyColumn not ScrollView
Cards: Card with tonal elevation
FAB: FloatingActionButton in scaffold
TopBar: TopAppBar with large title on scroll
```

## System Back Button

Always support system back:
- Expo Router: handled automatically by Stack
- Native: `BackHandler` API when needed
- Never disable without providing alternative
