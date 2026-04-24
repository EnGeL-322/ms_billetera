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
import { WalletService, Wallet } from 'src/app/providers/services/wallet/wallet.service';
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

  @ViewChild('dialogTemplate', { static: false }) dialogTemplate!: TemplateRef<any>;

  dialogRef!: MatDialogRef<any>;
  events: Event[] = [];
  editingId: number | null = null;
  userId!: number;
  wallet?: Wallet;

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
    this.loadWallet();
    this.loadEvents();
  }

  loadUserId(): void {
    const token = this.authService.getToken();
    if (!token) return;

    const decoded: any = jwtDecode(token);
    this.userId = Number(decoded.id || decoded.sub);
  }

  loadWallet(): void {
    if (!this.userId) return;

    this.walletService.getWalletByUserId$(this.userId).subscribe({
      next: (wallet) => this.wallet = wallet,
      error: () => this.wallet = undefined
    });
  }

  loadEvents(): void {
    if (!this.userId) return;

    this.eventService.getEventsByUser$(this.userId).subscribe({
      next: (list) => (this.events = list)
    });
  }

  openDialog(): void {
    if (!this.dialogTemplate) {
      console.error("dialogTemplate todavía no está listo");
      return;
    }

    this.editingId = null;
    this.form = { name: '', description: '', budget: 0, startDate: '', endDate: '' };
    this.dialogRef = this.dialog.open(this.dialogTemplate);
  }

  openEditDialog(event: Event): void {
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

  addSpent(event: Event): void {
    const amount = prompt('Monto a agregar al gasto del evento:');
    if (!amount) return;

    const num = Number(amount);

    if (isNaN(num) || num <= 0) {
      alert('Monto inválido');
      return;
    }

    if (!this.wallet) {
      alert('No se pudo obtener la billetera');
      return;
    }

    if (num > this.wallet.balance) {
      alert('❌ No tienes saldo suficiente en la billetera');
      return;
    }

    // ✅ SOLO CREAR TRANSACCIÓN: ella misma ya descuenta la wallet
    this.txService.createTransaction$({
      userId: this.userId,
      categoryId: null,
      subcategoryId: null,
      eventId: event.id,
      type: TransactionType.EXPENSE,
      amount: num,
      description: 'Gasto agregado al evento'
    }).subscribe({
      next: () => {
        // ✅ recién después actualizas el evento
        this.eventService.updateSpent$(event.id, { eventId: event.id, amount: num })
          .subscribe({
            next: () => {
              this.loadWallet();
              this.loadEvents();
              alert('✅ Gasto registrado correctamente');
            },
            error: (err) => {
              console.error('Error actualizando spent del evento:', err);
              this.loadWallet();
              alert('⚠ La transacción se registró, pero el evento no se actualizó.');
            }
          });
      },
      error: (err) => {
        console.error('Error creando transacción:', err);
        const msg = err?.error?.message || err?.error || '❌ No se pudo registrar el gasto';
        alert(msg);
      }
    });
  }

  deleteEvent(id: number): void {
    if (!confirm('¿Eliminar este evento?')) return;

    this.eventService.deleteEvent$(id).subscribe({
      next: () => this.loadEvents()
    });
  }
}