import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { WalletService, Wallet } from '../../../providers/services/wallet/wallet.service';

import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";

import { Router } from '@angular/router';
import { BankOperationsService } from 'src/app/providers/services/bank/bank-operations.service';


@Component({
  selector: 'app-wallet-crud',
  templateUrl: './wallet-crud.component.html',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class WalletCrudComponent {

  walletForm: FormGroup;
  wallet?: Wallet;

  banks: any[] = [];
  userIdLogged!: number;

  constructor(
    private fb: FormBuilder,
    private walletService: WalletService,
    private bankOpsService: BankOperationsService,
    private router: Router
  ) {

    this.walletForm = this.fb.group({
      userId: ['', Validators.required],
      currency: ['pen', Validators.required],
    });

    // Obtener usuario logueado
    this.userIdLogged = Number(localStorage.getItem('userId'));

    // Cargar bancos desde la BD
    this.loadBanks();
  }

  // =============================================
  // 🔹 Cargar bancos
  // =============================================
  loadBanks() {
    this.bankOpsService.getBanks$().subscribe({
      next: (res) => {
        console.log("Bancos obtenidos:", res);
        this.banks = res;
      },
      error: (err) => console.error('Error cargando bancos:', err)
    });
  }

  // =============================================
  // 🔹 Navegar al detalle del banco
  // =============================================
  goToBank(bankId: number) {
    this.router.navigate(['/wallet/bank', bankId]);
  }

  // =============================================
  // 🔹 Crear wallet
  // =============================================
  onSubmit(): void {
    if (this.walletForm.invalid) return;

    const walletData = this.walletForm.value;

    this.walletService.createWallet$(walletData).subscribe({
      next: (wallet) => {
        console.log('Wallet creada:', wallet);
        this.wallet = wallet;
        alert('✅ Wallet creada correctamente');
      },
      error: (err) => {
        console.error('Error al crear wallet:', err);
        alert('❌ Error al crear wallet');
      },
    });
  }

  // =============================================
  // 🔹 Eliminar wallet
  // =============================================
  onDelete(): void {
    const userId = this.walletForm.get('userId')?.value;
    if (!userId) return;

    this.walletService.deleteWalletByUserId$(userId).subscribe({
      next: () => {
        console.log('Wallet eliminada');
        alert('🗑️ Wallet eliminada correctamente');
        this.wallet = undefined;
      },
      error: (err) => {
        console.error('Error al eliminar wallet:', err);
        alert('❌ Error al eliminar wallet');
      },
    });
  }
}
