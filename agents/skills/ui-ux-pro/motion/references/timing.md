# Motion Timing & Framer Motion Reference

## Framer Motion Core Variants

```tsx
// Fade in
export const fadeIn = {
  initial: { opacity: 0 },
  animate: { opacity: 1 },
  exit:    { opacity: 0 },
  transition: { duration: 0.2 }
};

// Slide up (most common entrance)
export const slideUp = {
  initial:    { opacity: 0, y: 20 },
  animate:    { opacity: 1, y: 0 },
  exit:       { opacity: 0, y: 20 },
  transition: { duration: 0.3, ease: 'easeOut' }
};

// Scale on hover (cards, buttons)
export const scaleOnHover = {
  whileHover: { scale: 1.02 },
  whileTap:   { scale: 0.98 },
  transition: { type: 'spring', stiffness: 400, damping: 17 }
};

// Stagger container
export const staggerContainer = {
  hidden:  { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.05, delayChildren: 0.1 }
  }
};

// Stagger item
export const staggerItem = {
  hidden:  { opacity: 0, y: 10 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.2, ease: 'easeOut' } }
};
```

## Page Transition Wrapper

```tsx
import { motion } from 'framer-motion';

export function PageTransition({ children }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.3, ease: 'easeOut' }}
    >
      {children}
    </motion.div>
  );
}
```

## AnimatePresence (for exit animations)

```tsx
import { AnimatePresence } from 'framer-motion';

// Always wrap with mode="wait" for route transitions
<AnimatePresence mode="wait">
  <Routes location={location} key={location.pathname}>
    ...
  </Routes>
</AnimatePresence>

// For conditional visibility
<AnimatePresence>
  {isVisible && (
    <motion.div {...fadeIn}>
      Content
    </motion.div>
  )}
</AnimatePresence>
```

## Modal Animation

```tsx
// Overlay + content coordinated
<motion.div
  className="modal-overlay"
  initial={{ opacity: 0 }}
  animate={{ opacity: 1 }}
  exit={{ opacity: 0 }}
  transition={{ duration: 0.2 }}
>
  <motion.div
    className="modal-content"
    initial={{ opacity: 0, scale: 0.95, y: 10 }}
    animate={{ opacity: 1, scale: 1, y: 0 }}
    exit={{ opacity: 0, scale: 0.95 }}
    transition={{ duration: 0.25, ease: 'easeOut' }}
  >
    {children}
  </motion.div>
</motion.div>
```

## Reduced Motion

```tsx
import { useReducedMotion } from 'framer-motion';

function AnimatedComponent() {
  const shouldReduce = useReducedMotion();

  return (
    <motion.div
      animate={{ opacity: 1, y: shouldReduce ? 0 : 20 }}
      initial={{ opacity: 0, y: shouldReduce ? 0 : 20 }}
    >
      Content
    </motion.div>
  );
}
```

## Performance: GPU Only

```tsx
// GOOD — compositor properties only
<motion.div animate={{ x: 100 }} />           // transform: translateX
<motion.div animate={{ opacity: 0.5 }} />     // opacity
<motion.div animate={{ rotate: 45 }} />       // transform: rotate

// BAD — forces layout/paint
<motion.div animate={{ width: '100%' }} />    // triggers layout
<motion.div animate={{ height: '200px' }} />  // triggers layout
<motion.div animate={{ top: 20 }} />          // triggers layout
<motion.div animate={{ background: 'red' }} /> // triggers paint
```

## Tailwind CSS Animations (No JS needed)

```css
/* In globals.css or tailwind config */
@keyframes slideUp {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
}

.animate-slide-up {
  animation: slideUp 0.3s ease-out forwards;
}

/* Stagger via CSS delay */
.list-item:nth-child(1) { animation-delay: 0ms; }
.list-item:nth-child(2) { animation-delay: 50ms; }
.list-item:nth-child(3) { animation-delay: 100ms; }
```
