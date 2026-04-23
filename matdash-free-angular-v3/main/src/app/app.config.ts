import {
  ApplicationConfig,
  provideZoneChangeDetection,
  importProvidersFrom,
} from '@angular/core';
import {
  provideHttpClient, withInterceptors,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { routes } from './app.routes';
import {
  provideRouter,
  withComponentInputBinding,
  withInMemoryScrolling,
} from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideClientHydration } from '@angular/platform-browser';

// icons
import { TablerIconsModule } from 'angular-tabler-icons';
import * as TablerIcons from 'angular-tabler-icons/icons';

// perfect scrollbar
import { NgScrollbarModule } from 'ngx-scrollbar';

//Import all material modules
import { MaterialModule } from './material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {urlInterceptor} from "./core/interceptors/url.interceptor";
import {tokenInterceptor} from "./core/interceptors/token.interceptor";
import {errorInterceptor} from "./core/interceptors/error.interceptor";

// ⭐ NECESARIO PARA EL DATEPICKER
import { provideNativeDateAdapter } from '@angular/material/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes,
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled',
      }),
      withComponentInputBinding()
    ),
    provideHttpClient(
      withInterceptorsFromDi(),
      withInterceptors([
        urlInterceptor,     // 1. Modifica la URL
        tokenInterceptor,   // 2. Agrega el token JWT
        errorInterceptor    // 3. Maneja errores globales
      ])
    ),
    provideClientHydration(),
    provideAnimationsAsync(),

    // ⭐ AGREGAR ESTO SOLUCIONA TU ERROR
    provideNativeDateAdapter(),

    importProvidersFrom(
      FormsModule,
      ReactiveFormsModule,
      MaterialModule,
      TablerIconsModule.pick(TablerIcons),
      NgScrollbarModule,
    ),
  ],
};