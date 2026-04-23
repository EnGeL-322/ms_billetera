import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = '';

      if (error.error instanceof ErrorEvent) {
        // Error de frontend
        errorMessage = `Error del cliente: ${error.error.message}`;
      } else {
        // Error devuelto por el backend
        switch (error.status) {
          case 401:
            errorMessage = 'No autorizado. Inicia sesión nuevamente.';
            router.navigate(['/authentication/login']);
            break;

          case 403:
            errorMessage = 'Acceso prohibido.';
            break;

          case 404:
            errorMessage = 'Recurso no encontrado.';
            break;

          case 500:
            errorMessage = 'Error interno del servidor.';
            break;

          default:
            errorMessage = `Error ${error.status}: ${error.message}`;
        }
      }

      console.error('Interceptado:', errorMessage);

      return throwError(() => error);
    })
  );
};
