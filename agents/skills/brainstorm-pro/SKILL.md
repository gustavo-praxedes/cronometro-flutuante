---
name: brainstorm-pro
description: >
  Structured facilitation skill for turning vague ideas into validated designs through
  disciplined dialogue — before any implementation. Use this skill whenever the user
  wants to brainstorm, design, plan, spec, explore ideas, or validate a concept in ANY
  domain (software, business, creative, research, strategy, product, etc.). Triggers on:
  "brainstorm", "let's think through", "help me design", "I have an idea", "plan this
  with me", "validate my idea", "explore options", or any open-ended constructive request.
  Supports two modes: Standard (single facilitator) and Peer Review (multi-role critique).
  Always prefer this skill over ad-hoc reasoning when the user needs structured exploration.
---

# Brainstorm Unified

Turns raw ideas into **validated designs** through structured dialogue.
Applies to any domain. Prevents premature closure, hidden assumptions, and misaligned solutions.

---

## Core Constraints (Always Active)

- No implementation, code, or final output while brainstorming is active.
- One question per message. Never stack questions.
- All assumptions must be named explicitly.
- No section may be skipped. No gate may be bypassed.
- Discussion happens **before** any summary or decision is written.
- No repeated content across sections or messages.

---

## Mode Selection

Choose mode at session start (or default to Standard):

| Mode | When to use |
|------|-------------|
| **Standard** | Most sessions. One facilitator guides design. |
| **Peer Review** | High-stakes, complex, or high-risk ideas. Adds structured critique after design. |

To activate Peer Review: user says "peer review mode", "challenge this", "stress-test", or the design is flagged as high-impact.

---

## Phase 1 — Context Read (Mandatory First Step)

Before asking anything:

1. Review any available context (files, prior conversation, stated constraints).
2. Identify: what already exists vs. what is new, implicit constraints, domain type.
3. Do **not** start designing. Do **not** summarize yet.

Then begin Phase 2.

---

## Phase 2 — Discovery (One Question at a Time)

Goal: shared clarity on purpose, users, constraints, and success.

Rules:
- Ask one question per message. Prefer multiple-choice when possible.
- Topics to cover (not necessarily in this order — adapt to the idea):
  - **Purpose**: What problem does this solve? Why now?
  - **Users/Audience**: Who is this for? What do they need?
  - **Constraints**: Time, resources, scope, non-negotiables.
  - **Success criteria**: How will we know this worked?
  - **Non-goals**: What is explicitly out of scope?

Continue until all five topics are covered to a satisfactory depth.

---

## Phase 3 — Non-Functional Requirements

Explicitly clarify or propose assumptions for (adapt to domain):

- **Scale**: Users, volume, load, or reach.
- **Performance**: Speed, responsiveness, or throughput expectations.
- **Reliability**: Uptime, error tolerance, fallback needs.
- **Security / Privacy**: Data sensitivity, access control, compliance.
- **Maintainability**: Who owns this after launch? What's the lifecycle?

If the user is unsure about any item: propose a reasonable default, label it clearly as **[ASSUMPTION]**.

---

## Phase 4 — Discussion Gate (Hard Gate — Do Not Skip)

At this point, **discuss openly** any tensions, trade-offs, or unresolved points surfaced in Phases 2–3. Ask the user to push back, add nuance, or clarify anything that feels fuzzy.

Only after this discussion is complete, produce the **Understanding Lock**:

### Understanding Lock

> **What:** [1–2 sentences]
> **Why:** [1 sentence]
> **Who:** [1 sentence]
> **Constraints:** [bullet list, max 5]
> **Non-goals:** [bullet list]
> **Assumptions:** [bullet list, each labeled [ASSUMPTION]]
> **Open questions:** [if any remain]

Then ask:

> "Does this accurately reflect your intent? Please confirm or correct anything before we proceed to design."

**Do not advance until explicit confirmation is received.**

---

## Phase 5 — Design Exploration

Once Understanding Lock is confirmed:

1. Propose **2–3 distinct approaches** (not variations of the same idea).
2. Lead with your recommended option.
3. For each approach, state clearly:
   - Core idea (1 sentence)
   - Key trade-offs: complexity, risk, extensibility, cost
   - Why you would or wouldn't choose it

Apply **YAGNI ruthlessly**: do not include features or complexity not required by confirmed goals.

Present one approach at a time. After each, ask: "Does this direction resonate, or should we explore the next option?"

---

## Phase 6 — Incremental Design

Once an approach is chosen, build the design in sections of **200–300 words max**.

After each section, ask: "Does this look right so far?"

Cover only what is relevant to the idea (adapt per domain):

- Overall structure or architecture
- Key components or steps
- Data flow, state, or decision paths
- Error handling or failure modes
- Edge cases
- Testing or validation strategy

Do **not** repeat content from the Understanding Lock.

---

## Phase 7 — Decision Log (Maintained Throughout)

Track every significant decision from Phase 5 onward.

Format per entry:
```
Decision: [what was decided]
Alternatives considered: [what else was on the table]
Rationale: [why this option was chosen]
Objections/risks acknowledged: [if any]
```

The log is a living document. Update it after each phase. It is a required output.

---

## [Peer Review Mode Only] Phase 8 — Structured Critique

Activate only when Peer Review mode is selected. After Phase 6 is complete:

Each role is invoked **one at a time**, in order. Roles may not introduce new features or redesign the system.

### Role 1 — Skeptic
> Assume this design fails. Why?

Scope: surface weaknesses, edge cases, overconfident assumptions, YAGNI violations.

### Role 2 — Constraint Guardian
> Does this hold under real-world pressure?

Scope: performance, scale, security, reliability, operational cost. Reject designs that violate confirmed constraints.

### Role 3 — User/Audience Advocate
> Would the intended user actually succeed with this?

Scope: usability, clarity of flow, error handling from the user perspective, mismatches between intent and experience.

### Role 4 — Arbiter
> Resolve all objections.

Scope: accept or reject each objection with rationale. Update the Decision Log. Declare design status: **APPROVED / REVISE / REJECT**.

The Primary Designer must respond to each role's feedback before the next role activates. The Decision Log must be updated after each role.

---

## Exit Criteria (All Must Be True)

- [ ] Understanding Lock confirmed by user
- [ ] At least one design approach explicitly accepted
- [ ] All major assumptions documented
- [ ] Key risks acknowledged
- [ ] Decision Log complete
- [ ] [Peer Review] All roles invoked and Arbiter declared outcome

If any item is unmet: continue refinement. Do **not** proceed to implementation.

---

## Implementation Handoff (Optional)

After all exit criteria are met and documentation is complete:

> "Ready to move to implementation?"

If yes:
- Produce an explicit, incremental implementation plan
- Scope first step narrowly
- Hand off Decision Log as living documentation

---

## Key Principles

- Discussion precedes every summary.
- One question per message, always.
- Assumptions are explicit, never silent.
- YAGNI ruthlessly.
- Each piece of information appears exactly once.
- Clarity over cleverness.
- Any domain. Any scale.
