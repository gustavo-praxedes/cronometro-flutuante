# Angular UI Patterns Reference

## Modern Angular (v17+) — Signals + Control Flow

### Component State with Signals

```typescript
import { Component, signal, computed, inject } from '@angular/core';

@Component({
  template: `
    @if (error()) {
      <app-error-state [error]="error()" (retry)="load()" />
    } @else if (loading() && !items().length) {
      <app-skeleton-list />
    } @else if (!items().length) {
      <app-empty-state (action)="openCreateDialog()" />
    } @else {
      <app-item-list [items]="items()" />
    }
  `
})
export class ItemListComponent {
  private store = inject(ItemStore);

  items   = this.store.items;
  loading = this.store.loading;
  error   = this.store.error;
}
```

### @for with track (required)

```html
@for (item of items(); track item.id) {
  <app-item-card [item]="item" (delete)="remove(item.id)" />
} @empty {
  <app-empty-state
    title="No items yet"
    actionLabel="Create Item"
    (action)="create()"
  />
}
```

### @defer (progressive loading)

```html
<!-- Critical content loads immediately -->
<app-header />

<!-- Non-critical deferred until viewport -->
@defer (on viewport) {
  <app-comments [postId]="postId()" />
} @placeholder {
  <div class="h-32 bg-muted animate-pulse rounded-lg"></div>
} @loading (minimum 200ms) {
  <app-spinner />
} @error {
  <app-error-state message="Failed to load" />
}
```

### Button Loading State

```typescript
@Component({
  template: `
    <button
      [disabled]="saving()"
      (click)="save()"
      class="btn-primary"
    >
      @if (saving()) {
        <app-spinner size="sm" />
        Saving...
      } @else {
        Save
      }
    </button>
  `
})
export class SaveButtonComponent {
  saving = signal(false);

  async save() {
    this.saving.set(true);
    try {
      await this.service.save();
      this.toast.success('Saved');
    } catch (e) {
      console.error('Save failed:', e);
      this.toast.error('Failed to save. Try again.');
    } finally {
      this.saving.set(false);
    }
  }
}
```

### Reactive Form Pattern

```typescript
@Component({
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <div class="form-field">
        <label for="email">Email</label>
        <input
          id="email"
          type="email"
          formControlName="email"
          [class.error]="isFieldInvalid('email')"
          [attr.aria-invalid]="isFieldInvalid('email')"
          [attr.aria-describedby]="isFieldInvalid('email') ? 'email-error' : null"
        />
        @if (isFieldInvalid('email')) {
          <span id="email-error" role="alert" class="error-text">
            {{ getFieldError('email') }}
          </span>
        }
      </div>

      <button type="submit" [disabled]="form.invalid || submitting()">
        @if (submitting()) { <app-spinner size="sm" /> }
        {{ submitting() ? 'Submitting...' : 'Submit' }}
      </button>
    </form>
  `
})
export class UserFormComponent {
  private fb = inject(FormBuilder);
  submitting = signal(false);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    name:  ['', [Validators.required, Validators.minLength(2)]],
  });

  isFieldInvalid(field: string): boolean {
    const c = this.form.get(field);
    return !!(c?.invalid && c?.touched);
  }

  getFieldError(field: string): string {
    const c = this.form.get(field);
    if (c?.hasError('required')) return 'Required';
    if (c?.hasError('email'))    return 'Invalid email format';
    if (c?.hasError('minlength')) return 'Too short';
    return '';
  }

  async onSubmit() {
    if (this.form.invalid) return;
    this.submitting.set(true);
    try {
      await this.service.submit(this.form.value);
      this.toast.success('Submitted');
    } catch {
      this.toast.error('Submission failed');
    } finally {
      this.submitting.set(false);
    }
  }
}
```

## Signal Store Pattern

```typescript
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';

export const ItemStore = signalStore(
  withState({
    items:   [] as Item[],
    loading: false,
    error:   null as string | null,
  }),
  withMethods((store, service = inject(ItemService)) => ({
    async loadItems() {
      patchState(store, { loading: true, error: null });
      try {
        const items = await service.getAll();
        patchState(store, { items });
      } catch (e) {
        patchState(store, { error: 'Failed to load' });
      } finally {
        patchState(store, { loading: false });
      }
    }
  }))
);
```

## Anti-Patterns

```typescript
// BAD — spinner when data exists (flashes on refetch)
@if (loading()) { <app-spinner /> }

// GOOD — only when no data
@if (loading() && !items().length) { <app-spinner /> }

// BAD — silent error
} catch (e) { console.log(e); }

// GOOD — always surface
} catch (e) { this.toast.error('Action failed. Try again.'); }
```
