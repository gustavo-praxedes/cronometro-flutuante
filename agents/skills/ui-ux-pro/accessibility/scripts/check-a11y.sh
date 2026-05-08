#!/bin/bash
# A11y Static Checker
# Runs axe-core accessibility checks on a built web app
# Usage: bash check-a11y.sh [url-or-path] [--json]
#
# Requirements: node, npx (axe-cli auto-installed on first run)

set -e

URL="${1:-http://localhost:3000}"
JSON_OUTPUT=false
[[ "$*" == *"--json"* ]] && JSON_OUTPUT=true

# Resolve local path to file:// URL
if [[ "$URL" != http* ]]; then
  if [[ -f "$URL" ]]; then
    URL="file://$(realpath "$URL")"
  elif [[ -d "$URL" ]]; then
    URL="file://$(realpath "$URL")/index.html"
  fi
fi

echo ""
echo "============================================================"
echo "A11Y STATIC CHECKER — WCAG 2.2 AA"
echo "============================================================"
echo "Target: $URL"
echo ""

# Check node
if ! command -v node &> /dev/null; then
  echo "❌ Node.js is required. Install from https://nodejs.org"
  exit 1
fi

# Check/install axe-cli
if ! command -v axe &> /dev/null && ! npx --yes axe --version &> /dev/null 2>&1; then
  echo "📦 Installing axe-cli..."
  npm install -g axe-cli 2>/dev/null || npx --yes axe --version > /dev/null
fi

echo "🔍 Running axe-core accessibility scan..."
echo ""

# Run axe
AXE_CMD="npx --yes axe \"$URL\" --tags wcag2aa,wcag21aa,wcag22aa"

if $JSON_OUTPUT; then
  $AXE_CMD --reporter json 2>/dev/null || {
    echo '{"error": "axe scan failed — ensure the URL is accessible"}' 
    exit 1
  }
  exit 0
fi

# Human output — capture and parse
RESULT=$($AXE_CMD 2>&1) || true

echo "$RESULT"

# Summary
if echo "$RESULT" | grep -q "0 violations"; then
  echo ""
  echo "============================================================"
  echo "STATUS: PASS ✅ — No WCAG 2.2 AA violations found"
  echo "============================================================"
  exit 0
else
  VIOLATIONS=$(echo "$RESULT" | grep -c "Violation" 2>/dev/null || echo "?")
  echo ""
  echo "============================================================"
  echo "STATUS: FAIL ❌ — Accessibility violations found"
  echo "============================================================"
  echo ""
  echo "Common fixes:"
  echo "  - Missing alt text:        Add alt=\"description\" to <img>"
  echo "  - Missing aria-label:      Add aria-label=\"action\" to icon buttons"
  echo "  - Insufficient contrast:   Run color/scripts/check-contrast.js"
  echo "  - Missing form labels:     Add <label for=\"id\"> to every input"
  echo "  - Focus not visible:       Add focus-visible:ring-2 to interactive elements"
  exit 1
fi
