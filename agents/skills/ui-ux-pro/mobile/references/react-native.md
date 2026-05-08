# React Native / Expo Performance Reference

## List Optimization (Critical)

```tsx
import { FlatList, useCallback, memo } from 'react-native';
import { FlashList } from '@shopify/flash-list'; // preferred for large lists

// Row component — always memo
const Row = memo(({ item, onPress }: { item: Item; onPress: (id: string) => void }) => (
  <Pressable onPress={() => onPress(item.id)} style={styles.row}>
    <Text>{item.title}</Text>
  </Pressable>
));

// Parent — stable renderItem
export function ItemList({ items }: { items: Item[] }) {
  const handlePress = useCallback((id: string) => {
    router.push(`/i/${id}`);
  }, []);

  const renderItem = useCallback(
    ({ item }: { item: Item }) => <Row item={item} onPress={handlePress} />,
    [handlePress]
  );

  // FlashList (preferred)
  return (
    <FlashList
      data={items}
      renderItem={renderItem}
      keyExtractor={item => item.id}  // stable ID, never index
      estimatedItemSize={72}
      contentInsetAdjustmentBehavior="automatic"
    />
  );

  // FlatList (fallback)
  return (
    <FlatList
      data={items}
      renderItem={renderItem}
      keyExtractor={item => item.id}
      getItemLayout={(_, index) => ({ length: 72, offset: 72 * index, index })}
      contentInsetAdjustmentBehavior="automatic"
    />
  );
}
```

## Animations (Native Driver Required)

```tsx
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withTiming,
  withSpring,
  Easing,
} from 'react-native-reanimated';

// Fade in
const opacity = useSharedValue(0);

useEffect(() => {
  opacity.value = withTiming(1, { duration: 300, easing: Easing.out(Easing.ease) });
}, []);

const animStyle = useAnimatedStyle(() => ({ opacity: opacity.value }));

return <Animated.View style={animStyle}><Content /></Animated.View>;

// Spring bounce (for interactive elements)
const scale = useSharedValue(1);
const onPressIn  = () => { scale.value = withSpring(0.96); };
const onPressOut = () => { scale.value = withSpring(1); };
```

## Secure Storage

```tsx
import * as SecureStore from 'expo-secure-store';

// Store token
await SecureStore.setItemAsync('auth_token', token);

// Get token
const token = await SecureStore.getItemAsync('auth_token');

// Delete token
await SecureStore.deleteItemAsync('auth_token');

// NEVER use AsyncStorage for sensitive data
// AsyncStorage → only for non-sensitive preferences
```

## Expo Router Patterns

```tsx
// Navigation
import { router, useLocalSearchParams } from 'expo-router';

router.push('/settings');
router.replace('/home');  // no back button
router.back();

// Params
const { id } = useLocalSearchParams<{ id: string }>();

// Conditional navigation
router.push({
  pathname: '/i/[id]',
  params: { id: item.id }
});
```

## Platform Detection

```tsx
import { process } from 'process';

// Preferred (Expo)
process.env.EXPO_OS === 'ios'
process.env.EXPO_OS === 'android'
process.env.EXPO_OS === 'web'

// NOT Platform.OS (legacy)
```

## Dimensions

```tsx
// ALWAYS useWindowDimensions (reactive to rotation)
import { useWindowDimensions } from 'react-native';
const { width, height } = useWindowDimensions();

// NEVER Dimensions.get() (not reactive)
```

## Layout Rules

```tsx
// Content padding on ScrollView
<ScrollView contentContainerStyle={{ padding: 16, gap: 12 }}>

// Flex gap (preferred over margin)
<View style={{ flexDirection: 'row', gap: 8 }}>

// Shadows (CSS boxShadow — NOT legacy shadow props)
<View style={{ boxShadow: '0 2px 8px rgba(0,0,0,0.12)' }}>

// Border radius + continuous curve
<View style={{ borderRadius: 12, borderCurve: 'continuous' }}>
```

## Performance Checklist

- [ ] FlatList/FlashList for lists (never ScrollView for long lists)
- [ ] renderItem wrapped in useCallback
- [ ] Row components wrapped in memo
- [ ] Keys are stable IDs (never index)
- [ ] Animations use native driver (Reanimated)
- [ ] console.log stripped from production
- [ ] Images have explicit width + height
- [ ] No anonymous functions creating new references in render
- [ ] Secure tokens in SecureStore (not AsyncStorage)
- [ ] Tested on low-end Android device
