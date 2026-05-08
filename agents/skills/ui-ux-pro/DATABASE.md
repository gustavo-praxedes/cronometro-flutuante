# UI/UX PRO — Knowledge Database
> Synthesized from 29 source skill files. Single source of truth for all specialist skills.

---

## KNOWLEDGE AREAS MAP

| Area | Folder | Key Topics |
|------|--------|-----------|
| Design System | design-system/ | Tokens, theming, setup, components |
| Components | components/ | UI patterns, states, forms, dialogs |
| Accessibility | accessibility/ | WCAG 2.2 AA, a11y audit, ARIA |
| UX Flow | ux-flow/ | Navigation, flows, information arch |
| UX Feedback | ux-feedback/ | Loading, error, empty, success states |
| Motion & Animation | motion/ | Performance, timing, anti-patterns |
| Typography | typography/ | Scale, font pairing, spacing |
| Color | color/ | Palettes, tokens, contrast |
| Mobile | mobile/ | iOS, Android, Expo, React Native |
| Web | web/ | React, Angular, dark theme, Tailwind |
| Visual Design | visual-design/ | Aesthetic, anti-patterns, glass |
| UX Copy | ux-copy/ | Microcopy, tone, error messages |
| Audit & Review | audit/ | Heuristics, compliance, validation |
| Persuasion UX | persuasion/ | Behavioral psychology, choice arch |
| AI Tools | ai-tools/ | Stitch, Magic UI, AI-assisted design |
| Quality Check | quality/ | Cross-domain validator (universal) |

---

## CORE PRINCIPLES (Universal)

### UI States
- Loading: show only when no data; skeleton over spinner for known shapes
- Error: always surface; never swallow; show near the action
- Empty: every list needs empty state + one clear next action
- Success: confirm with toast; undo for destructive actions

### Loading Decision Tree
Error? Show error+retry | Loading+no data? Show skeleton | Has data? Show it | Empty? Empty state

### Button Rules
- Disable during async ops (prevent double-submit)
- Show loading indicator when submitting
- AlertDialog for destructive/irreversible actions

---

## DESIGN SYSTEM

### Design Tokens Scale
Colors: brand, semantic (success/warning/error/info), neutral, text, border, dataviz
Typography: display/h1/h2/h3/body/small/caption/label
Spacing: xs=4px sm=8px md=16px lg=24px xl=32px 2xl=48px 3xl=64px
Radius: sm=4px md=8px lg=12px xl=16px full=9999px
Shadow: sm/md/lg/xl semantic elevation
Motion: fast=150ms normal=250ms slow=400ms cubic-bezier spring

### Token Governance
- JSON tokens → CSS variables → component usage (keep in sync)
- Semantic names (bg-card not bg-white)
- Light + dark variants required
- Never hardcode hex in components

### Setup Wizard Sequence
1. App type (SaaS/ecommerce/fintech/social/productivity)
2. Brand color → update light+dark tokens
3. Visual reference (optional: Stripe, Linear, Vercel)
4. Typography
5. Scaffold first screen

---

## COMPONENTS

### Atomic Structure
atoms: Button, Input, Icon, Badge, Spinner, Avatar
molecules: Card, Form field, Nav item, Toast
organisms: Header, Sidebar, Modal, Data table
patterns: Onboarding, Search, Dashboard layout

### Requirements
- className passthrough support
- data-slot for identification
- CVA for variants
- Semantic tokens only
- Touch targets min 44x44px (iOS) / 48dp (Android)
- Keyboard focus visible
- ARIA attributes passthrough

### Accessible Primitive Libraries
Web: Radix UI, Base UI, React Aria
iOS: SwiftUI native
Android: Jetpack Compose / Material 3
Cross-platform: Expo UI (@expo/ui)

---

## ACCESSIBILITY (WCAG 2.2 AA)

### Audit Areas
Perceivable: contrast 4.5:1 text, 3:1 non-text, alt text, no color-only info
Operable: touch targets >= 44px, keyboard nav, tab order, visible focus, reduced-motion
Understandable: visible labels, errors linked to fields, clear wording
Robust: semantic HTML, correct ARIA, no fake buttons

### Critical Rules
- Icon-only buttons MUST have aria-label
- NEVER rebuild keyboard/focus by hand
- NEVER block paste in input/textarea
- Pause looping animations when off-screen
- Always respect prefers-reduced-motion

---

## UX FLOW

### Navigation Models
Hub & spoke: dashboards | Linear: onboarding/checkout | Tab: 3-5 top areas

### Flow Rules
- Clear entry + exit on every flow
- Key features <= 3 taps from home
- Non-root screens need back nav
- Loading/empty/error need recovery paths

### Information Architecture
Progressive disclosure | Miller's Law (<=7 chunks) | Hick's Law (min decisions per screen)
Info pyramid: hero > KPIs > detail > secondary

### Onboarding (Elite)
Screen 1: Promise | Screen 2: Immediate value (min form) | Screen 3: Personalization (max 3q + skip) | Screen 4: Aha moment

---

## UX FEEDBACK STATES

