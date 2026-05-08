#!/usr/bin/env node
/**
 * Token Sync Validator
 * Checks JSON tokens ↔ CSS variables ↔ Tailwind config are in sync
 * Usage: node validate-tokens.js [--tokens tokens.json] [--css globals.css] [--tailwind tailwind.config.js] [--src ./src] [--json]
 */

const fs = require('fs');
const path = require('path');

const args = process.argv.slice(2);
const getArg = (flag) => { const i = args.indexOf(flag); return i !== -1 ? args[i + 1] : null; };
const JSON_OUTPUT = args.includes('--json');

const TOKENS_FILE    = getArg('--tokens')   || findFile(['tokens.json', 'design-tokens.json', 'src/tokens.json']);
const CSS_FILE       = getArg('--css')      || findFile(['src/index.css', 'src/globals.css', 'styles/globals.css', 'app/globals.css']);
const TAILWIND_FILE  = getArg('--tailwind') || findFile(['tailwind.config.js', 'tailwind.config.ts']);
const SRC_DIR        = getArg('--src')      || './src';

function findFile(candidates) {
  for (const c of candidates) if (fs.existsSync(c)) return c;
  return null;
}

const issues = { critical: [], high: [], medium: [], low: [] };
const addIssue = (sev, msg, fix) => issues[sev].push({ msg, fix });

// --- 1. Parse JSON tokens ---
let jsonTokens = {};
if (TOKENS_FILE && fs.existsSync(TOKENS_FILE)) {
  function flattenTokens(obj, prefix = '') {
    for (const [k, v] of Object.entries(obj)) {
      const key = prefix ? `${prefix}-${k}` : k;
      if (typeof v === 'string') jsonTokens[key] = v;
      else if (typeof v === 'object' && v !== null) flattenTokens(v, key);
    }
  }
  try { flattenTokens(JSON.parse(fs.readFileSync(TOKENS_FILE, 'utf8'))); } catch (e) {
    addIssue('critical', `Cannot parse ${TOKENS_FILE}: ${e.message}`, 'Fix JSON syntax');
  }
}

// --- 2. Parse CSS variables ---
let cssVars = {};
if (CSS_FILE && fs.existsSync(CSS_FILE)) {
  const css = fs.readFileSync(CSS_FILE, 'utf8');
  for (const [, name, val] of css.matchAll(/--([^:]+)\s*:\s*([^;]+)/g)) {
    cssVars[name.trim()] = val.trim();
  }

  // Check dark mode presence
  if (!css.includes('[data-theme="dark"]') && !css.includes('.dark ') && !css.includes('prefers-color-scheme')) {
    addIssue('high', 'No dark mode variables found in CSS', 'Add [data-theme="dark"] { } or .dark { } block with all color overrides');
  }
}

// --- 3. Parse Tailwind config ---
let tailwindContent = '';
if (TAILWIND_FILE && fs.existsSync(TAILWIND_FILE)) {
  tailwindContent = fs.readFileSync(TAILWIND_FILE, 'utf8');
}

// --- 4. Scan src for hardcoded hex ---
const EXTENSIONS = ['.tsx', '.ts', '.jsx', '.js', '.css', '.scss'];
function walkSrc(dir, files = []) {
  if (!fs.existsSync(dir)) return files;
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, e.name);
    if (e.isDirectory() && !['node_modules', '.git', 'dist'].includes(e.name)) walkSrc(full, files);
    else if (e.isFile() && EXTENSIONS.includes(path.extname(e.name))) files.push(full);
  }
  return files;
}

