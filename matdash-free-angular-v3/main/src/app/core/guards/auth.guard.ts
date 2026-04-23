import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from 'src/app/providers/services/auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    const token = this.authService.getToken();

    // ❌ si NO hay token → bloquea y envía al login
    if (!token) {
      this.router.navigate(['/auth/login']);
      return false;
    }

    // ✔️ si hay token → deja pasar
    return true;
  }
}
