#!/usr/bin/env node
// Claude Code PreToolUse hook: blocks any Write/Edit/MultiEdit (or Bash write)
// targeting .memory/ whose new content matches a credential pattern.
// Hard stop for the "Secret" classification in docs/memory-architecture.md.
// Exit 2 = block the tool call; stderr is shown to Claude as the reason.
const fs = require('fs');
const path = require('path');

const patternsFile = path.join(__dirname, 'secret-patterns.txt');
const patterns = fs.readFileSync(patternsFile, 'utf8')
  .split(/\r?\n/)
  .map(l => l.trim())
  .filter(l => l && !l.startsWith('#'))
  .map(p => new RegExp(p.replace(/\[\[:space:\]\]/g, '\\s'), 'im'));

let input = '';
process.stdin.on('data', c => (input += c));
process.stdin.on('end', () => {
  let event;
  try { event = JSON.parse(input); } catch { process.exit(0); }
  const tool = event.tool_name || '';
  const ti = event.tool_input || {};
  let target = '';
  let text = '';

  if (['Write', 'Edit', 'MultiEdit'].includes(tool)) {
    target = ti.file_path || '';
    text = [ti.content, ti.new_string, ...(ti.edits || []).map(e => e.new_string)]
      .filter(Boolean).join('\n');
  } else if (tool === 'Bash') {
    const cmd = ti.command || '';
    // Only shell commands that write into .memory/ (redirect, tee, cp, mv, sed -i).
    if (/\.memory\//.test(cmd) && /(>|\btee\b|\bcp\b|\bmv\b|sed\s+-i)/.test(cmd)) {
      target = '.memory/ (via Bash)';
      text = cmd;
    }
  }

  const normalized = target.replace(/\\/g, '/');
  if (!/(^|\/)\.memory\//.test(normalized) && !target.startsWith('.memory/ (via Bash)')) {
    process.exit(0);
  }

  const hit = patterns.find(re => re.test(text));
  if (hit) {
    const m = text.match(hit)[0].trim();
    console.error(
      `BLOCKED by memory-guard: the content for ${target} matches a credential pattern ` +
      `(${hit.source}) near "${m.slice(0, 6)}...". Secret values must never be written to memory. ` +
      `Record only the environment variable name (e.g. GEMINI_API_KEY, DB_PASSWORD) and tell the user the value was not stored.`
    );
    process.exit(2);
  }
  process.exit(0);
});