4 Required States:
1. Loading — skeleton matching layout; delay to avoid flash
2. Empty — message + next action; zero values still render
3. Error — plain language + recovery; localize to affected section
4. Success — lightweight confirm; undo for reversible destructive

Error Hierarchy:
Inline (field) > Toast (recoverable) > Banner (page-level) > Full screen (unrecoverable)

---

## MOTION & ANIMATION

### Hard Rules
- NEVER animate unless requested
- ONLY transform + opacity
- NEVER width/height/top/left/margin/padding
- MAX 200ms for interaction feedback
- Pause looping off-screen
- Respect prefers-reduced-motion
- No custom easing unless requested

### Timing Scale
Interaction: 150-200ms ease-out
Transitions: 250-300ms ease-in-out
Spring: 400ms cubic-bezier(0.34,1.56,0.64,1)
Stagger: 50-100ms between items

### Performance
Never animate box-shadow or filter continuously
Never will-change outside active animation
Never large blur/backdrop-filter surfaces

---

## TYPOGRAPHY

### Scale
display=48/700/lh1.1 | h1=36/700/lh1.2 | h2=28/600/lh1.3
body=16/400/lh1.6 | small=14/400/lh1.5 | caption=12/400

### Rules
text-balance for headings | text-pretty for body | tabular-nums for data
Line length: 65-75 chars | Min 16px mobile
NEVER modify letter-spacing unless requested

### Fonts
1 expressive display + 1 restrained body
AVOID: Inter, Roboto, Arial, system fonts
USE: distinctive, context-appropriate choices

---

## COLOR

### Palette Structure
Brand: primary/hover/light/subtle
Semantic: success=#10B981 warning=#F59E0B error=#EF4444 info=#3B82F6
Neutral: bg-1..6, text-primary/secondary/muted, border-subtle/default/strong
Dataviz: 7-color accessible

### Dark Mode Scale
bg-1=hsl(240,6%,10%) to bg-6=hsl(240,4%,26%)
text-primary=#FFF secondary=#A1A1AA muted=#71717A
borders=hsla(0,0%,100%,0.08-0.20)

### Light Mode
Text min: #0F172A / muted min: #475569
Cards: bg-white/80+ (not transparent)
Borders: border-gray-200

### Anti-Patterns
No purple/white gradients | No glow affordances | No evenly-balanced palettes | 1 accent per view

---

## MOBILE

### Platform Decision
OTA + web team > React Native + Expo
High-perf UI > Flutter
iOS only > SwiftUI
Android only > Jetpack Compose
Cross via Expo > @expo/ui

### Universal Rules
Touch targets: 44pt iOS / 48dp Android
Safe area insets always accounted for
No horizontal overflow | Thumb-zone for CTAs
Handle offline states

### iOS
SF Symbols via expo-image sf:name
borderCurve: continuous for rounded
Use .sheet(item:) not .sheet(isPresented:)
contentInsetAdjustmentBehavior="automatic" on all scrollable

### Android
Material Design 3 | LazyColumn over ScrollView
Host wrapper required for Compose trees
Vector drawables from Material Symbols

### React Native Performance
FlatList/FlashList for long lists
useCallback + React.memo for renderItem
Stable IDs (never index as key)
Native driver for animations
Tokens in SecureStore/Keychain not AsyncStorage

---

## WEB

### React
Functional components | local state first | error boundary + suspense
Never useEffect for render logic

### Angular (Modern)
Signals | @defer for non-critical | @if/@for/@empty control flow
Signal stores for shared state

### Tailwind Rules
cn() for class logic | h-dvh not h-screen | fixed z-index scale
size-* for squares | semantic classes before custom

---

## VISUAL DESIGN

### Anti-AI-Slop
No: purple/white gradients, centered layouts, uniform corners, Inter
No: glow affordances, generic shadows, template sections
Yes: clear aesthetic direction, differentiation anchor, cohesive POV

### Aesthetic Directions
Brutalist/Raw | Editorial/Magazine | Luxury/Refined | Retro-futuristic
Industrial | Organic | Playful | Minimalist/Severe | Art Deco | Glassmorphism | Maximalist

### Glassmorphism
backdrop-filter: blur(12px)
bg: rgba(255,255,255,0.05-0.10)
border: 1px solid rgba(255,255,255,0.08-0.20)
Light mode: bg-white/80+ required

### DFII Score
= (Impact + Fit + Feasibility + Performance) - Consistency Risk
>= 12: Execute | 8-11: Proceed | 4-7: Reduce | <=3: Rethink

---

## UX COPY

### Tone
Casual + polite | Active voice | Positive | Plain language | Concise

### By Surface
Buttons: verb+object ("Save changes" not "Submit")
Empty: "No items yet. Create your first X."
Errors: blame system not user
Toasts: confirm + undo option
Forms: clear label + useful placeholder + specific error
Dialogs: state action + consequence for risky

---

## AUDIT & REVIEW

### Nielsen Heuristics
1. System status visibility | 2. Real-world language | 3. User control+freedom
4. Consistency | 5. Error prevention | 6. Recognition over recall
7. Flexibility | 8. Aesthetic minimalism | 9. Error recovery | 10. Help

