# Friction Reduction Reference

## Friction Audit Process

### Step 1: Map the Current Flow
List every screen, decision point, and form field in sequence.

### Step 2: Score Each Step
For each step ask:
- Is this decision necessary? (can we default it?)
- Is this field required for this step? (can we ask later?)
- Is this page necessary? (can we merge?)
- Does this step create doubt or hesitation?

### Step 3: Apply Reduction

| Friction Type | Reduction Strategy |
|---|---|
| Too many form fields | Defer optional fields to after activation |
| Too many choices | Use smart defaults; hide advanced |
| Too many screens | Merge compatible steps |
| Unclear next action | Single primary CTA per screen |
| Cognitive overload | Progressive disclosure |
| Hesitation / doubt | Social proof, guarantees, previews |

---

## Onboarding Friction Patterns

### High Friction (Avoid)
```
[Sign up form: name/email/password/company/role/team size]
    → [Email verification]
        → [Feature tour (5 screens)]
            → [Empty dashboard]
```

### Low Friction (Preferred)
```
[Email only]
    → [One action that delivers value]
        → [Personalization (max 3q, skippable)]
            → [First success moment]
```

### Techniques
- **Value before signup**: Let users try before creating account
- **Progressive profiling**: Collect info spread across sessions
- **Smart defaults**: Pre-fill based on email domain, location, etc.
- **Skip everything**: Every optional step gets a "Skip" link

---

## Commitment + Consistency Ladder

Build toward the big ask with small yes-steps:

```
Micro-commitment 1: "See how it works" (free, no signup)
     ↓
Micro-commitment 2: Email for result (low cost)
     ↓
Micro-commitment 3: Create profile (moderate cost)
     ↓
Big ask: Subscribe / Invite team / Pay (high cost)
```

**Rule**: Never ask for highest commitment before establishing value.

---

## CTA Optimization

### Primary CTA Rules
- One primary CTA per screen
- Verb + benefit when possible: "Start free trial" not "Try now"
- Position: thumb zone (mobile), above fold (web)
- Size: large enough to tap (min 44px height)
- Color: highest contrast on page

### CTA Hierarchy
```
Primary:   Brand color, filled, prominent
Secondary: Outlined or ghost, less prominent
Tertiary:  Text link, minimal visual weight
```

### Copy Upgrades
| Generic | Specific |
|---|---|
| "Get started" | "Start your free trial" |
| "Sign up" | "Create your account" |
| "Learn more" | "See how it works" |
| "Submit" | "Send my request" |
| "Continue" | "Set up my workspace" |

---

## Social Proof Patterns

### Types by Trust Stage

| Stage | Best Proof Type |
|---|---|
| Awareness | Usage numbers ("10,000+ teams") |
| Consideration | Case studies, logos |
| Decision | Testimonials with specifics |
| Post-purchase | Community, NPS, reviews |

### Placement Rules
- Near primary CTA → removes last-moment doubt
- Near pricing → reduces risk perception
- On signup form → reduces signup hesitation
- After onboarding step → reinforces good decision

---

## Loss Framing (Use Carefully)

### When to Use
- Audience already values the outcome
- Cost of inaction is real and near-term
- Urgency is genuine (not manufactured)

### Framing Templates
```
"Every day without X costs [specific amount/outcome]"
"Teams without X take [X times longer] to [task]"
"You're [currently doing workaround] — there's a better way"
```

### When NOT to Use
- Audience doesn't yet value the outcome (build desire first)
- The consequence is distant or uncertain
- Repeated too often (loses credibility)
