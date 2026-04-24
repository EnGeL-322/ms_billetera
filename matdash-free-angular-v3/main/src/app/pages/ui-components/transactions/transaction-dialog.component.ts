import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { TransactionService, TransactionType } from '../../../providers/services/transaction/transaction.service';
import { CategoryService } from 'src/app/providers/services/category/category.service';
import { GoalsService } from 'src/app/providers/services/goals/GoalsService';
import { WalletService, Wallet } from 'src/app/providers/services/wallet/wallet.service';

@Component({
  selector: 'app-transaction-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './transaction-dialog.component.html',
  styleUrls: ['./transaction-dialog.component.css'],
})
export class TransactionDialogComponent implements OnInit {

  txForm: FormGroup;

  categories: any[] = [];
  subcategories: any[] = [];
  goals: any[] = [];
  wallet?: Wallet;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { userId: number },
    private fb: FormBuilder,
    private txService: TransactionService,
    private categoryService: CategoryService,
    private goalsService: GoalsService,
    private walletService: WalletService,
    private dialogRef: MatDialogRef<TransactionDialogComponent>
  ) {
    this.txForm = this.fb.group({
      type: ['EXPENSE' as TransactionType, Validators.required],
      categoryId: [null, Validators.required],
      subcategoryId: [null, Validators.required],
      goalId: [null],
      eventId: [null],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.categoryService.getCategoriesByUser$(this.data.userId).subscribe(res => {
      this.categories = res;
    });

    this.goalsService.getGoalsByUser$(this.data.userId).subscribe(res => {
      this.goals = res;
    });

    this.walletService.getWalletByUserId$(this.data.userId).subscribe({
      next: (wallet) => this.wallet = wallet,
      error: () => this.wallet = undefined
    });

    this.txForm.get('categoryId')?.valueChanges.subscribe(categoryId => {
      if (categoryId) {
        this.categoryService.getSubcategories$(categoryId).subscribe(res => {
          this.subcategories = res;
        });
      } else {
        this.subcategories = [];
      }
    });
  }

  onSubmit(): void {
    if (this.txForm.invalid) return;

    const f = this.txForm.value;
    const amount = Number(f.amount);

    if (amount <= 0 || isNaN(amount)) {
      alert('❌ El monto debe ser mayor a 0');
      return;
    }

    if (f.type === TransactionType.EXPENSE && this.wallet && amount > this.wallet.balance) {
      alert('❌ No tienes saldo suficiente en la billetera');
      return;
    }

    const payload = {
      userId: this.data.userId,
      categoryId: Number(f.categoryId),
      subcategoryId: Number(f.subcategoryId),
      goalId: f.goalId ? Number(f.goalId) : null,
      eventId: f.eventId ? Number(f.eventId) : null,
      type: f.type,
      amount,
      description: f.description
    };

    this.txService.createTransaction$(payload).subscribe({
      next: () => {
        alert('✅ Transacción registrada correctamente');
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Error al crear transacción:', err);
        const msg = err?.error?.message || err?.error || '❌ Error al crear transacción';
        alert(msg);
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}