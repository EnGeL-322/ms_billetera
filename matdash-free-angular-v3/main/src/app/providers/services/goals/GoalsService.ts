import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class GoalsService {

  constructor(private http: HttpClient) {}

  // ============================================================
  // 📌 CREAR META
  // ============================================================
  createGoal$(payload: any): Observable<any> {
    return this.http.post('goals', payload);
  }

  // ============================================================
  // 📌 LISTAR METAS POR USUARIO
  // ============================================================
  getGoalsByUser$(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`goals/user/${userId}`);
  }

  // ============================================================
  // 📌 EDITAR META COMPLETA
  // ============================================================
  updateGoal$(goalId: number, payload: any): Observable<any> {
    return this.http.put(`goals/${goalId}`, payload);
  }

  // ============================================================
  // 📌 ELIMINAR META
  // ============================================================
  deleteGoal$(goalId: number): Observable<any> {
    return this.http.delete(`goals/${goalId}`);
  }

  // ============================================================
  // 📌 APORTAR MONTO A UNA META
  // ============================================================
  updateGoalAmount$(goalId: number, amount: number): Observable<any> {

    const params = new HttpParams().set('amountChange', amount);

    return this.http.put(`goals/${goalId}/amount`, null, { params });
  }
}
