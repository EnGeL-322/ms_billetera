import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PcgeAccount {
  id: number;
  code: string;
  name: string;
  classNumber: number;
  level: number;
  parentCode: string;
  nature: string;
  type: string;
}

@Injectable({
  providedIn: 'root'
})
export class PcgeService {

  private baseUrl = 'http://localhost:9020/pcge'; // AJUSTA AL GATEWAY

  constructor(private http: HttpClient) {}

  getAll(): Observable<PcgeAccount[]> {
    return this.http.get<PcgeAccount[]>(`${this.baseUrl}/accounts`);
  }

  search(name: string): Observable<PcgeAccount[]> {
    return this.http.get<PcgeAccount[]>(`${this.baseUrl}/accounts/search?name=${name}`);
  }
}
