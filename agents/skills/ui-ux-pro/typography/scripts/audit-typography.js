#!/usr/bin/env node
/**
 * Typography Audit
 * Detects generic AI-default fonts and violations of type scale
 * Usage: node audit-typography.js [path] [--json]
 */

const fs = require('fs');
const path = require('path');

const TARGET = process.argv[2] || '.';
const JSON_OUTPUT = process.argv.includes('--json');
const EXTENSIONS = ['.tsx', '.ts', '.jsx', '.js', '.css', '.scss', '.html'];

// Generic fonts that lack personality (AI slop defaults)
const GENERIC_FONTS = [
  { name: 'Inter', pattern: /['"]Inter['"]|font-inter\b|fontFamily.*Inter/, reason: 'Most overused AI/dev font — no personality' },
  { name: 'Roboto', pattern: /['"]Roboto['"]|font-roboto\b/, reason: 'Android system default — generic for web' },
  { name: 'Arial', pattern: /['"]Arial['"]/, reason: 'System fallback — never use as primary' },
  { name: 'Helvetica', pattern: /['"]Helvetica Neue['"]|['"]Helvetica['"]/, reason: 'Dated system font — use a web font' },
  { name: 'sans-serif (only)', pattern: /fontFamily\s*:\s*['"]sans-serif['"]|font-family\s*:\s*sans-serif\s*;/, reason: 'No brand font defined — just system fallback' },
  { name: 'system-ui (primary)', pattern: /fontFamily.*['"]system-ui['"](?!.*,)|font-family.*system-ui(?!\s*,)/, reason: 'system-ui as primary has no personality — use as fallback only' },
];

// Font size patterns — detect values outside recommended scale
const SCALE_PX = [12, 13, 14, 15, 16, 18, 20, 22, 24, 28, 32, 36, 40, 48, 56, 64, 72, 80, 96];
const SCALE_REM = SCALE_PX.map(v => parseFloat((v / 16).toFixed(4)));

// Tailwind text sizes (allowed)
const ALLOWED_TW = new Set(['text-xs', 'text-sm', 'text-base', 'text-lg', 'text-xl', 'text-2xl', 'text-3xl', 'text-4xl', 'text-5xl', 'text-6xl', 'text-7xl', 'text-8xl', 'text-9xl']);

// Line height checks
const LINE_HEIGHT_RULES = [
  { regex: /line-height\s*:\s*1(?:\.0+)?(?:rem|em|px)?\s*;|leading-none\b|leading-tight\b(?!.*(?:heading|display|title))/, msg: 'Very tight line-height — use 1.5+ for body text', severity: 'medium' },
  { regex: /line-height\s*:\s*(?:2\.|[3-9])\d*|leading-loose\b|leading-relaxed.*heading/, msg: 'Very loose line-height — use 1.1–1.3 for headings, 1.5–1.7 for body', severity: 'low' },
];

// Missing utility classes
const MISSING_UTILITIES = [
  { regex: /<h[1-3][^>]*>(?![^<]*text-balance)/, msg: 'Heading without text-balance — may orphan words', severity: 'low', fix: 'Add text-balance class to headings' },
  { regex: /<p[^>]*>(?![^<]*text-pretty)/, msg: 'Paragraph without text-pretty — suboptimal wrapping', severity: 'low', fix: 'Add text-pretty class to paragraphs' },
  { regex: /\$\d[\d,]*|\d+\.\d+%|[0-9]{2,}(?![px|em|rem|vh|vw|%|ms|s])/, msg: 'Number display without tabular-nums — digits may not align', severity: 'low', fix: 'Add tabular-nums class/style to numeric displays' },
];

// File walker
function walk(dir, files = []) {
  if (!fs.existsSync(dir)) return files;
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, e.name);
    if (e.isDirectory() && !['node_modules', '.git', 'dist', '.next', 'build'].includes(e.name)) walk(full, files);
    else if (e.isFile() && EXTENSIONS.includes(path.extname(e.name))) files.push(full);
  }
  return files;
}

const files = walk(TARGET);
const findings = { genericFonts: [], scaleViolations: [], lineHeight: [], utilities: [] };

for (const file of files) {
  let content;
  try { content = fs.readFileSync(file, 'utf8'); } catch (_) { continue; }
  const rel = path.relative(TARGET, file);
  const lines = content.split('\n');

  // Generic fonts
  for (const { name, pattern, reason } of GENERIC_FONTS) {
    if (pattern.test(content)) {
      const lineNum = lines.findIndex(l => pattern.test(l)) + 1;
      findings.genericFonts.push({ file: rel, line: lineNum, font: name, reason, severity: 'high', fix: 'Choose a distinctive font — see typography/references/font-pairing.md' });
    }
  }

  // Arbitrary font sizes (non-scale values)
  for (const [i, line] of lines.entries()) {
    // CSS px values
    for (const [, val] of line.matchAll(/font-size\s*:\s*(\d+(?:\.\d+)?)px/g)) {
      const px = parseFloat(val);
      if (!SCALE_PX.includes(px)) {
        findings.scaleViolations.push({ file: rel, line: i + 1, value: `${px}px`, severity: 'medium', fix: `Use nearest scale value: ${SCALE_PX.reduce((a, b) => Math.abs(b - px) < Math.abs(a - px) ? b : a)}px` });
      }
    }
    // Tailwind arbitrary text sizes
    for (const [, val] of line.matchAll(/text-\[(\d+(?:\.\d+)?(?:px|rem|em))\]/g)) {
      findings.scaleViolations.push({ file: rel, line: i + 1, value: val, severity: 'medium', fix: 'Use Tailwind text-* scale class instead of arbitrary value' });
    }
    // Line height issues
    for (const rule of LINE_HEIGHT_RULES) {
      if (rule.regex.test(line)) findings.lineHeight.push({ file: rel, line: i + 1, msg: rule.msg, severity: rule.severity });
    }
  }

  // Missing utilities (JSX/HTML only)
  if (['.tsx', '.jsx', '.html'].includes(path.extname(file))) {
    for (const rule of MISSING_UTILITIES) {
      if (rule.regex.test(content)) {
        findings.utilities.push({ file: rel, msg: rule.msg, severity: rule.severity, fix: rule.fix });
      }
    }
  }
}

const total = Object.values(findings).reduce((n, arr) => n + arr.length, 0);
const critical = findings.genericFonts.filter(f => f.severity === 'high').length;

if (JSON_OUTPUT) {
  console.log(JSON.stringify({ files: files.length, total, findings }, null, 2));
  process.exit(critical > 0 ? 1 : 0);
}

console.log('\n' + '='.repeat(60));
console.log('TYPOGRAPHY AUDIT');
console.log('='.repeat(60));
console.log(`Scanned: ${files.length} files | Issues: ${total}\n`);

if (findings.genericFonts.length) {
  console.log('🟠 GENERIC FONTS (high — replace with distinctive choice):');
  for (const f of findings.genericFonts) {
    console.log(`  ${f.file}:${f.line} — "${f.font}"`);
    console.log(`  Reason: ${f.reason}`);
    console.log(`  Fix: ${f.fix}\n`);
  }
}

if (findings.scaleViolations.length) {
  console.log('🟡 SCALE VIOLATIONS (off-scale font sizes):');
  for (const f of findings.scaleViolations.slice(0, 10)) {
    console.log(`  ${f.file}:${f.line} — ${f.value} → ${f.fix}`);
  }
  if (findings.scaleViolations.length > 10) console.log(`  ... and ${findings.scaleViolations.length - 10} more`);
  console.log('');
}

if (findings.lineHeight.length) {
  console.log('🔵 LINE HEIGHT NOTES:');
  for (const f of findings.lineHeight) console.log(`  ${f.file}:${f.line} — ${f.msg}`);
  console.log('');
}

if (findings.utilities.length) {
  console.log('🔵 MISSING UTILITIES:');
  for (const f of findings.utilities) {
    console.log(`  ${f.file} — ${f.msg}`);
    console.log(`  Fix: ${f.fix}`);
  }
  console.log('');
}

console.log('='.repeat(60));
console.log(`Recommended fonts → typography/references/font-pairing.md`);
console.log(`STATUS: ${critical > 0 ? 'FAIL ❌' : total === 0 ? 'PASS ✅' : 'PASS WITH WARNINGS ⚠️'}`);
process.exit(critical > 0 ? 1 : 0);
