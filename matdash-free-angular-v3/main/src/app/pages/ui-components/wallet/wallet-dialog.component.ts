import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog'; // 👈 IMPORTANTE
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

import { WalletService } from '../../../providers/services/wallet/wallet.service';

@Component({
  selector: 'app-wallet-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule, 
    MatIconModule
// 👈 AGREGA ESTE MÓDULO
  ],
  template: `
    <h2 mat-dialog-title>Crear Nueva Billetera</h2>

    <mat-dialog-content>
      <form [formGroup]="walletForm">
        <mat-form-field appearance="outline" class="w-100">
          <mat-label>ID Usuario</mat-label>
          <input matInput formControlName="userId" readonly />
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Moneda</mat-label>
          <input matInput formControlName="currency" />
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button
        mat-raised-button
        color="primary"
        (click)="onSubmit()"
        [disabled]="walletForm.invalid"
      >
        Crear
      </button>
    </mat-dialog-actions>
  `,
})
export class WalletDialogComponent {
  walletForm: FormGroup;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private walletService: WalletService,
    private dialogRef: MatDialogRef<WalletDialogComponent>
  ) {
    this.walletForm = this.fb.group({
      userId: [data.userId, Validators.required],
      currency: ['pen', Validators.required],
    });
  }

  onSubmit(): void {
    const walletData = this.walletForm.getRawValue();
    this.walletService.createWallet$(walletData).subscribe({
      next: () => {
        alert('✅ Billetera creada correctamente');
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error(err);
        alert('❌ Error al crear billetera');
      },
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
