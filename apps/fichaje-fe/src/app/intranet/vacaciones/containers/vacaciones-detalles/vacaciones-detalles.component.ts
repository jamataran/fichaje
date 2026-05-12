import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Vacaciones } from '../../models/vacaciones';
import { VacacionesService } from '../../service/vacaciones.service';
import { Popup } from 'src/app/shared/helper/popup';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-vacaciones-detalles',
  templateUrl: './vacaciones-detalles.component.html',
  styleUrls: ['./vacaciones-detalles.component.css'],
  standalone: true,
  imports: [CommonModule, SharedModule]
})
export class VacacionesDetallesComponent implements OnInit {
  private readonly service = inject(VacacionesService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  model = signal<Vacaciones | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.detail(id).subscribe({
      next: data => {
        this.model.set(data);
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onDelete(): void {
    const id = this.activatedRoute.snapshot.params.id;
    Popup.dangerConfirmBox('¿Desea eliminar las vacaciones?', 'Esta operación no se puede deshacer', 'SI', 'NO', () => {
      this.delete(id);
    });
  }

  delete(id: number) {
    this.service.delete(id).subscribe({
      next: () => {
        Popup.toastWarning('', 'Vacaciones Eliminadas');
        this.router.navigate(['intranet/vacaciones']);
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onApprove(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.aprobar(id).subscribe({
      next: () => {
        Popup.toastSucess('', 'Vacaciones Aprobadas');
        this.load();
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onDeny(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.denegar(id).subscribe({
      next: () => {
        Popup.toastDanger('', 'Vacaciones Denegadas');
        this.load();
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }
}
