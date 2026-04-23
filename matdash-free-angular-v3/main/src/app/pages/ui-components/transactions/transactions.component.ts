import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TransactionService, TransactionResponse } from '../../../providers/services/transaction/transaction.service';
import { AuthService } from '../../../providers/services/auth/auth.service';
import { jwtDecode } from 'jwt-decode';
import { TransactionDialogComponent } from './transaction-dialog.component';

@Component({
  selector: 'app-transactions',
  standalone: true,
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css'],
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatDialogModule,
    MatIconModule
  ]
})
export class TransactionsComponent implements OnInit {
  transactions: TransactionResponse[] = [];
  userId: number | null = null;

  displayedColumns = [
    'transactionDate',
    'type',
    'category',
    'subcategory',
    'amount',
    'event',
    'description'
  ];

  constructor(
    private txService: TransactionService,
    private authService: AuthService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadUserId();
    this.loadTransactions();
  }

  private loadUserId(): void {
    const token = this.authService.getToken();
    if (!token) return;

    const decoded: any = jwtDecode(token);
    this.userId = Number(decoded.id || decoded.sub);
  }

  private loadTransactions(): void {
    if (!this.userId) return;

    this.txService.getUserTransactions$(this.userId).subscribe({
      next: (txs) => {
        this.transactions = txs;
        console.log('Transacciones:', txs);
      },
      error: (err) => {
        console.error('Error al cargar transacciones:', err);
        this.transactions = [];
      }
    });
  }

  openNewTransactionDialog(): void {
    if (!this.userId) {
      alert('No se encontró el usuario autenticado.');
      return;
    }

    const dialogRef = this.dialog.open(TransactionDialogComponent, {
      width: '450px',
      data: { userId: this.userId }
    });

    dialogRef.afterClosed().subscribe((created) => {
      if (created) {
        this.loadTransactions();  // recarga la tabla
      }
    });
  }
}
