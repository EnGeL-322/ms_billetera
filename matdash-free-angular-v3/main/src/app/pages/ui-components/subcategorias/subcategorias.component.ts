import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';


import { Subcategory } from 'src/app/providers/models/subcategory.model';
import { CategoryService } from 'src/app/providers/services/category/category.service';

@Component({
  selector: 'app-subcategorias',
  standalone: true,
  templateUrl: './subcategorias.component.html',
  styleUrls: ['./subcategorias.component.css'],
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class AppSubcategoriasComponent implements OnInit {

  categoryId!: number;
  dialogRef!: MatDialogRef<any>;

  displayedColumns = ['id', 'name', 'createdAt', 'actions'];
  subcategories: Subcategory[] = [];

  form = { name: '' };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private categoryService: CategoryService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.categoryId = +params['categoryId'];
      this.loadSubcategories();
    });
  }

  loadSubcategories() {
    this.categoryService.getSubcategories$(this.categoryId).subscribe((res) => {
      this.subcategories = res;
    });
  }

  openCreateDialog(template: any) {
    this.form = { name: '' };
    this.dialogRef = this.dialog.open(template);
  }

  saveSubcategory() {
    this.categoryService.createSubcategory$(this.categoryId, this.form).subscribe(() => {
      this.dialogRef.close();
      this.loadSubcategories();
    });
  }

  openEditDialog(template: any, sub: Subcategory) {
    this.form = { name: sub.name };
    this.dialogRef = this.dialog.open(template);

    this.dialogRef.beforeClosed().subscribe(() => {
      if (!this.form.name) return;
      this.categoryService.updateSubcategory$(this.categoryId, sub.id, this.form)
        .subscribe(() => this.loadSubcategories());
    });
  }

  deleteSubcategory(id: number) {
    this.categoryService.deleteSubcategory$(this.categoryId, id)
      .subscribe(() => this.loadSubcategories());
  }

  goBack() {
    this.router.navigate(['/ui-components/menu']);
  }
}
