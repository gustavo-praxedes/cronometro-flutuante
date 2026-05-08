#!/usr/bin/env node
/**
 * Component States Auditor
 * Scans components for missing UI states: loading, error, empty, success
 * Usage: node audit-states.js [path] [--json]
 */

const fs = require('fs');
const path = require('path');

const TARGET = process.argv[2] || './src';
const JSON_OUTPUT = process.argv.includes('--json');
const EXTENSIONS = ['.tsx', '.jsx', '.ts', '.js'];

// --- Detection patterns ---
const CHECKS = [
  // Data fetching indicators — files that likely need states
  { id: 'has-fetch', regex: /useQuery|useSWR|fetch\(|axios\.|useEffect.*fetch|\.get\(|\.post\(/, type: 'data', label: 'Data fetching detected' },
  { id: 'has-mutation', regex: /useMutation|\.post\(|\.put\(|\.delete\(|\.patch\(/, type: 'mutation', label: 'Mutation detected' },

  // State presence
  { id: 'loading-state', regex: /isLoading|isPending|loading\b.*true|loading\s*&&|skeleton|Skeleton|Spinner|spinner/, type: 'has', label: 'Loading state' },
  { id: 'error-state', regex: /isError|error\s*&&|\.error\b|\berror\b.*return|ErrorState|error-state|<Error/, type: 'has', label: 'Error state' },
  { id: 'empty-state', regex: /\.length\s*===\s*0|!items|!data|EmptyState|empty-state|ListEmptyComponent|@empty/, type: 'has', label: 'Empty state' },
  { id: 'success-feedback', regex: /toast\.|\.success\(|onSuccess|showToast|notification/, type: 'has', label: 'Success feedback' },

  // Anti-patterns
  { id: 'swallowed-error', regex: /catch\s*\([^)]*\)\s*\{[\s\n]*(?:\/\/[^\n]*\n\s*)*\}|catch.*\{\s*\}/, type: 'antipattern', label: 'Empty catch block (error swallowed)', severity: 'critical' },
  { id: 'console-only-error', regex: /catch\s*\([^)]*\)\s*\{[^}]*console\.[log|warn|error]+[^}]*\}/, type: 'antipattern', label: 'catch with console.log only (user not notified)', severity: 'high' },
  { id: 'button-not-disabled', regex: /<button[^>]*onClick[^>]*>(?![^<]*disabled)/, type: 'antipattern', label: 'Button without disabled prop (double-submit risk)', severity: 'high' },
  { id: 'loading-when-data', regex: /(?:isLoading|loading)\s*&&\s*(?!.*!\s*(?:data|items)).*(?:<Spinner|<Skeleton|Loading)/, type: 'antipattern', label: 'Loading shown without checking for existing data', severity: 'medium' },
  { id: 'map-no-empty', regex: /\.map\([^)]+\)(?![\s\S]{0,500}(?:EmptyState|empty|length.*0|!items|!data|@empty|ListEmpty))/, type: 'antipattern', label: 'List .map() without empty state handling', severity: 'high' },
];

// --- File walker ---
function walk(dir, files = []) {
  if (!fs.existsSync(dir)) return files;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory() && !['node_modules', '.git', 'dist', '.next', 'build'].includes(entry.name)) {
      walk(full, files);
    } else if (entry.isFile() && EXTENSIONS.includes(path.extname(entry.name))) {
      files.push(full);
    }
  }
  return files;
}

// --- Audit single file ---
function auditFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');
  const rel = path.relative(TARGET, filePath);

  const detected = {};
  for (const check of CHECKS) {
    detected[check.id] = check.regex.test(content);
  }

  // Only flag components with data fetching
  const hasData = detected['has-fetch'];
  const hasMutation = detected['has-mutation'];
  if (!hasData && !hasMutation) return null;

  const issues = [];
  const antipatterns = [];

  if (hasData) {
    if (!detected['loading-state']) issues.push({ type: 'missing', msg: 'Missing loading state', severity: 'high', fix: 'Add skeleton/spinner when loading && !data' });
    if (!detected['error-state'])   issues.push({ type: 'missing', msg: 'Missing error state', severity: 'high', fix: 'Add error display + retry button' });
    if (!detected['empty-state'])   issues.push({ type: 'missing', msg: 'Missing empty state', severity: 'high', fix: 'Add empty state with next action for lists' });
  }
  if (hasMutation) {
    if (!detected['success-feedback']) issues.push({ type: 'missing', msg: 'Missing success feedback', severity: 'medium', fix: 'Add toast.success() or visual confirmation' });
    if (detected['swallowed-error'])   antipatterns.push({ msg: 'Empty catch block — errors silently swallowed', severity: 'critical', fix: 'Add toast.error() in catch block' });
    if (detected['console-only-error']) antipatterns.push({ msg: 'catch logs to console only — user never notified', severity: 'high', fix: 'Add toast.error() alongside console.error()' });
  }
  if (detected['button-not-disabled']) antipatterns.push({ msg: 'Button may not be disabled during async op', severity: 'high', fix: 'Add disabled={isLoading} and isLoading indicator' });
  if (detected['map-no-empty'])        antipatterns.push({ msg: '.map() without empty state — blank screen on empty data', severity: 'high', fix: 'Add empty state check before .map()' });

  if (!issues.length && !antipatterns.length) return null;

  return { file: rel, hasData, hasMutation, issues, antipatterns };
}

// --- Main ---
const files = walk(TARGET);
const results = files.map(f => { try { return auditFile(f); } catch (_) { return null; } }).filter(Boolean);

const criticalCount = results.reduce((n, r) => n + r.antipatterns.filter(a => a.severity === 'critical').length, 0);
const issueCount = results.reduce((n, r) => n + r.issues.length, 0);
const antiCount = results.reduce((n, r) => n + r.antipatterns.length, 0);

if (JSON_OUTPUT) {
  console.log(JSON.stringify({ files: files.length, flagged: results.length, issues: issueCount, antipatterns: antiCount, critical: criticalCount, results }, null, 2));
  process.exit(criticalCount > 0 ? 1 : 0);
}

console.log('\n' + '='.repeat(60));
console.log('COMPONENT STATES AUDITOR');
console.log('='.repeat(60));
console.log(`Scanned: ${files.length} files | Flagged: ${results.length} | Issues: ${issueCount} | Anti-patterns: ${antiCount}\n`);

for (const r of results) {
  console.log(`📄 ${r.file}`);
  const ctx = [r.hasData && 'data-fetching', r.hasMutation && 'mutation'].filter(Boolean).join(' + ');
  console.log(`   Context: ${ctx}`);

  for (const issue of r.issues) {
    const icon = issue.severity === 'high' ? '🟠' : '🟡';
    console.log(`   ${icon} ${issue.msg}`);
    console.log(`      Fix: ${issue.fix}`);
  }
  for (const ap of r.antipatterns) {
    const icon = ap.severity === 'critical' ? '🔴' : '🟠';
    console.log(`   ${icon} [ANTI-PATTERN] ${ap.msg}`);
    console.log(`      Fix: ${ap.fix}`);
  }
  console.log('');
}

const status = criticalCount > 0 ? 'FAIL ❌' : results.length === 0 ? 'PASS ✅' : 'PASS WITH WARNINGS ⚠️';
console.log('='.repeat(60));
console.log(`STATUS: ${status}`);
process.exit(criticalCount > 0 ? 1 : 0);
