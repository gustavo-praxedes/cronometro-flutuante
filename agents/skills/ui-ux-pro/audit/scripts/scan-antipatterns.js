#!/usr/bin/env node
/**
 * Anti-Pattern Scanner
 * Detects common UI/UX anti-patterns across codebase
 * Usage: node scan-antipatterns.js [path] [--json]
 */

const fs = require('fs');
const path = require('path');

const TARGET = process.argv[2] || '.';
const JSON_OUTPUT = process.argv.includes('--json');

const EXTENSIONS = ['.tsx', '.ts', '.jsx', '.js', '.css', '.scss'];

const PATTERNS = [
  // Layout
  { id: 'h-screen', regex: /\bh-screen\b/, msg: 'Use h-dvh instead of h-screen (mobile browser bars)', severity: 'high', fix: 'Replace h-screen → h-dvh' },
  { id: 'w-h-square', regex: /\bw-(\d+)\s+h-\1\b/, msg: 'Use size-N for square elements instead of w-N + h-N', severity: 'low', fix: 'Replace w-10 h-10 → size-10' },

  // Z-index
  { id: 'z-arbitrary', regex: /\bz-\[(\d+)\]/, msg: 'Arbitrary z-index — use fixed scale (z-10/20/30/40/50)', severity: 'medium', fix: 'Use z-10, z-20, z-30, z-40, or z-50' },
  { id: 'z-9999', regex: /\bz-(?:index\s*:\s*)?9{2,}/, msg: 'z-index 999+ detected — use semantic z-index scale', severity: 'high', fix: 'Use z-50 (max in fixed scale)' },

  // Colors / AI slop
  { id: 'purple-gradient', regex: /(?:from|via|to)-(?:purple|violet|indigo)-\d{3}.*(?:from|via|to)-white|bg-gradient.*purple/i, msg: 'Purple gradient on white — #1 AI-slop marker', severity: 'high', fix: 'Choose a distinctive brand direction' },
  { id: 'hardcoded-hex', regex: /#[0-9a-fA-F]{3,6}(?![\w-])/, msg: 'Hardcoded hex color — use design tokens/CSS variables', severity: 'high', fix: 'Replace with var(--color-*) or Tailwind semantic class' },

  // Animation / Performance
  { id: 'will-change-always', regex: /will-change\s*:\s*transform(?!.*animation|.*@keyframes)/, msg: 'will-change:transform applied statically — only add during active animation', severity: 'medium', fix: 'Add will-change on animationstart, remove on animationend' },
  { id: 'animate-width', regex: /transition.*\bwidth\b|animate.*\bwidth\b/, msg: 'Animating width triggers layout — use transform instead', severity: 'high', fix: 'Use scaleX() transform or max-width with opacity' },
  { id: 'animate-height', regex: /transition.*\bheight\b|animate.*\bheight\b/, msg: 'Animating height triggers layout — use transform or max-height', severity: 'high', fix: 'Use scaleY() or max-height transition' },

  // React anti-patterns
  { id: 'useeffect-render', regex: /useEffect\([^)]*\[\]\)[\s\S]{0,200}setState/, msg: 'useEffect with setState for render logic — use derived state', severity: 'medium', fix: 'Compute value directly in render or use useMemo' },
  { id: 'index-key', regex: /key=\{(?:index|i|idx)\}|\.map\(\([^,)]+,\s*(?:index|i|idx)\)[^)]*\)\s*=>\s*[^]*?key=\{(?:index|i|idx)\}/, msg: 'Array index as React key — causes reorder bugs', severity: 'high', fix: 'Use stable unique ID (item.id)' },
  { id: 'console-log-prod', regex: /console\.log\(/, msg: 'console.log found — strip from production builds', severity: 'medium', fix: 'Remove or use a logger that strips in production' },

  // Accessibility
  { id: 'div-onclick', regex: /<div[^>]*onClick[^>]*>(?!.*role=["']button["'])/, msg: 'div with onClick — use <button> for keyboard accessibility', severity: 'high', fix: 'Replace <div onClick> with <button type="button">' },
  { id: 'img-no-alt', regex: /<img(?![^>]*alt=)[^>]*>/, msg: 'img without alt attribute', severity: 'high', fix: 'Add alt="" (decorative) or alt="description" (meaningful)' },
  { id: 'outline-none', regex: /outline\s*:\s*none|outline-none/, msg: 'outline:none removes focus indicator — add visible alternative', severity: 'high', fix: 'Add focus-visible:ring-2 or custom focus style' },

  // Mobile
  { id: 'scrollview-map', regex: /<ScrollView[^>]*>[\s\S]{0,500}\.map\(/, msg: 'ScrollView + .map() — use FlatList for long lists (memory)', severity: 'critical', fix: 'Replace ScrollView + map with FlatList' },
  { id: 'asyncstorage-token', regex: /AsyncStorage.*(?:token|auth|password|secret|key)/i, msg: 'Sensitive data in AsyncStorage — use SecureStore/Keychain', severity: 'critical', fix: 'Use expo-secure-store or @react-native-keychain' },

  // Tailwind
  { id: 'no-cn-utility', regex: /className=\{`[^`]*\$\{[^}]+\}[^`]*`\}/, msg: 'Template literal className — use cn() utility instead', severity: 'low', fix: 'import { cn } from "@/lib/utils"; use cn("base", condition && "extra")' },
];

// --- File walking ---
function walk(dir, files = []) {
  if (!fs.existsSync(dir)) return files;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory() && !['node_modules', '.git', 'dist', '.next', 'build', 'out'].includes(entry.name)) {
      walk(full, files);
    } else if (entry.isFile() && EXTENSIONS.includes(path.extname(entry.name))) {
      files.push(full);
    }
  }
  return files;
}

// --- Scanner ---
function scanFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');
  const lines = content.split('\n');
  const findings = [];

  for (const pattern of PATTERNS) {
    lines.forEach((line, i) => {
      if (pattern.regex.test(line)) {
        findings.push({
          file: filePath,
          line: i + 1,
          code: line.trim().slice(0, 100),
          ...pattern,
        });
      }
    });
  }
  return findings;
}

