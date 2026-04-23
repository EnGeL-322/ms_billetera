import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * 🔹 Servicio Genérico para CRUD
 * Se usa para todos los módulos excepto AuthService.
 */
export class EntityDataService<T> {

  constructor(
    protected httpClient: HttpClient,
    protected endPoint: string,
  ) {}

  // GET ALL
  public getAll$(): Observable<T> {
    return this.httpClient.get<T>(`${this.endPoint}`);
  }

  // GET BY ID
  public getById$(id: string | number): Observable<T> {
    return this.httpClient.get<T>(`${this.endPoint}/${id}`);
  }

  // POST - Crear nuevo registro
  public add$(entity: any): Observable<T> {
    return this.httpClient.post<T>(`${this.endPoint}`, entity);
  }

  // PUT - Actualizar
  public update$(id: string | number, entity: any): Observable<T> {
    return this.httpClient.put<T>(`${this.endPoint}/${id}`, entity);
  }

  // DELETE
  public delete$(id: string | number): Observable<any> {
    return this.httpClient.delete<any>(`${this.endPoint}/${id}`);
  }

}
