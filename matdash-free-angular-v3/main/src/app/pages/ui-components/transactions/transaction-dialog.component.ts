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
  goals: any[] = [];  // 👈 metas cargadas

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { userId: number },
    private fb: FormBuilder,
    private txService: TransactionService,
    private categoryService: CategoryService,
    private goalsService: GoalsService,
    private dialogRef: MatDialogRef<TransactionDialogComponent>
  ) {
    this.txForm = this.fb.group({
      type: ['EXPENSE' as TransactionType, Validators.required],
      categoryId: [null, Validators.required],
      subcategoryId: [null, Validators.required],
      goalId: [null],   // 👈 nuevo
      eventId: [null],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required],
    });
  }

  ngOnInit(): void {

    // cargar categorías del usuario
    this.categoryService.getCategoriesByUser$(this.data.userId).subscribe(res => {
      this.categories = res;
    });

    // cargar metas del usuario
    this.goalsService.getGoalsByUser$(this.data.userId).subscribe(res => {
      this.goals = res;
    });

    // subcategorías al cambiar categoría
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

    const payload = {
      userId: this.data.userId,
      categoryId: Number(f.categoryId),
      subcategoryId: Number(f.subcategoryId),
      goalId: f.goalId ? Number(f.goalId) : null,     // 👈 enviar goalId
      eventId: f.eventId ? Number(f.eventId) : null,
      type: f.type,
      amount: Number(f.amount),
      description: f.description
    };

    this.txService.createTransaction$(payload).subscribe({
      next: () => {
        alert('✅ Transacción registrada correctamente');
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Error al crear transacción:', err);
        alert('❌ Error al crear transacción');
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