// --- Main ---
const files = walk(TARGET);
const allFindings = [];

for (const file of files) {
  try {
    allFindings.push(...scanFile(file));
  } catch (_) {}
}

const bySeverity = { critical: [], high: [], medium: [], low: [] };
for (const f of allFindings) {
  (bySeverity[f.severity] || bySeverity.low).push(f);
}

if (JSON_OUTPUT) {
  console.log(JSON.stringify({ total: allFindings.length, files: files.length, findings: allFindings }, null, 2));
  process.exit(bySeverity.critical.length > 0 ? 1 : 0);
}

// Human output
console.log('\n' + '='.repeat(60));
console.log('ANTI-PATTERN SCANNER');
console.log('='.repeat(60));
console.log(`Scanned: ${files.length} files | Found: ${allFindings.length} issues\n`);

for (const [sev, items] of Object.entries(bySeverity)) {
  if (!items.length) continue;
  const icon = sev === 'critical' ? '🔴' : sev === 'high' ? '🟠' : sev === 'medium' ? '🟡' : '🔵';
  console.log(`${icon} ${sev.toUpperCase()} (${items.length})`);
  for (const f of items.slice(0, 10)) {
    console.log(`  ${path.relative(TARGET, f.file)}:${f.line}`);
    console.log(`  → ${f.msg}`);
    console.log(`  ✓ ${f.fix}\n`);
  }
  if (items.length > 10) console.log(`  ... and ${items.length - 10} more\n`);
}

const status = bySeverity.critical.length > 0 ? 'FAIL' : allFindings.length === 0 ? 'PASS ✅' : 'PASS WITH WARNINGS';
console.log('='.repeat(60));
console.log(`STATUS: ${status}`);
process.exit(bySeverity.critical.length > 0 ? 1 : 0);
