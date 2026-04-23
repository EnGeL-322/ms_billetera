import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/providers/services/auth/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  @Input() showToggle: boolean = true;

  @Output() toggleCollapsed = new EventEmitter<void>();
  @Output() toggleMobileNav = new EventEmitter<void>();

  userName: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    console.log("TOKEN:", token);

    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        console.log("TOKEN DECODIFICADO:", decoded);

        // ⭐⭐ AQUÍ LA SOLUCIÓN ⭐⭐
        this.userName = decoded.sub || '';

      } catch (e) {
        console.error("Error decodificando token:", e);
      }
    }
  }

  onToggleCollapsed() {
    this.toggleCollapsed.emit();
  }

  onToggleMobileNav() {
    this.toggleMobileNav.emit();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }
}
