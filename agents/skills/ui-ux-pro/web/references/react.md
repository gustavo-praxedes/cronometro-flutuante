# React UI Patterns Reference

## State Management Order

1. `useState` — local UI state
2. `useReducer` — complex local state
3. Context — cross-component shared state
4. Zustand / Jotai — app-wide state
5. React Query / SWR — server state

## Data Fetching Pattern

```tsx
// React Query (recommended)
const { data, loading, error, refetch } = useQuery({
  queryKey: ['items'],
  queryFn: fetchItems,
});

if (error) return <ErrorState error={error} onRetry={refetch} />;
if (loading && !data) return <SkeletonList />;
if (!data?.length) return <EmptyState />;
return <ItemList items={data} />;
```

## Mutation Pattern

```tsx
const mutation = useMutation({
  mutationFn: createItem,
  onSuccess: () => {
    toast.success('Item created');
    queryClient.invalidateQueries(['items']);
  },
  onError: (error) => {
    console.error('createItem failed:', error);
    toast.error('Failed to create item. Try again.');
  },
});

// In component:
<button
  onClick={() => mutation.mutate(data)}
  disabled={mutation.isPending}
>
  {mutation.isPending ? 'Creating...' : 'Create'}
</button>
```

## Component Architecture

```tsx
// Function declaration (not const arrow for components)
export function ItemCard({ item, onDelete, className }: ItemCardProps) {
  return (
    <div className={cn("card-base", className)} data-slot="item-card">
      {/* content */}
    </div>
  );
}

// CVA variants
const cardVariants = cva("rounded-lg border p-4", {
  variants: {
    variant: {
      default:  "bg-card text-card-foreground",
      outline:  "border-2 bg-transparent",
      ghost:    "border-transparent bg-transparent",
    },
    size: {
      sm: "p-2 text-sm",
      md: "p-4 text-base",
      lg: "p-6 text-lg",
    },
  },
  defaultVariants: { variant: "default", size: "md" },
});
```

## Radix UI Patterns

```tsx
// Dialog (correct pattern)
<Dialog.Root open={open} onOpenChange={setOpen}>
  <Dialog.Trigger asChild>
    <button>Open</button>   {/* asChild avoids nested button */}
  </Dialog.Trigger>
  <Dialog.Portal>
    <Dialog.Overlay className="dialog-overlay" />
    <Dialog.Content className="dialog-content">
      <Dialog.Title>Title</Dialog.Title>          {/* Required for a11y */}
      <Dialog.Description>Description</Dialog.Description>  {/* Required */}
      {/* content */}
      <Dialog.Close asChild><button>Close</button></Dialog.Close>
    </Dialog.Content>
  </Dialog.Portal>
</Dialog.Root>

// Dropdown
<DropdownMenu.Root>
  <DropdownMenu.Trigger asChild>
    <button aria-label="Actions">⋯</button>
  </DropdownMenu.Trigger>
  <DropdownMenu.Portal>
    <DropdownMenu.Content>
      <DropdownMenu.Item onSelect={handleEdit}>Edit</DropdownMenu.Item>
      <DropdownMenu.Separator />
      <DropdownMenu.Item onSelect={handleDelete} className="text-destructive">
        Delete
      </DropdownMenu.Item>
    </DropdownMenu.Content>
  </DropdownMenu.Portal>
</DropdownMenu.Root>
```

## Tailwind + cn() Pattern

```tsx
import { cn } from "@/lib/utils";  // clsx + tailwind-merge

<div className={cn(
  "base-styles rounded-lg p-4",
  isActive && "ring-2 ring-brand",
  isDisabled && "opacity-50 cursor-not-allowed",
  className,  // always allow override
)} />
```

## Performance Checklist

- [ ] Heavy components code-split with `React.lazy`
- [ ] Lists virtualized if >100 items (react-virtual or tanstack/virtual)
- [ ] Images optimized (next/image or explicit width/height)
- [ ] No anonymous functions in JSX for stable references
- [ ] Context split to prevent unnecessary rerenders
