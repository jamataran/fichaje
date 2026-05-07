import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Permiso } from '../../model/permiso';
import { PermisoService } from '../../service/permiso.service';
import { Popup } from 'src/app/shared/helper/popup';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-permisos-detalles',
  templateUrl: './permisos-detalles.component.html',
  styleUrls: ['./permisos-detalles.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule, RouterLink]
})
export class PermisosDetallesComponent implements OnInit {
  private readonly service = inject(PermisoService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly model = signal<Permiso | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const id = this.activatedRoute.snapshot.params.id;
    if (!id) return;
    
    this.service.detail(id).subscribe({
      next: data => this.model.set(data),
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error('Error loading permiso details:', err);
      }
    });
  }

  onDelete(): void {
    const id = this.activatedRoute.snapshot.params.id;
    Popup.dangerConfirmBox('¿Desea eliminar el permiso?', 'Esta operación no se puede deshacer', 'SI', 'NO', () => {
      this.delete(id);
    });
  }

  delete(id: number) {
    this.service.delete(id).subscribe({
      next: () => {
        Popup.toastWarning('', 'Permiso Eliminado');
        this.router.navigate(['intranet/permisos']);
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error('Error deleting permiso:', err);
      }
    });
  }

  onApprove(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.aprobar(id).subscribe({
      next: () => {
        Popup.toastSucess('', 'Permiso Aprobado');
        this.load();
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error('Error approving permiso:', err);
      }
    });
  }

  onDeny(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.denegar(id).subscribe({
      next: () => {
        Popup.toastDanger('', 'Permiso Denegado');
        this.load();
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error('Error denying permiso:', err);
      }
    });
  }
}
