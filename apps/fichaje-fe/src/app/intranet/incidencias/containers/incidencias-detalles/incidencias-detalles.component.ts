import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Empleado } from 'src/app/intranet/empleados/model/empleado';
import { Incidencia } from '../../model/incidencia';
import { IncidenciaService } from '../../service/incidencia.service';
import { Popup } from 'src/app/shared/helper/popup';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-incidencias-detalles',
  templateUrl: './incidencias-detalles.component.html',
  styleUrls: ['./incidencias-detalles.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule, RouterModule]
})
export class IncidenciasDetallesComponent implements OnInit {
  public readonly service = inject(IncidenciaService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  model = signal<Incidencia>(new Incidencia('', '', '', null, new Empleado('', '', '', '', null, null, null, null, null, '')));

  ngOnInit(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.detail(id).subscribe({
      next: data => this.model.set(data),
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onUpdate(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.update(id, this.model()).subscribe({
      next: () => Popup.toastSucess('', 'Cambios Guardados'),
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onDelete(): void {
    const id = this.activatedRoute.snapshot.params.id;
    Popup.dangerConfirmBox('¿Desea eliminar la incidencia?', 'Esta operación no se puede deshacer', 'SI', 'NO', () => {
      this.delete(id);
    });
  }

  private delete(id: number): void {
    this.service.delete(id).subscribe({
      next: () => {
        Popup.toastWarning('', 'Incidencia Eliminada');
        this.router.navigate(['intranet/incidencias']);
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }
}
