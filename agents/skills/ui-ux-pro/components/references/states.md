# Component UI States Reference

## The 4 Required States

### 1. Loading State
Show ONLY when no data exists. Prefer skeletons for known shapes.

**Skeleton vs Spinner:**
| Use Skeleton | Use Spinner |
|---|---|
| Known layout shape | Unknown shape |
| List / card layouts | Modal actions |
| Initial page load | Button submissions |
| Content placeholders | Inline operations |

**React pattern:**
```tsx
if (error) return <ErrorState error={error} onRetry={refetch} />;
if (loading && !data) return <SkeletonList />;
if (!data?.items.length) return <EmptyState />;
return <ItemList items={data.items} />;
```

**Angular signals pattern:**
```typescript
// template
@if (error()) {
  <app-error-state [error]="error()" (retry)="load()" />
} @else if (loading() && !items().length) {
  <app-skeleton-list />
} @else if (!items().length) {
  <app-empty-state />
} @else {
  <app-item-list [items]="items()" />
}
```

---

### 2. Error State
Always surface. Never swallow. Show near the failing action.

**Error Hierarchy:**
```
Field-level inline  → validation errors
Toast notification  → recoverable, user can retry
Error banner        → page-level, data partially usable
Full error screen   → unrecoverable, needs action
```

**React pattern:**
```tsx
const ErrorState = ({ error, onRetry, title }) => (
  <div role="alert" className="error-state">
    <h3>{title ?? 'Something went wrong'}</h3>
    <p>{error.message}</p>
    {onRetry && <button onClick={onRetry}>Try again</button>}
  </div>
);
```

**Anti-pattern (never do this):**
```tsx
} catch (e) {
  console.log(e); // User has no idea!
}
```

**Correct pattern:**
```tsx
} catch (e) {
  console.error('Save failed:', e);
  toast.error('Failed to save. Please try again.');
}
```

---

### 3. Empty State
Every list/collection must have one. Include one clear next action.

```tsx
// React
<div className="empty-state">
  <Icon name="inbox" />
  <h3>No items yet</h3>
  <p>Create your first item to get started</p>
  <button onClick={onCreate}>Create item</button>
</div>

// Angular @empty block
@for (item of items(); track item.id) {
  <app-item-card [item]="item" />
} @empty {
  <app-empty-state
    icon="folder-open"
    title="No items yet"
    description="Create your first item"
    actionLabel="Create Item"
    (action)="openCreateDialog()"
  />
}
```

---

### 4. Button States

```tsx
// CORRECT — disabled + loading indicator
<button
  onClick={handleSubmit}
  disabled={isSubmitting || !isValid}
>
  {isSubmitting ? <Spinner size="sm" /> : null}
  {isSubmitting ? 'Saving...' : 'Save Changes'}
</button>

// WRONG — user can click multiple times
<button onClick={handleSubmit}>
  {isSubmitting ? 'Saving...' : 'Save'}
</button>
```

## Checklist Before Shipping

- [ ] Error state: shown to user, not swallowed
- [ ] Loading: only shown when no data
- [ ] Empty: collection has empty state with action
- [ ] Buttons: disabled during async, shows loading
- [ ] All mutations: have onError handler
- [ ] All user actions: have visual feedback
- [ ] Optimistic updates: rollback on failure
- [ ] Loading states: announced to screen readers
- [ ] Error messages: linked to form fields via aria-describedby
- [ ] Focus: managed after state changes
