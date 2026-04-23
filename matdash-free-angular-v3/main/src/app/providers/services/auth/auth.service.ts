import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { END_POINTS } from '../../utils/end-points';
import { TokenModels } from 'src/app/core/models/token-models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'auth_token';

  constructor(private httpClient: HttpClient) {}

  // ============================
  //       TOKEN HANDLING
  // ============================

  setToken(token: string | undefined): void {
    if (token) {
      localStorage.setItem(this.TOKEN_KEY, token);
    } else {
      localStorage.removeItem(this.TOKEN_KEY);
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    return !this.isTokenExpired();
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (!payload.exp) return true;
      return Date.now() > payload.exp * 1000;
    } catch (e) {
      console.error('Error verificando token:', e);
      return true;
    }
  }

  // ============================
  //     🔥 OBTENER USER ID 🔥
  // ============================

  getUserId(): number {
    const token = this.getToken();
    if (!token) return 0;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.userId || 0;  // ← CAMPO REAL DE TU TOKEN
    } catch (e) {
      console.error('Error obteniendo userId:', e);
      return 0;
    }
  }

  // ============================
  //       AUTH OPERATIONS
  // ============================

  login(credentials: any) {
    return this.httpClient.post<TokenModels>(END_POINTS.login, credentials);
  }

  register(payload: { email: string; password: string }) {
    const requestBody = {
      userName: payload.email,
      password: payload.password
    };

    return this.httpClient.post<any>(END_POINTS.register, requestBody);
  }

}