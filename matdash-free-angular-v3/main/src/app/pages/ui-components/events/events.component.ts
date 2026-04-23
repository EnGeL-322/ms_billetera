import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogRef, MatDialogModule } from '@angular/material/dialog';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';

import { FormsModule } from '@angular/forms';
import { jwtDecode } from 'jwt-decode';
import { MatCardModule } from '@angular/material/card';

import { Event } from 'src/app/providers/models/event.model';
import { EventService } from 'src/app/providers/services/events/events.service';
import { AuthService } from 'src/app/providers/services/auth/auth.service';
import { WalletService } from 'src/app/providers/services/wallet/wallet.service';
import { TransactionService } from 'src/app/providers/services/transaction/transaction.service';
import { TransactionType } from '../../../providers/services/transaction/transaction.service';

@Component({
  selector: 'app-events',
  standalone: true,
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css'],
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule
  ]
})
export class EventsComponent implements OnInit {

  // 🔥 FIX AGREGADO: static: false + TemplateRef
  @ViewChild('dialogTemplate', { static: false }) dialogTemplate!: TemplateRef<any>;

  dialogRef!: MatDialogRef<any>;

  events: Event[] = [];
  editingId: number | null = null;

  userId!: number;

  form = {
    name: '',
    description: '',
    budget: 0,
    startDate: '',
    endDate: ''
  };

  displayedColumns = ['name', 'budget', 'spent', 'startDate', 'endDate', 'actions'];

  constructor(
    private eventService: EventService,
    private walletService: WalletService,
    private txService: TransactionService,
    private dialog: MatDialog,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserId();
    this.loadEvents();
  }

  // ============================================================
  // 🔥 Obtener ID del usuario del token
  // ============================================================
  loadUserId(): void {
    const token = this.authService.getToken();
    if (!token) return;

    const decoded: any = jwtDecode(token);
    this.userId = Number(decoded.id || decoded.sub);

    console.log("USER ID DESDE TOKEN:", this.userId);
  }

  // ============================================================
  // 🔹 Cargar eventos
  // ============================================================
  loadEvents(): void {
    if (!this.userId) return;

    this.eventService.getEventsByUser$(this.userId).subscribe({
      next: (list) => (this.events = list)
    });
  }

  // 🟣 Abrir para CREAR
  openDialog(): void {

    // 🔥 FIX: validar que el template ya existe
    if (!this.dialogTemplate) {
      console.error("dialogTemplate todavía no está listo");
      return;
    }

    this.editingId = null;
    this.form = { name: '', description: '', budget: 0, startDate: '', endDate: '' };

    this.dialogRef = this.dialog.open(this.dialogTemplate);
  }

  // 🟡 Abrir para EDITAR
  openEditDialog(event: Event): void {

    // 🔥 FIX: evitar undefined → evita error ɵcmp
    if (!this.dialogTemplate) {
      console.error("dialogTemplate todavía no está listo");
      return;
    }

    this.editingId = event.id;

    this.form = {
      name: event.name,
      description: event.description,
      budget: event.budget,
      startDate: event.startDate,
      endDate: event.endDate
    };

    this.dialogRef = this.dialog.open(this.dialogTemplate);
  }

  // 🟢 Guardar CREAR o EDITAR
  saveEvent(): void {
    const payload = {
      ...this.form,
      userId: this.userId
    };

    if (this.editingId === null) {
      this.eventService.createEvent$(payload).subscribe({
        next: () => {
          this.loadEvents();
          this.dialogRef.close();
        }
      });

    } else {
      this.eventService.updateEvent$(this.editingId, payload).subscribe({
        next: () => {
          this.loadEvents();
          this.dialogRef.close();
        }
      });
    }
  }

  // ============================================================
  // ➕ AGREGAR GASTO (EVENTO → TRANSACCIÓN → BILLETERA)
  // ============================================================
  addSpent(event: Event): void {
    const amount = prompt('Monto a agregar al gasto del evento:');

    if (!amount) return;
    const num = Number(amount);
    if (isNaN(num)) return alert('Monto inválido');

    this.eventService.updateSpent$(event.id, { eventId: event.id, amount: num })
      .subscribe({
        next: () => {

          this.txService.createTransaction$({
            userId: this.userId,
            categoryId: null,
            subcategoryId: null,
            eventId: event.id,
            type: TransactionType.EXPENSE,
            amount: num,
            description: 'Gasto agregado al evento'
          }).subscribe({
            next: () => console.log("Transacción creada correctamente"),
            error: (err) => console.error("Error creando transacción:", err)
          });

          this.walletService.subtractFromWallet$(this.userId, num)
            .subscribe({
              next: () => {
                this.loadEvents();
                console.log("Gasto actualizado y billetera descontada correctamente");
              },
              error: (err) => {
                console.error("Error actualizando billetera", err);
                alert("El gasto se registró, pero NO se pudo actualizar la billetera.");
              }
            });

        }
      });
  }

  // 🗑️ Eliminar evento
  deleteEvent(id: number): void {
    if (!confirm('¿Eliminar este evento?')) return;

    this.eventService.deleteEvent$(id).subscribe({
      next: () => this.loadEvents()
    });
  }
}
