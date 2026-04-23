import { Component } from '@angular/core';
import { CoreService } from 'src/app/core/services/core.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialModule } from 'src/app/material.module';
import { AuthService } from 'src/app/providers/services/auth/auth.service';

@Component({
  selector: 'app-side-register',
  imports: [RouterModule, MaterialModule, FormsModule, ReactiveFormsModule],
  templateUrl: './side-register.component.html',
  styleUrls: ['./side-register.component.css'],
  
})
export class AppSideRegisterComponent {
  options = this.settings.getOptions();

  loading = false;
  errorMessage = '';

  constructor(
    private settings: CoreService,
    private router: Router,
    private authService: AuthService
  ) {}

  form = new FormGroup({
    uname: new FormControl('', [
      Validators.required,
      Validators.minLength(3),
    ]),
    email: new FormControl('', [
      Validators.required,
      Validators.email,
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(6),
    ]),
  });

  get f() {
    return this.form.controls;
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const value = this.form.value;

    const payload = {
      email: value.email ?? '',
      password: value.password ?? '',
    };


    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        alert('✅ Cuenta creada correctamente. Ahora puedes iniciar sesión.');
        this.router.navigate(['/authentication/login']);
      },
      error: (err) => {
        console.error('Error en registro:', err);
        this.loading = false;
        this.errorMessage = '❌ No se pudo crear la cuenta. Intenta de nuevo.';
      }
    });
  }
}
