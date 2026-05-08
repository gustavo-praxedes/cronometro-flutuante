---
name: caveman-compress
description: >
  Compress natural language memory files (CLAUDE.md, todos, preferences) into caveman format
  to save input tokens. Preserves all technical substance, code, URLs, and structure.
  Compressed version overwrites the original file. Human-readable backup saved as FILE.original.md.
  Trigger: /caveman:compress <filepath> or "compress memory file".
---

# Caveman Compress

Compress natural language .md files to caveman prose. Reduce input tokens. Backup original.

## Trigger

`/caveman:compress <filepath>` or "compress memory file".

## Process

1. This SKILL.md lives alongside `scripts/` in same directory. Find that directory.
2. Run:

```bash
cd <directory_containing_this_SKILL.md> && python3 -m scripts <absolute_filepath>
```

3. CLI will:
   - Detect file type (no tokens)
   - Call Claude to compress
   - Validate output (no tokens)
   - If errors: cherry-pick fix with Claude (targeted fixes, no recompression)
   - Retry up to 2 times
   - If still failing after 2 retries: report error, leave original untouched

## Compression Rules

### Remove
- Articles: a, an, the
- Filler: just, really, basically, actually, simply, essentially, generally
- Pleasantries: sure, certainly, of course, happy to, I'd recommend
- Hedging: it might be worth, you could consider, it would be good to
- Redundant phrasing: "in order to" → "to", "make sure to" → "ensure"
- Connective fluff: however, furthermore, additionally, in addition

### Preserve EXACTLY
- Code blocks (fenced ``` and indented)
- Inline code (`backtick content`)
- URLs and links
- File paths
- Commands
- Technical terms (library names, API names, protocols)
- Proper nouns (project names, people, companies)
- Dates, version numbers, numeric values
- Environment variables

### Preserve Structure
- All markdown headings (compress body, keep heading text exact)
- Bullet point hierarchy (keep nesting)
- Numbered lists
- Tables (compress cell text, keep structure)
- Frontmatter/YAML headers

### Compress
- Short synonyms: "big" not "extensive", "fix" not "implement a solution for"
- Fragments OK
- Drop "you should", "make sure to", "remember to" — state action directly
- Merge redundant bullets

## CRITICAL

Anything inside ` ``` ... ``` ` copied EXACTLY. No comment removal, no spacing changes, no reordering, no command shortening.

Inline code (`` `...` ``) preserved EXACTLY.

## Boundaries

- ONLY compress: .md, .txt, extensionless
- NEVER modify: .py, .js, .ts, .json, .yaml, .yml, .toml, .env, .lock, .css, .html, .xml, .sql, .sh
- Mixed content: compress prose only
- Backup saved as FILE.original.md before overwrite
- Never compress FILE.original.md
