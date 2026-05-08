# Forms Reference

## Form Principles

1. Show errors inline, next to the field
2. Validate on blur (not on every keystroke)
3. Disable submit during async
4. Always provide feedback on success/failure
5. Never block paste

## React Form Pattern

```tsx
const MyForm = () => {
  const [isSubmitting, setSubmitting] = useState(false);

  const handleSubmit = async (values) => {
    if (!isValid) { toast.error('Fix errors first'); return; }
    setSubmitting(true);
    try {
      await submitApi(values);
      toast.success('Saved successfully');
    } catch (e) {
      toast.error('Failed to save. Try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-field">
        <label htmlFor="name">Name</label>
        <input
          id="name"
          aria-describedby={errors.name ? 'name-error' : undefined}
          aria-invalid={!!errors.name}
        />
        {errors.name && (
          <span id="name-error" role="alert">{errors.name}</span>
        )}
      </div>
      <button type="submit" disabled={isSubmitting || !isValid}>
        {isSubmitting ? 'Saving...' : 'Save'}
      </button>
    </form>
  );
};
```

## Angular Reactive Form Pattern

```typescript
form = this.fb.group({
  name:  ['', [Validators.required, Validators.minLength(2)]],
  email: ['', [Validators.required, Validators.email]],
});

isFieldInvalid(field: string): boolean {
  const c = this.form.get(field);
  return c ? c.invalid && c.touched : false;
}

getFieldError(field: string): string {
  const c = this.form.get(field);
  if (c?.hasError('required')) return 'Required';
  if (c?.hasError('email')) return 'Invalid email';
  if (c?.hasError('minlength')) return 'Too short';
  return '';
}
```

## Field Error Display Rules

- Error shown only when field is touched (not on fresh load)
- Error text linked via `aria-describedby`
- `aria-invalid="true"` on invalid input
- Error near the field, not at top of form
- Specific message, not generic "Invalid input"

## Good Error Messages

| Context | Bad | Good |
|---|---|---|
| Required | "Field required" | "Enter your name" |
| Email | "Invalid" | "Use format: name@example.com" |
| Min length | "Too short" | "At least 8 characters" |
| Network | "Error 500" | "Failed to save. Check connection and try again." |

## Confirmation Dialog Pattern

Use for destructive actions:

```tsx
// Never use window.confirm()
const confirmed = await dialog.confirm({
  title: 'Delete item?',
  message: 'This cannot be undone.',
  confirmLabel: 'Delete',
  cancelLabel: 'Cancel',
  destructive: true,
});

if (confirmed) await deleteItem(id);
```
