# Glassmorphism & Spatial UI Reference

## Glassmorphism

### Core CSS

```css
/* Dark glass card */
.glass-card {
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.10);
  border-radius: 1rem;
}

/* Dark glass panel (sidebar/overlay) */
.glass-panel {
  backdrop-filter: blur(20px);
  background: rgba(0, 0, 0, 0.40);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

/* Light glass card (IMPORTANT: min 80% opacity) */
.glass-card-light {
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.80);  /* NEVER 0.05 in light */
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 1rem;
}
```

### Performance Rules
- NEVER animate `backdrop-filter` continuously
- Test on low-end devices (expensive operation)
- Limit glass layers to 2–3 overlapping max
- Use sparingly — not every card needs glass

### When to Use
- App launchers / dashboards on rich backgrounds
- Modals over content
- iOS-style overlays
- Dark backgrounds with color/image behind

---

## Antigravity / 3D / Spatial UI

### Floating Elements

```css
/* Weightless card */
.floating-card {
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.05),
    0 4px 8px rgba(0, 0, 0, 0.04);
  transform: translateZ(0);
  transition: box-shadow 300ms ease, transform 300ms ease;
}

.floating-card:hover {
  box-shadow:
    0 32px 64px rgba(0, 0, 0, 0.08),
    0 8px 16px rgba(0, 0, 0, 0.06);
  transform: translateY(-4px) translateZ(0);
}
```

### CSS 3D Transforms

```css
/* Isometric grid tilt */
.isometric-grid {
  transform: rotateX(60deg) rotateZ(-45deg);
  transform-style: preserve-3d;
}

/* Perspective container */
.scene {
  perspective: 1000px;
  perspective-origin: 50% 50%;
}

/* Card with depth */
.card-3d {
  transform: rotateX(8deg) rotateY(-4deg);
  transform-style: preserve-3d;
}
```

### GSAP ScrollTrigger Pattern

```javascript
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";

gsap.registerPlugin(ScrollTrigger);

// Float in from below on scroll
gsap.from(".card", {
  scrollTrigger: {
    trigger: ".card",
    start: "top 80%",
    end: "top 50%",
    scrub: 1,
  },
  y: 60,
  opacity: 0,
  rotateX: 10,
  stagger: 0.1,
});

// Parallax layers
gsap.to(".background-layer", {
  scrollTrigger: { scrub: true },
  y: "-30%",  // slower than foreground
});
```

### Staggered Card Entrance

```tsx
// Framer Motion stagger
const container = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.1 }
  }
};

const item = {
  hidden: { opacity: 0, y: 30, rotateX: 10 },
  visible: { opacity: 1, y: 0, rotateX: 0 }
};

<motion.div variants={container} initial="hidden" animate="visible">
  {cards.map(card => (
    <motion.div key={card.id} variants={item}>
      <Card {...card} />
    </motion.div>
  ))}
</motion.div>
```

### GPU Performance

```css
/* Force GPU layer */
.animated-element {
  will-change: transform;  /* add only during animation */
  transform: translateZ(0);
}

/* Remove after animation */
.animation-complete {
  will-change: auto;
}
```

---

## Design Spells (Micro-Magic)

High-impact details that make interfaces memorable:

| Spell | Effect | When to use |
|---|---|---|
| Magnetic hover | Elements gently follow cursor | Hero sections, CTAs |
| Physics bounce | Spring animation on interaction | Buttons, toggles |
| Scroll reveal | Elements float in with slight rotation | Content sections |
| Number ticker | Animated counting | Stats, metrics |
| Cursor trail | Subtle particle follows cursor | Landing pages |
| Gradient shift | Background gradient moves on mouse | Hero backgrounds |
| Text scramble | Letters randomize then resolve | Loading states |
| Sticky header morph | Nav transforms on scroll | App shells |

### Implementation Rule
- 60fps minimum — test on device
- GPU properties only (transform, opacity)
- Disable for `prefers-reduced-motion`
- One "spell" per screen maximum
- Never janky — broken spell = worse than no spell
