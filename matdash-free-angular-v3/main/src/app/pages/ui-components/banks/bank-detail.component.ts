import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

import { Bank } from 'src/app/providers/models/bank.model';
import { UserBankIncome } from 'src/app/providers/models/user-bank-income.model';
import { UserBankBalance } from 'src/app/providers/models/user-bank-balance.model';
import { BankOperationsService } from 'src/app/providers/services/bank/bank-operations.service';

import { AuthService } from 'src/app/providers/services/auth/auth.service';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-bank-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule
  ],
  templateUrl: './bank-detail.component.html',
  styleUrls: ['./bank-detail.component.css']
})
export class BankDetailComponent implements OnInit {

  bankId!: number;
  userId!: number;

  bank?: Bank;
  incomes: UserBankIncome[] = [];
  bankBalance: number = 0;

  showAddForm = false;
  showTransferForm = false;

  amount: number = 0;
  description: string = '';
  transferAmount: number = 0;

  constructor(
    private route: ActivatedRoute,
    private bankOps: BankOperationsService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserId();                // 👈 IGUALITO QUE CATEGORIES
    this.bankId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadBankData();
  }

  // =====================================
  //   🔥 Igualito al método de categorías
  // =====================================
  loadUserId(): void {
    const token = this.authService.getToken();
    if (!token) return;

    const decoded: any = jwtDecode(token);
    this.userId = Number(decoded.id || decoded.sub);  // 👈 MISMA LÓGICA QUE CATEGORIES
  }

  loadBankData(): void {

    this.bankOps.getBankById$(this.bankId).subscribe({
      next: (bank) => this.bank = bank
    });

    // ➤ Historial del usuario REAL
    this.bankOps.getIncomeHistory$(this.userId, this.bankId).subscribe({
      next: (data) => this.incomes = data
    });

    // ➤ Saldo filtrado POR USUARIO
    this.bankOps.getBankBalance$(this.userId, this.bankId).subscribe({
      next: (data: UserBankBalance) => {
        this.bankBalance = Number(data.balance);
      }
    });
  }

  toggleAddForm() {
    this.showAddForm = !this.showAddForm;
    this.showTransferForm = false;
  }

  toggleTransferForm() {
    this.showTransferForm = !this.showTransferForm;
    this.showAddForm = false;
  }

  // ➤ Guardar ingreso
  submitIncome() {
    if (!this.amount || this.amount <= 0) return alert("Monto inválido");
    if (!this.description.trim()) return alert("Descripción requerida");

    const payload = {
      userId: this.userId,     // 👈 Usuario filtrado igual que en categorías
      bankId: this.bankId,
      amount: this.amount,
      description: this.description
    };

    this.bankOps.addIncomeToBank$(payload).subscribe({
      next: () => {
        this.loadBankData();
        this.showAddForm = false;
        this.amount = 0;
        this.description = '';
      }
    });
  }

  // ➤ Transferir a Wallet
  submitTransfer() {
    if (!this.transferAmount || this.transferAmount <= 0) {
      alert("Monto inválido");
      return;
    }

    const payload = {
      userId: this.userId,     // 👈 Igual que CATEGORIES
      bankId: this.bankId,
      amount: this.transferAmount
    };

    this.bankOps.transferToWallet$(payload).subscribe({
      next: () => {
        this.loadBankData();
        this.showTransferForm = false;
        this.transferAmount = 0;
      }
    });
  }
  // ===============================
//  🔥 LOGO ESTÁTICO DE LOS BANCOS
// ===============================
  getBankLogo(name: string | undefined): string {
    if (!name) return 'assets/banks/default.png';

    const key = name.toLowerCase();

    const logos: any = {
      'yape': 'assets/banks/yape.png',
      'bbva': 'assets/banks/bbva.png',
      'bcp': 'assets/banks/bcp.png',
      'plin': 'assets/banks/plin.png'
    };

    return logos[key] || 'assets/banks/default.png';
  }

}
