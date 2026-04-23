import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Bank } from '../../models/bank.model';
import { UserBankBalance } from '../../models/user-bank-balance.model';
import { UserBankIncome } from '../../models/user-bank-income.model';

@Injectable({
  providedIn: 'root'
})
export class BankOperationsService {

  private readonly BANKS_URL = 'bancos';
  private readonly OPS_URL = 'bank-ops';

  constructor(private http: HttpClient) {}

  // =====================================================
  // 🟩 1. BANCOS
  // =====================================================

  public getBanks$(): Observable<Bank[]> {
    return this.http.get<Bank[]>(`${this.BANKS_URL}`);
  }

  public createBank$(payload: Partial<Bank>): Observable<Bank> {
    return this.http.post<Bank>(`${this.BANKS_URL}`, payload);
  }

  public getBankById$(id: number): Observable<Bank> {
    return this.http.get<Bank>(`${this.BANKS_URL}/${id}`);
  }

  // =====================================================
  // 🟩 2. AÑADIR DINERO
  // =====================================================

  public addIncomeToBank$(payload: {
    userId: number;
    bankId: number;
    amount: number;
    description: string;
  }): Observable<UserBankIncome> {
    return this.http.post<UserBankIncome>(`${this.OPS_URL}/add-income`, payload);
  }

  // =====================================================
  // 🟩 3. HISTORIAL
  // =====================================================

  public getIncomeHistory$(userId: number, bankId: number): Observable<UserBankIncome[]> {
    return this.http.get<UserBankIncome[]>(`${this.OPS_URL}/history/${userId}/${bankId}`);
  }

  // =====================================================
  // 🟩 4. TRANSFERIR A WALLET
  // =====================================================

  public transferToWallet$(payload: {
    userId: number;
    bankId: number;
    amount: number;
  }): Observable<any> {
    return this.http.post<any>(`${this.OPS_URL}/transfer`, payload);
  }

  // =====================================================
  // 🟩 5. ⭐ AGREGAR ESTA FUNCIÓN AQUÍ BAJO
  // =====================================================

  public getBankBalance$(userId: number, bankId: number): Observable<UserBankBalance> {
    return this.http.get<UserBankBalance>(`${this.OPS_URL}/balance/${userId}/${bankId}`);
  }
  

}