### Code Review Checklist
No hardcoded hex | No improvised shadows/radius | className passthrough
Touch targets >=44px | Errors surfaced | Loading/empty/error states present
Buttons disabled during async | A11y labels present

---

## PERSUASION UX

### Framework (Fogg Model)
Behavior = Motivation x Ability x Prompt
Failures: cognitive overload, hidden next steps

### Sequence
1. Define ONE target behavior
2. Audit friction (remove unnecessary decisions)
3. Design default path (easiest = most helpful)
4. Insert commitment points (small yes-steps)
5. Verify ethical guardrails

### Loss Framing Rules
Only use when reference point supports it
Minimum signal needed | Specific near-term consequences
Real tradeoffs only, not invented panic

---

## AI TOOLS

### Google Stitch Prompt Template
[Screen type] for [context]
Features: [list]
Style: [color] [aesthetic] [layout]
Platform: [Mobile/Web/Responsive]

### Magic UI (21st.dev)
Multiple variations before selecting
Push unconventional styles
TypeScript + accessible + responsive always

### DESIGN.md Structure
1. Visual Theme & Atmosphere
2. Color Palette & Roles (name + hex + role)
3. Typography Rules
4. Component Stylings
5. Layout Principles

---

## PLATFORM CONTEXT MATRIX

| Feature | Web | iOS | Android | Cross |
|---------|-----|-----|---------|-------|
| Nav | Router/Next.js | NavigationStack | Back stack | Expo Router |
| Components | Radix/shadcn | SwiftUI | Compose | Expo UI |
| Animation | Framer/CSS | SwiftUI anim | Compose anim | Reanimated |
| Icons | Lucide/Heroicons | SF Symbols | Material Symbols | expo-image sf: |
| Styling | Tailwind | Inline/SwiftUI | Compose modifiers | Inline RN |
| Safe area | CSS env() | SafeAreaInsets | WindowInsets | contentInsetAdjustmentBehavior |
| Touch min | CSS 44px | 44pt | 48dp | 44px |

---

## SOURCES (29 files)
1-uxui-principles | 2-android-ui-verification | 3-angular-ui-patterns | 4-baseline-ui
5-building-native-ui | 6-expo-jetpack-compose | 7-expo-swiftui | 8-frontend-ui-dark-ts
9-magic-ui-generator | 10-radix-ui-design-system | 11-react-ui-patterns | 12-stitch-ui-design
13-swiftui-ui-patterns | 14-ui-a11y | 15-ui-component | 16-ui-page | 17-ui-pattern
18-ui-review | 19-ui-setup | 20-ui-skills | 21-ui-tokens | 22-ui-ux-designer
23-ui-ux-pro-max | 24-ui-visual-validator | 25-ux-audit | 26-ux-copy | 27-ux-feedback
28-ux-flow | 29-ux-persuasion-engineer

---

## UPDATE — Additional Sources (files 29–54)

Files 29–54 scanned. New unique content found:

### canvas-design (SKILL__51_)
- 2-step process: Design Philosophy (.md) → Canvas expression (.pdf/.png)
- Philosophy naming: 1-2 word movement name
- 4-6 paragraph manifesto: space/form/color/composition/rhythm
- Output: 90% visual, 10% text
- Craft emphasis: must look like countless hours of work
- Subtle reference: conceptual DNA woven invisibly into composition
- Multi-page: coffee table book approach when requested
- NEVER: cartoony, amateur, AI-looking — always museum quality

### theme-factory (SKILL__53_)
- 10 preset themes for artifacts (slides, docs, HTML pages)
- Themes: Ocean Depths, Sunset Boulevard, Forest Canopy, Modern Minimalist,
  Golden Hour, Arctic Frost, Desert Rose, Tech Innovation, Botanical Garden, Midnight Galaxy
- Each: color palette (hex) + font pairing + visual identity
- Workflow: show theme-showcase.pdf → user selects → apply consistently
- Custom theme: generate on-the-fly from description

### web-artifacts-builder (SKILL__54_)
- Stack: React 18 + TypeScript + Vite + Parcel + Tailwind 3.4.1 + shadcn/ui
- Init: scripts/init-artifact.sh → dev → bundle: scripts/bundle-artifact.sh
- Output: single self-contained bundle.html
- 40+ shadcn/ui components pre-installed
- Parcel bundling with path alias support (@/)
- Anti-slop: no centered layouts, purple gradients, uniform corners, Inter font
- Only for complex artifacts (state, routing, shadcn) — not simple single-file

### Confirmed duplicates/variations (no new knowledge):
- SKILL__29_ tailwind-design-system (already covered)
- SKILL__30_ tool-design (agent tooling, not UI/UX)
- SKILL__31_-38_ StyleSeed variants (already synthesized)
- SKILL__39_-48_ ui-ux-designer, uxui-principles variants (already covered)
- SKILL__49_ wordpress-theme-development (out of scope)
- SKILL__50_ brand-guidelines / Anthropic brand (specific, noted)

### SOURCES NOW COMPLETE: 55 files reviewed
