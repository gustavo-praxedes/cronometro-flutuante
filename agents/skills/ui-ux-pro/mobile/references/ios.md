# iOS Reference (SwiftUI + Expo SwiftUI)

## Expo Router — Standard App Structure

```tsx
// app/_layout.tsx — Root with NativeTabs
import { NativeTabs, Icon, Label } from "expo-router/unstable-native-tabs";

export default function Layout() {
  return (
    <NativeTabs>
      <NativeTabs.Trigger name="(index)">
        <Icon sf="list.dash" />
        <Label>Items</Label>
      </NativeTabs.Trigger>
      <NativeTabs.Trigger name="(search)" role="search" />
    </NativeTabs>
  );
}
```

```tsx
// app/(index,search)/_layout.tsx — Stack per tab group
import { Stack } from "expo-router/stack";
import { PlatformColor } from "react-native";

export default function Layout() {
  return (
    <Stack
      screenOptions={{
        headerLargeTitle: true,
        headerTransparent: true,
        headerShadowVisible: false,
        headerBlurEffect: "none",
        headerBackButtonDisplayMode: "minimal",
        headerTitleStyle: { color: PlatformColor("label") },
      }}
    />
  );
}
```

## Navigation Patterns

```tsx
// Link with preview (use frequently)
<Link href="/settings" asChild>
  <Link.Trigger>
    <Pressable><Card /></Pressable>
  </Link.Trigger>
  <Link.Preview />
</Link>

// Context menu
<Link href="/item/1" asChild>
  <Link.Trigger><Pressable><Row /></Pressable></Link.Trigger>
  <Link.Menu>
    <Link.MenuAction title="Share" icon="square.and.arrow.up" onPress={share} />
    <Link.MenuAction title="Delete" icon="trash" destructive onPress={del} />
  </Link.Menu>
</Link>
```

## Sheet Presentation

```tsx
// Modal sheet with grab handle
<Stack.Screen
  name="sheet"
  options={{
    presentation: "formSheet",
    sheetGrabberVisible: true,
    sheetAllowedDetents: [0.5, 1.0],
    contentStyle: { backgroundColor: "transparent" }, // liquid glass iOS 26+
  }}
/>
```

## ScrollView Best Practice

```tsx
// Always first child in Stack route
export default function Screen() {
  return (
    <ScrollView contentInsetAdjustmentBehavior="automatic">
      <Content />
    </ScrollView>
  );
}

// FlatList same pattern
<FlatList
  contentInsetAdjustmentBehavior="automatic"
  data={items}
  renderItem={renderItem}
  keyExtractor={(i) => i.id}
/>
```

## Styling Rules

```tsx
// Shadows: CSS boxShadow (NOT legacy shadow props)
<View style={{ boxShadow: "0 1px 2px rgba(0, 0, 0, 0.05)" }} />

// Rounded: continuous curve
<View style={{ borderRadius: 12, borderCurve: "continuous" }} />

// Spacing: prefer flex gap over margin
<View style={{ flexDirection: "row", gap: 8 }}>

// Never use Dimensions.get() — use useWindowDimensions
const { width } = useWindowDimensions();
```

## SF Symbols

```tsx
import { Image } from "expo-image";

// Basic SF symbol
<Image source="sf:heart.fill" style={{ width: 24, height: 24 }} />

// Tinted
<Image
  source="sf:star.fill"
  style={{ width: 24, height: 24, tintColor: "#FF6B6B" }}
/>
```

## expo-ui SwiftUI (SDK 55)

```tsx
import { Host, VStack, Button, Text } from "@expo/ui/swift-ui";
import { fillMaxWidth, paddingAll } from "@expo/ui/swift-ui/modifiers";

// Every SwiftUI tree must be in Host
<Host matchContents>
  <VStack>
    <Text style={{ typography: "title" }}>Hello</Text>
    <Button onPress={() => alert("!")}>Tap</Button>
  </VStack>
</Host>

// Embed RN inside SwiftUI
import { RNHostView } from "@expo/ui/swift-ui";
<Host matchContents>
  <VStack>
    <RNHostView matchContents>
      <Pressable />  {/* RN component inside SwiftUI */}
    </RNHostView>
  </VStack>
</Host>
```

## Haptics (iOS only)

```tsx
import * as Haptics from "expo-haptics";
import { process } from "process";

const triggerHaptic = () => {
  if (process.env.EXPO_OS === "ios") {
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
  }
};
```
