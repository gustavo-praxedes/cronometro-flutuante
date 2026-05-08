# UX Feedback — Loading & Empty States Reference

## Loading States

### Skeleton Patterns

```tsx
// List skeleton
export function SkeletonList({ count = 5 }: { count?: number }) {
  return (
    <div className="space-y-3" aria-busy="true" aria-label="Loading items">
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="flex items-center gap-3 p-4 rounded-lg bg-surface-secondary animate-pulse">
          <div className="size-10 rounded-full bg-muted" />
          <div className="flex-1 space-y-2">
            <div className="h-4 w-3/4 rounded bg-muted" />
            <div className="h-3 w-1/2 rounded bg-muted" />
          </div>
        </div>
      ))}
    </div>
  );
}

// Card skeleton
export function SkeletonCard() {
  return (
    <div className="rounded-xl border p-6 animate-pulse space-y-4">
      <div className="h-6 w-1/3 rounded bg-muted" />
      <div className="space-y-2">
        <div className="h-4 w-full rounded bg-muted" />
        <div className="h-4 w-5/6 rounded bg-muted" />
      </div>
      <div className="h-9 w-24 rounded-md bg-muted" />
    </div>
  );
}
```

### Flash Prevention

```tsx
// Delay skeleton to avoid flash on fast responses
function useDelayedLoading(loading: boolean, delay = 200) {
  const [show, setShow] = useState(false);

  useEffect(() => {
    if (!loading) { setShow(false); return; }
    const t = setTimeout(() => setShow(true), delay);
    return () => clearTimeout(t);
  }, [loading, delay]);

  return show;
}

// Usage
const showSkeleton = useDelayedLoading(loading && !data);
```

### Inline Loading (buttons, inputs)

```tsx
// Button loading
<button disabled={isLoading} aria-busy={isLoading}>
  {isLoading ? (
    <>
      <svg className="animate-spin size-4" .../>
      Saving...
    </>
  ) : 'Save'}
</button>

// Input loading
<div className="relative">
  <input value={query} onChange={e => setQuery(e.target.value)} />
  {isSearching && (
    <div className="absolute right-3 top-1/2 -translate-y-1/2">
      <Spinner size="sm" />
    </div>
  )}
</div>
```

---

## Empty States

### Anatomy

```
[Icon or illustration — contextual, not generic]
[Title — what's missing]
[Description — why and what to do]
[Primary CTA — one clear action]
[Optional: secondary link]
```

### Context Variants

**First time / no data yet:**
```tsx
<EmptyState
  icon={<FolderIcon />}
  title="No projects yet"
  description="Create your first project to get your team started."
  action={{ label: "Create project", onClick: handleCreate }}
/>
```

**Search / filter with no results:**
```tsx
<EmptyState
  icon={<SearchIcon />}
  title={`No results for "${query}"`}
  description="Try different keywords or clear your filters."
  action={{ label: "Clear search", onClick: clearSearch }}
/>
```

**Awaiting others:**
```tsx
<EmptyState
  icon={<UsersIcon />}
  title="Waiting for your team"
  description="Share this link to invite collaborators."
  action={{ label: "Copy invite link", onClick: copyLink }}
/>
```

**Permission / restricted:**
```tsx
<EmptyState
  icon={<LockIcon />}
  title="Access restricted"
  description="You don't have permission to view this. Contact your admin."
/>
```

### Empty State Component Pattern

```tsx
interface EmptyStateProps {
  icon?: React.ReactNode;
  title: string;
  description?: string;
  action?: { label: string; onClick: () => void };
  secondaryAction?: { label: string; onClick: () => void };
}

export function EmptyState({ icon, title, description, action, secondaryAction }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-4 text-center">
      {icon && (
        <div className="mb-4 text-muted-foreground opacity-40">
          {icon}
        </div>
      )}
      <h3 className="text-base font-semibold text-foreground mb-1">{title}</h3>
      {description && (
        <p className="text-sm text-muted-foreground mb-6 max-w-xs text-pretty">{description}</p>
      )}
      {action && (
        <button onClick={action.onClick} className="btn-primary">
          {action.label}
        </button>
      )}
      {secondaryAction && (
        <button onClick={secondaryAction.onClick} className="btn-ghost mt-2 text-sm">
          {secondaryAction.label}
        </button>
      )}
    </div>
  );
}
```

---

## Success States

### Toast Patterns

```tsx
// Simple success
toast.success("Saved");

// With undo (destructive reversible)
toast.success("Deleted", {
  action: { label: "Undo", onClick: handleUndo },
  duration: 5000,
});

// With detail
toast.success("Invite sent to sarah@company.com");
```

### Inline Success

```tsx
// Form submit success
{submitted && !error && (
  <div role="status" className="flex items-center gap-2 text-success text-sm">
    <CheckIcon className="size-4" />
    Changes saved successfully
  </div>
)}
```

### Optimistic Updates

```tsx
// Update UI immediately, revert on error
const handleDelete = async (id: string) => {
  const prev = items;
  setItems(items.filter(i => i.id !== id));  // optimistic
  try {
    await deleteItem(id);
    toast.success("Deleted · Undo", { action: { label: "Undo", onClick: () => setItems(prev) } });
  } catch {
    setItems(prev);  // revert
    toast.error("Couldn't delete. Try again.");
  }
};
```
