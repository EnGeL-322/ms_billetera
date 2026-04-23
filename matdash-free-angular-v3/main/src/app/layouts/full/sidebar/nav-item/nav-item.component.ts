import {
  Component,
  HostBinding,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
} from '@angular/core';

import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { TablerIconsModule } from 'angular-tabler-icons';
import { MaterialModule } from 'src/app/material.module';

import { NavItem } from './nav-item';
import { NavService } from 'src/app/core/services/nav.service';

@Component({
  selector: 'app-nav-item',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    TablerIconsModule,
    MaterialModule
  ],
  templateUrl: './nav-item.component.html',
  styleUrls: []
})
export class AppNavItemComponent implements OnChanges {

  /** Recibe un item del menú */
  @Input() item!: NavItem;

  /** Nivel del menú (submenus = depth + 1) */
  @Input() depth: number = 0;

  /** Notifica al sidenav cuando debe cerrarse en mobile */
  @Output() notify = new EventEmitter<boolean>();

  /** Estado expandido */
  expanded: boolean = false;

  /** Necesario para accesibilidad */
  @HostBinding('attr.aria-expanded') ariaExpanded = this.expanded;

  constructor(
    private navService: NavService,
    public router: Router
  ) {}

  // Detecta cambios de ruta y expande el menú correspondiente
  ngOnChanges(changes: SimpleChanges): void {
    if (!this.item?.route) return;

    const currentUrl = this.navService.currentUrl();

    if (!currentUrl) return;

    // Chequea si la ruta actual corresponde al item
    this.expanded = currentUrl.startsWith(`/${this.item.route}`);
    this.ariaExpanded = this.expanded;
  }

  /**
   * Evento cuando el usuario selecciona un item
   */
  onItemSelected(item: NavItem): void {
    // Si no tiene hijos => navegar
    if (!item.children || item.children.length === 0) {

      if (item.route) {
        this.router.navigate([item.route]);
      }

      this.closeMenuIfMobile();
      return;
    }

    // Si tiene hijos => expandir/colapsar
    this.expanded = !this.expanded;
    this.ariaExpanded = this.expanded;

    this.scrollToTop();
  }

  /**
   * Abre un link externo
   */
  openExternalLink(url?: string): void {
    if (url) {
      window.open(url, '_blank');
    }
  }

  /**
   * Si está en móvil => cerrar sidenav al seleccionar item
   */
  private closeMenuIfMobile(): void {
    if (window.innerWidth < 1024) {
      this.notify.emit(true);
    }
  }

  /**
   * Scroll suave al seleccionar opción
   */
  private scrollToTop(): void {
    window.scroll({
      top: 0,
      left: 0,
      behavior: 'smooth'
    });
  }
}
