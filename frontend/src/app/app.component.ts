import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import {
  ClassSession,
  SessionsService,
  SessionPayload,
  Booking
} from './services/sessions.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  sessions: ClassSession[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  editingId: number | null = null;
  bookingNames: Record<number, string> = {};

  readonly sessionForm = this.fb.group({
    title: ['', Validators.required],
    description: [''],
    startTime: ['', Validators.required],
    capacity: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly sessionsService: SessionsService
  ) {}

  ngOnInit(): void {
    this.loadSessions();
  }

  loadSessions(): void {
    this.loading = true;
    this.sessionsService
      .list()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (sessions) => {
          this.sessions = sessions;
        },
        error: (error) => this.handleError(error)
      });
  }

  submitSession(): void {
    if (this.sessionForm.invalid) {
      this.sessionForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    const payload = this.buildPayload();
    const request$ = this.editingId
      ? this.sessionsService.update(this.editingId, payload)
      : this.sessionsService.create(payload);

    request$
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => {
          this.resetForm();
          this.loadSessions();
        },
        error: (error) => this.handleError(error)
      });
  }

  startEdit(session: ClassSession): void {
    this.editingId = session.id;
    this.sessionForm.setValue({
      title: session.title,
      description: session.description ?? '',
      startTime: this.toInputDate(session.startTime),
      capacity: session.capacity?.toString() ?? ''
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.sessionForm.reset();
  }

  deleteSession(session: ClassSession): void {
    if (
      this.saving ||
      !confirm(`Czy na pewno chcesz usunąć zajęcia "${session.title}"?`)
    ) {
      return;
    }

    this.saving = true;
    this.sessionsService
      .remove(session.id)
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => this.loadSessions(),
        error: (error) => this.handleError(error)
      });
  }

  addBooking(session: ClassSession): void {
    const name = (this.bookingNames[session.id] ?? '').trim();
    if (!name || this.sessionLocked(session) || this.sessionFull(session)) {
      return;
    }

    this.saving = true;
    this.sessionsService
      .addBooking(session.id, name)
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => {
          this.bookingNames[session.id] = '';
          this.loadSessions();
        },
        error: (error) => this.handleError(error)
      });
  }

  cancelBooking(session: ClassSession, booking: Booking): void {
    if (this.sessionLocked(session)) {
      return;
    }

    this.saving = true;
    this.sessionsService
      .cancelBooking(session.id, booking.id)
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => this.loadSessions(),
        error: (error) => this.handleError(error)
      });
  }

  sessionLocked(session: ClassSession): boolean {
    const start = new Date(session.startTime).getTime();
    const now = Date.now();
    const hoursDiff = (start - now) / (1000 * 60 * 60);
    return hoursDiff <= 24;
  }

  sessionFull(session: ClassSession): boolean {
    return (
      session.capacity !== null &&
      session.attendeeCount >= session.capacity
    );
  }

  private buildPayload(): SessionPayload {
    const raw = this.sessionForm.value;
    return {
      title: raw.title!.trim(),
      description: raw.description?.trim() || null,
      startTime: this.normalizeDate(raw.startTime!),
      capacity: raw.capacity ? Number(raw.capacity) : null
    };
  }

  private normalizeDate(value: string): string {
    if (!value) {
      return value;
    }
    return value.length === 16 ? `${value}:00` : value;
  }

  private toInputDate(value: string): string {
    return value ? value.slice(0, 16) : '';
  }

  private handleError(error: unknown): void {
    const message =
      (error as { error?: { message?: string }; message?: string })?.error
        ?.message ||
      (error as { message?: string }).message ||
      'Wystąpił nieoczekiwany błąd.';
    this.errorMessage = message;
    console.error(error);
  }
}
