import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Empleado } from 'src/app/intranet/empleados/model/empleado';
import { Fichaje } from '../../model/fichaje';
import { FichajeService } from '../../service/fichaje.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';

import { Popup } from 'src/app/shared/helper/popup';

@Component({
  selector: 'app-fichajes-detalle',
  templateUrl: './fichajes-detalle.component.html',
  styleUrls: ['./fichajes-detalle.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule]
})
export class FichajesDetalleComponent implements OnInit {
  private readonly service = inject(FichajeService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  model = signal<Fichaje>(new Fichaje('', '', '', null, new Empleado('', '', '', '', null, null, null, null, null, '')));

  ngOnInit(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.detail(id).subscribe({
      next: data => {
        console.log(data);
        this.model.set(data);
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onUpdate(): void {
    const id = this.activatedRoute.snapshot.params.id;
    this.service.update(id, this.model()).subscribe({
      next: () => {
        Popup.toastSucess('', 'Cambios Guardados');
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onDelete(): void {
    const id = this.activatedRoute.snapshot.params.id;
    Popup.dangerConfirmBox('¿Desea eliminar el fichaje?', 'Esta operación no se puede deshacer', 'SI', 'NO', () => {
      this.delete(id);
    });
  }

  delete(id: number) {
    this.service.delete(id).subscribe({
      next: () => {
        Popup.toastWarning('', 'Fichaje Eliminado');
        this.router.navigate(['intranet/fichajes']);
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  updateModel(field: keyof Fichaje, value: any): void {
    this.model.update(m => ({ ...m, [field]: value }));
  }
}