const hardcodedHex = [];
if (fs.existsSync(SRC_DIR)) {
  for (const file of walkSrc(SRC_DIR)) {
    const content = fs.readFileSync(file, 'utf8');
    const lines = content.split('\n');
    lines.forEach((line, i) => {
      const matches = [...line.matchAll(/#[0-9a-fA-F]{3,6}\b/g)];
      for (const m of matches) {
        // Skip comments
        if (line.trim().startsWith('//') || line.trim().startsWith('*')) continue;
        hardcodedHex.push({ file: path.relative(SRC_DIR, file), line: i + 1, hex: m[0] });
      }
    });
  }
}

// --- 5. JSON → CSS sync checks ---
const colorJsonKeys = Object.keys(jsonTokens).filter(k => k.startsWith('color'));
for (const key of colorJsonKeys) {
  const cssName = key.replace(/\./g, '-');
  if (!cssVars[cssName] && !cssVars[`color-${cssName}`]) {
    addIssue('high', `JSON token "${key}" has no CSS variable equivalent`, `Add --${cssName}: ${jsonTokens[key]}; to your CSS`);
  }
}

// --- 6. CSS → Tailwind sync checks ---
const cssColorVars = Object.keys(cssVars).filter(k => k.startsWith('color') || k.includes('brand') || k.includes('surface'));
for (const cssVar of cssColorVars) {
  if (tailwindContent && !tailwindContent.includes(`--${cssVar}`) && !tailwindContent.includes(cssVar.replace('color-', ''))) {
    addIssue('medium', `CSS variable "--${cssVar}" not referenced in Tailwind config`, `Add ${cssVar.replace('color-', '')}: 'var(--${cssVar})' to tailwind.config theme.extend.colors`);
  }
}

// --- 7. Hardcoded hex in source ---
for (const h of hardcodedHex.slice(0, 20)) {
  addIssue('high', `Hardcoded hex ${h.hex} in ${h.file}:${h.line}`, 'Replace with CSS variable or Tailwind semantic class');
}
if (hardcodedHex.length > 20) {
  addIssue('high', `... and ${hardcodedHex.length - 20} more hardcoded hex values`, 'Run with --json for full list');
}

// --- 8. Required tokens check ---
const REQUIRED_SEMANTIC = ['brand', 'error', 'success', 'warning'];
for (const sem of REQUIRED_SEMANTIC) {
  const inJson = Object.keys(jsonTokens).some(k => k.includes(sem));
  const inCss = Object.keys(cssVars).some(k => k.includes(sem));
  if (!inJson && !inCss) {
    addIssue('medium', `Semantic token "${sem}" not found in tokens or CSS`, `Add --color-${sem}: #hex; to your design tokens`);
  }
}

// --- Output ---
const totalIssues = Object.values(issues).reduce((n, arr) => n + arr.length, 0);

if (JSON_OUTPUT) {
  console.log(JSON.stringify({
    files: { tokens: TOKENS_FILE, css: CSS_FILE, tailwind: TAILWIND_FILE },
    jsonTokens: Object.keys(jsonTokens).length,
    cssVars: Object.keys(cssVars).length,
    hardcodedHex: hardcodedHex.length,
    totalIssues,
    issues,
  }, null, 2));
  process.exit(issues.critical.length > 0 ? 1 : 0);
}

console.log('\n' + '='.repeat(60));
console.log('TOKEN SYNC VALIDATOR');
console.log('='.repeat(60));
console.log(`Tokens: ${TOKENS_FILE || 'not found'}`);
console.log(`CSS:    ${CSS_FILE || 'not found'}`);
console.log(`Tailwind: ${TAILWIND_FILE || 'not found'}`);
console.log(`JSON tokens: ${Object.keys(jsonTokens).length} | CSS vars: ${Object.keys(cssVars).length} | Hardcoded hex: ${hardcodedHex.length}\n`);

for (const [sev, items] of Object.entries(issues)) {
  if (!items.length) continue;
  const icon = { critical: '🔴', high: '🟠', medium: '🟡', low: '🔵' }[sev];
  console.log(`${icon} ${sev.toUpperCase()} (${items.length})`);
  for (const item of items) {
    console.log(`  → ${item.msg}`);
    console.log(`     Fix: ${item.fix}\n`);
  }
}

const status = issues.critical.length > 0 ? 'FAIL ❌' : totalIssues === 0 ? 'PASS ✅' : 'PASS WITH WARNINGS ⚠️';
console.log('='.repeat(60));
console.log(`STATUS: ${status} (${totalIssues} issues)`);
process.exit(issues.critical.length > 0 ? 1 : 0);
