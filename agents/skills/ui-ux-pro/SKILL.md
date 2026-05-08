---
name: ui-ux-pro
description: "Master UI/UX orchestrator. Routes to the right specialist for any design or implementation task across Web, Mobile (iOS/Android), and Desktop apps. Use whenever someone asks about: UI design, UX flows, components, accessibility, design systems, tokens, motion, color, typography, visual design, microcopy, audits, or any frontend interface work. Automatically selects the best specialist skill."
tags: [ux, ui, design, orchestrator, web, mobile, app, components, accessibility]
tools: [claude, cursor, windsurf, codex, gemini]
---

# UI/UX Pro — Master Orchestrator

Routes to the correct specialist. Does not execute design work directly.

## How to Route

Read the request. Pick the best-fit specialist below. Call it immediately.

## Specialist Map

| Request type | Specialist |
|---|---|
| Design system setup, brand, tokens, theming | `design-system/SKILL.md` |
| Build a component (button, card, form, modal, etc.) | `components/SKILL.md` |
| Accessibility audit, WCAG, ARIA, focus, contrast | `accessibility/SKILL.md` |
| User flows, navigation, information architecture | `ux-flow/SKILL.md` |
| Loading/error/empty/success states | `ux-feedback/SKILL.md` |
| Animation, motion, transitions, micro-interactions | `motion/SKILL.md` |
| Font pairing, type scale, line height, readability | `typography/SKILL.md` |
| Color palette, dark mode, contrast, tokens | `color/SKILL.md` |
| iOS, Android, React Native, Expo, SwiftUI, Compose | `mobile/SKILL.md` |
| React, Angular, Tailwind, web UI patterns | `web/SKILL.md` |
| Aesthetic direction, visual identity, anti-AI-slop | `visual-design/SKILL.md` |
| Button labels, error messages, empty states copy | `ux-copy/SKILL.md` |
| UX audit, heuristic review, code review, validation | `audit/SKILL.md` |
| Conversion, friction reduction, behavioral design | `persuasion/SKILL.md` |
| Google Stitch, Magic UI, AI-assisted design tools | `ai-tools/SKILL.md` |
| Final quality check on completed work | `quality/SKILL.md` |

## Multi-domain Requests

If request spans multiple areas (e.g. "build accessible mobile component with animations"):
1. Route to primary specialist first
2. Note secondary areas in your response
3. Call secondary specialists as needed

## Quality Check

After any significant output, call `quality/SKILL.md` to verify the work meets standards.

## Rules

- Never design directly from this skill
- Always read the specialist SKILL.md before responding
- Use `references/` files in each specialist folder for depth
- If unclear which specialist fits → ask one clarifying question
