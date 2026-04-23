import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface Wallet {
  id: number;
  userId: number;
  balance: number;
  currency: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class WalletService {

  private readonly baseUrl = `${environment.apiUrl}/wallets`;

  constructor(private http: HttpClient) {}

  /** Obtener wallet por userId */
  getWalletByUserId$(userId: number): Observable<Wallet> {
    return this.http.get<Wallet>(`${this.baseUrl}/user/${userId}`);
  }

  /** Crear wallet */
  createWallet$(wallet: Partial<Wallet>): Observable<Wallet> {
    return this.http.post<Wallet>(`${this.baseUrl}`, wallet);
  }

  /** Eliminar wallet */
  deleteWalletByUserId$(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/user/${userId}`);
  }

 subtractFromWallet$(userId: number, amount: number): Observable<any> {
  return this.http.patch(`${this.baseUrl}/${userId}/subtract`, { amount }, {
    responseType: 'text' as 'json'
  });
}

}