import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Booking {
  id: number;
  attendeeName: string;
  createdAt: string;
}

export interface ClassSession {
  id: number;
  title: string;
  description: string | null;
  startTime: string;
  capacity: number | null;
  attendeeCount: number;
  bookings: Booking[];
}

export interface SessionPayload {
  title: string;
  description: string | null;
  startTime: string;
  capacity: number | null;
}

@Injectable({
  providedIn: 'root'
})
export class SessionsService {
  private readonly baseUrl = `${environment.apiUrl}/sessions`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<ClassSession[]> {
    return this.http.get<ClassSession[]>(this.baseUrl);
  }

  create(payload: SessionPayload): Observable<ClassSession> {
    return this.http.post<ClassSession>(this.baseUrl, payload);
  }

  update(id: number, payload: SessionPayload): Observable<ClassSession> {
    return this.http.put<ClassSession>(`${this.baseUrl}/${id}`, payload);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  addBooking(sessionId: number, attendeeName: string): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/${sessionId}/bookings`, {
      attendeeName
    });
  }

  cancelBooking(sessionId: number, bookingId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${sessionId}/bookings/${bookingId}`);
  }
}

