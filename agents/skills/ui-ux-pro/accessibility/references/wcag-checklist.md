# WCAG 2.2 AA Checklist

## Perceivable

### Text Contrast
- [ ] Normal text (< 18px regular / < 14px bold): 4.5:1 minimum
- [ ] Large text (≥ 18px regular / ≥ 14px bold): 3:1 minimum
- [ ] UI components (borders, icons): 3:1 against adjacent color
- [ ] Disabled states exempt

### Non-Text Content
- [ ] Meaningful images have descriptive `alt` text
- [ ] Decorative images: `alt=""` or CSS background
- [ ] Icons that convey meaning have text alternative or `aria-label`
- [ ] Charts/graphs have text descriptions

### Color Independence
- [ ] No information conveyed by color alone
- [ ] Error states: icon or text in addition to red color
- [ ] Required fields: label text not just asterisk color

---

## Operable

### Touch Targets (2.5.8)
- [ ] All interactive elements ≥ 24×24px CSS (minimum)
- [ ] Recommended: ≥ 44×44px CSS
- [ ] Adequate spacing between adjacent targets

### Keyboard Navigation
- [ ] All interactive elements reachable via Tab
- [ ] Tab order matches visual reading order
- [ ] No keyboard traps (unless intentional modal)
- [ ] Skip navigation link for long pages

### Focus Indicators (2.4.11)
- [ ] Visible focus outline on all interactive elements
- [ ] Focus indicator meets 3:1 contrast against adjacent colors
- [ ] `:focus-visible` used (not `:focus`)
- [ ] `outline: none` never without alternative

### Motion (2.3.3)
- [ ] `prefers-reduced-motion` respected
- [ ] Looping animations pause when off-screen
- [ ] No content flashing more than 3 times per second

---

## Understandable

### Forms
- [ ] Every input has visible label (not placeholder only)
- [ ] Labels associated with inputs (`for`/`id` or wrapping `<label>`)
- [ ] Required fields indicated in label text (not color alone)
- [ ] Error messages specific and actionable
- [ ] Errors linked via `aria-describedby`
- [ ] `aria-invalid="true"` on invalid fields
- [ ] Success confirmation provided

### Language
- [ ] `<html lang="en">` declared
- [ ] Language changes within page declared

---

## Robust

### Semantic HTML
- [ ] Headings in logical order (h1 → h2 → h3)
- [ ] Lists use `<ul>/<ol>/<li>`
- [ ] Tables use `<th>` with scope
- [ ] Buttons are `<button>` (not `<div>` with click)
- [ ] Links are `<a href>` (not `<div>` with click)
- [ ] Landmarks used: `<main>`, `<nav>`, `<header>`, `<footer>`

### ARIA
- [ ] ARIA used only when native HTML insufficient
- [ ] No duplicate ARIA roles on native elements
- [ ] `aria-hidden` not on focusable elements
- [ ] `aria-live` regions for dynamic content
- [ ] Dialog: `role="dialog"` + `aria-labelledby` + `aria-modal="true"`
- [ ] `aria-expanded` on toggle elements

---

## Mobile Specific

- [ ] Touch targets: 44pt iOS / 48dp Android
- [ ] Pinch-to-zoom not blocked (`user-scalable=no` forbidden)
- [ ] Content readable at 200% zoom without horizontal scroll
- [ ] Bottom nav items accessible in one-hand use (thumb zone)
- [ ] Haptic feedback not the only confirmation

---

## Quick Fixes Reference

| Issue | Fix |
|---|---|
| Icon button no label | Add `aria-label="Close"` |
| Input no label | Add `<label for="id">` |
| Error not linked | Add `aria-describedby="error-id"` |
| Invalid field no ARIA | Add `aria-invalid="true"` |
| Focus hidden | Remove `outline:none` or add visible alternative |
| Div button | Replace with `<button type="button">` |
| Div link | Replace with `<a href>` |
| Modal no focus trap | Add focus trap on open, restore on close |
