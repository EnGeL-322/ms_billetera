import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { EntityDataService } from '../../utils/entity-data';
import { Event } from '../../models/event.model';
import { EventUpdateSpentDTO } from '../../models/event-update-spent.model';

@Injectable({
  providedIn: 'root'
})
export class EventService extends EntityDataService<Event> {

  constructor(protected override httpClient: HttpClient) {
    super(httpClient, 'events');
  }

  // ===========================================================
  // 🔹 EVENTOS
  // ===========================================================

  /** Obtener todos los eventos por usuario */
  public getEventsByUser$(userId: number): Observable<Event[]> {
    return this.httpClient.get<Event[]>(`events/user/${userId}`);
  }

  /** Crear evento */
  public createEvent$(payload: Partial<Event>): Observable<Event> {
    return this.httpClient.post<Event>(`events`, payload);
  }

  /** Actualizar evento */
  public updateEvent$(id: number, payload: Partial<Event>): Observable<Event> {
    return this.httpClient.put<Event>(`events/${id}`, payload);
  }

  /** Eliminar evento por ID */
  public deleteEvent$(id: number): Observable<any> {
    return this.httpClient.delete(`events/${id}`, {
      responseType: 'text' as 'json'   // ⭐ SOLUCIÓN AL ERROR 200 PARSING
    });
  }

  // ===========================================================
  // 🔹 GASTOS (PATCH)
  // ===========================================================

  /** Actualizar (sumar) gasto del evento */
  public updateSpent$(eventId: number, payload: EventUpdateSpentDTO): Observable<Event> {
    return this.httpClient.patch<Event>(`events/${eventId}/spent`, payload);
  }
}
