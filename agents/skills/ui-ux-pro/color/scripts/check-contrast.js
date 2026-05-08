#!/usr/bin/env node
/**
 * Contrast Checker — WCAG 2.2 AA Compliance
 * Reads color tokens from CSS/JSON and checks all text+bg pairs
 * Usage: node check-contrast.js [tokens-file] [--json]
 *
 * Supports: CSS variables file, JSON tokens file, or built-in defaults
 */

const fs = require('fs');
const path = require('path');

const INPUT = process.argv[2];
const JSON_OUTPUT = process.argv.includes('--json');

// --- Color math ---
function hexToRgb(hex) {
  const clean = hex.replace('#', '');
  const full = clean.length === 3
    ? clean.split('').map(c => c + c).join('')
    : clean;
  return {
    r: parseInt(full.slice(0, 2), 16),
    g: parseInt(full.slice(2, 4), 16),
    b: parseInt(full.slice(4, 6), 16),
  };
}

function relativeLuminance({ r, g, b }) {
  const ch = [r, g, b].map(v => {
    const s = v / 255;
    return s <= 0.04045 ? s / 12.92 : Math.pow((s + 0.055) / 1.055, 2.4);
  });
  return 0.2126 * ch[0] + 0.7152 * ch[1] + 0.0722 * ch[2];
}

function contrastRatio(hex1, hex2) {
  const l1 = relativeLuminance(hexToRgb(hex1));
  const l2 = relativeLuminance(hexToRgb(hex2));
  const [light, dark] = l1 > l2 ? [l1, l2] : [l2, l1];
  return parseFloat(((light + 0.05) / (dark + 0.05)).toFixed(2));
}

// WCAG thresholds
function grade(ratio, isLarge = false, isUI = false) {
  if (isUI) return ratio >= 3 ? 'AA ✅' : 'FAIL ❌';
  if (isLarge) return ratio >= 4.5 ? 'AAA ✅' : ratio >= 3 ? 'AA ✅' : 'FAIL ❌';
  return ratio >= 7 ? 'AAA ✅' : ratio >= 4.5 ? 'AA ✅' : 'FAIL ❌';
}

// --- Token extraction ---
function extractFromCSS(content) {
  const tokens = {};
  const matches = content.matchAll(/--color-([^:]+)\s*:\s*(#[0-9a-fA-F]{3,6})/g);
  for (const [, name, hex] of matches) tokens[name.trim()] = hex;
  return tokens;
}

function extractFromJSON(content) {
  const tokens = {};
  function flatten(obj, prefix = '') {
    for (const [k, v] of Object.entries(obj)) {
      const key = prefix ? `${prefix}-${k}` : k;
      if (typeof v === 'string' && /^#[0-9a-fA-F]{3,6}$/.test(v)) tokens[key] = v;
      else if (typeof v === 'object' && v !== null) flatten(v, key);
    }
  }
  flatten(JSON.parse(content));
  return tokens;
}

// Built-in default token pairs to always check
const DEFAULT_PAIRS = [
  // [fg, bg, label, isLarge, isUI]
  ['#FFFFFF', '#6C63FF', 'white on brand-primary', false, false],
  ['#FFFFFF', '#5A52E0', 'white on brand-hover', false, false],
  ['#0F172A', '#FFFFFF', 'slate-900 on white (heading)', false, false],
  ['#475569', '#FFFFFF', 'slate-600 on white (body min)', false, false],
  ['#6B7280', '#FFFFFF', 'gray-500 on white (muted)', false, false],
  ['#9CA3AF', '#FFFFFF', 'gray-400 on white (should fail)', false, false],
  ['#FFFFFF', '#111827', 'white on neutral-900 (dark bg)', false, false],
  ['#A1A1AA', '#18181B', 'text-secondary on dark-bg', false, false],
  ['#71717A', '#18181B', 'text-muted on dark-bg', false, false],
  ['#FFFFFF', '#EF4444', 'white on error', false, false],
  ['#FFFFFF', '#22C55E', 'white on success', false, false],
  ['#FFFFFF', '#F59E0B', 'white on warning', false, false],
  ['#FFFFFF', '#3B82F6', 'white on info', false, false],
  // UI components
  ['#6C63FF', '#FFFFFF', 'brand border on white (UI)', false, true],
  ['#EF4444', '#FFFFFF', 'error border on white (UI)', false, true],
  // Large text
  ['#475569', '#FFFFFF', 'slate-600 body (large)', true, false],
];

// --- Load tokens ---
let fileTokens = {};
if (INPUT && fs.existsSync(INPUT)) {
  const content = fs.readFileSync(INPUT, 'utf8');
  fileTokens = INPUT.endsWith('.json') ? extractFromJSON(content) : extractFromCSS(content);
}

// --- Build pairs from file tokens ---
const TEXT_KEYS = ['text', 'foreground', 'on'];
const BG_KEYS = ['bg', 'background', 'surface'];

const textTokens = Object.entries(fileTokens).filter(([k]) => TEXT_KEYS.some(t => k.includes(t)));
const bgTokens = Object.entries(fileTokens).filter(([k]) => BG_KEYS.some(t => k.includes(t)));

const filePairs = [];
for (const [tk, tv] of textTokens) {
  for (const [bk, bv] of bgTokens) {
    filePairs.push([tv, bv, `${tk} on ${bk}`, false, false]);
  }
}

const allPairs = [...DEFAULT_PAIRS, ...filePairs];

// --- Run checks ---
const results = allPairs.map(([fg, bg, label, isLarge, isUI]) => {
  const ratio = contrastRatio(fg, bg);
  const pass = isUI ? ratio >= 3 : isLarge ? ratio >= 3 : ratio >= 4.5;
  return { label, fg, bg, ratio, grade: grade(ratio, isLarge, isUI), pass, isUI, isLarge };
});

const failures = results.filter(r => !r.pass);
const passes = results.filter(r => r.pass);

// --- Output ---
if (JSON_OUTPUT) {
  console.log(JSON.stringify({ total: results.length, failures: failures.length, results }, null, 2));
  process.exit(failures.length > 0 ? 1 : 0);
}

console.log('\n' + '='.repeat(60));
console.log('CONTRAST CHECKER — WCAG 2.2 AA');
console.log('='.repeat(60));
if (INPUT) console.log(`Token file: ${INPUT}`);
console.log(`Checked: ${results.length} pairs | Passed: ${passes.length} | Failed: ${failures.length}\n`);

if (failures.length > 0) {
  console.log('❌ FAILURES (must fix):');
  for (const r of failures) {
    const type = r.isUI ? '[UI Component]' : r.isLarge ? '[Large Text]' : '[Normal Text]';
    console.log(`  ${type} ${r.label}`);
    console.log(`  ${r.fg} on ${r.bg} → ratio ${r.ratio}:1 ${r.grade}`);
    console.log(`  Required: ${r.isUI ? '3:1' : r.isLarge ? '3:1' : '4.5:1'}\n`);
  }
}

if (passes.length > 0) {
  console.log('✅ PASSING:');
  for (const r of passes) {
    console.log(`  ${r.label} → ${r.ratio}:1 ${r.grade}`);
  }
}

console.log('\n' + '='.repeat(60));
console.log(`STATUS: ${failures.length === 0 ? 'PASS ✅' : 'FAIL ❌'}`);
process.exit(failures.length > 0 ? 1 : 0);
