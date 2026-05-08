---
name: caveman-help
description: >
  Quick-reference card for all caveman-pro modes, skills, and commands.
  One-shot display, not a persistent mode. Trigger: /caveman-help,
  "caveman help", "what caveman commands", "how do I use caveman".
---

# Caveman Help

One-shot reference card. Do NOT change mode, write files, or persist anything. Output in caveman style.

## Display This

### Modes

| Mode | Trigger | What changes |
|------|---------|--------------|
| **lite** | `/caveman lite` | Drop filler. Keep sentences. |
| **full** | `/caveman` or `/caveman full` | Drop articles + filler. Fragments OK. Default. |
| **ultra** | `/caveman ultra` | Extreme compression. Abbreviations. Arrows → causality. |
| **wenyan-lite** | `/caveman wenyan-lite` | Classical Chinese, light compression. |
| **wenyan-full** | `/caveman wenyan-full` | Full 文言文. Max classical terseness. |
| **wenyan-ultra** | `/caveman wenyan-ultra` | Extreme. Ancient scholar on a token budget. |

### Sub-Skills

| Skill | Trigger | Does |
|-------|---------|------|
| **caveman-commit** | `/caveman-commit` | Terse commit messages. Conventional Commits. ≤50 char subject. |
| **caveman-review** | `/caveman-review` | One-line PR comments: `L42: 🔴 bug: user null. Add guard.` |
| **caveman:compress** | `/caveman:compress <file>` | Compress .md files to caveman prose. |
| **caveman-help** | `/caveman-help` | This card. |

### Deactivate

"stop caveman" or "normal mode". Resume: `/caveman`.

### Configure Default

Default = `full`. To change, set env var:

```bash
export CAVEMAN_DEFAULT_MODE=ultra
```

Or config file `~/.config/caveman/config.json`:
```json
{ "defaultMode": "lite" }
```

Set `"off"` to disable auto-activation. Manual `/caveman` still works.

Priority: env var > config file > `full`.

### More

Full docs: https://github.com/JuliusBrussee/caveman
