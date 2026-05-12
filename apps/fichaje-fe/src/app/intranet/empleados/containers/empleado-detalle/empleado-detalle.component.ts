import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Empleado } from '../../model/empleado';
import { EmpleadosService } from '../../service/empleados.service';
import { Popup } from 'src/app/shared/helper/popup';

@Component({
    selector: 'app-empleado-detalle',
    templateUrl: './empleado-detalle.component.html',
    styleUrls: ['./empleado-detalle.component.css'],
    standalone: false
})
export class EmpleadoDetalleComponent implements OnInit {
  private service = inject(EmpleadosService);
  private activatedRoute = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  model = signal<Empleado | null>(null);
  editForm!: FormGroup;
  isLoading = signal(true);

  ngOnInit(): void {
    this.initForm();
    const id = this.activatedRoute.snapshot.params.id;
    this.loadEmpleado(id);
  }

  private initForm(): void {
    this.editForm = this.fb.group({
      nombreEmpleado: ['', Validators.required],
      numero: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      dni: ['', Validators.required],
      diasVacaciones: [0],
      horasGeneradas: [0],
      enVacaciones: [false],
      deBaja: [false],
      working: [false]
    });
  }

  private loadEmpleado(id: number): void {
    this.isLoading.set(true);
    this.service.detail(id).subscribe({
      next: (data) => {
        this.model.set(data);
        this.editForm.patchValue(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onUpdate(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    const id = this.activatedRoute.snapshot.params.id;
    const updatedEmpleado = { ...this.model(), ...this.editForm.value };

    this.service.update(id, updatedEmpleado).subscribe({
      next: () => {
        Popup.toastSucess('', 'Cambios Guardados');
      },
      error: (err) => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  onDelete(): void {
    const id = this.activatedRoute.snapshot.params.id;
    Popup.dangerConfirmBox('¿Desea eliminar el usuario?', 'Esta operación no se puede deshacer', 'SI', 'NO', () => {
      this.delete(id);
    });
  }

  private delete(id: number) {
    this.service.delete(id).subscribe({
      next: () => {
        Popup.toastWarning('', 'Usuario Eliminado');
        this.router.navigate(['intranet/empleados']);
      },
      error: (err) => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }
}
