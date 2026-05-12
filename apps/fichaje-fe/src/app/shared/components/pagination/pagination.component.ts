import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pagination } from './model/pagination.model';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class PaginationComponent {
  @Input() pag: Pagination | null = null;
  @Output() paginacion = new EventEmitter<Pagination>();

  rewind(): void {
    if (this.pag && !this.pag.isFirst) {
      this.pag.page--;
      this.paginacion.emit(this.pag);
    }
  }

  forward(): void {
    if (this.pag && !this.pag.isLast) {
      this.pag.page++;
      this.paginacion.emit(this.pag);
    }
  }

  setPage(page: number): void {
    if (this.pag) {
      this.pag.page = page;
      this.paginacion.emit(this.pag);
    }
  }

  setSize(size: number): void {
    if (this.pag) {
      this.pag.size = Math.min(size, this.pag.sizeLimit);
      this.setPage(0);
    }
  }
}
