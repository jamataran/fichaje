import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Popup } from 'src/app/shared/helper/popup';
import { Sede, SedeCreate, SedeUpdate } from '../../model/sede.model';
import { SedesService } from '../../service/sedes.service';
import { EmpresasService } from '../../../empresas/service/empresas.service';
import { TokenService } from 'src/app/core/auth/service/token.service';

@Component({
  selector: 'app-sedes',
  templateUrl: './sedes.component.html',
  styleUrls: ['./sedes.component.css'],
  standalone: false
})
export class SedesComponent implements OnInit {

  listaSedes: Sede[] = [];
  empresaId: number | null = null;
  nombreEmpresa: string = '';

  isCreating = signal(false);
  isUpdating = signal(false);
  isDeactivating = signal(false);
  editingSedeId = signal<number | null>(null);

  // Formularios Reactivos
  sedeForm: FormGroup;
  editSedeForm: FormGroup;

  constructor(
    private sedesService: SedesService,
    private empresasService: EmpresasService,
    private tokenService: TokenService,
    private fb: FormBuilder
  ) {
    this.sedeForm = this.initSedeForm();
    this.editSedeForm = this.initSedeForm();
  }

  ngOnInit(): void {
    this.empresaId = this.tokenService.getEmpresaId();
    this.cargarDatos();
  }

  private initSedeForm(): FormGroup {
    return this.fb.group({
      nombre: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefono: [''],
      direccion: ['', Validators.required],
      codigoPostal: ['', Validators.required],
      localidad: ['', Validators.required],
      provincia: ['', Validators.required],
      pais: ['España', Validators.required],
      latitud: [null],
      longitud: [null],
      activa: [true]
    });
  }

  cargarDatos(): void {
    // Cargar mi empresa para tener el nombre
    this.empresasService.getMiEmpresa().subscribe({
      next: (empresa) => {
        this.nombreEmpresa = empresa.nombre;
        if (!this.empresaId) this.empresaId = empresa.id;
      },
      error: () => {
        Popup.toastDanger('Error', 'No se pudo cargar la información de la empresa');
      }
    });

    this.listarSedes();
  }

  listarSedes(): void {
    this.sedesService.getMisSedes().subscribe({
      next: (sedes) => {
        this.listaSedes = sedes ?? [];
      },
      error: (err) => {
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudieron cargar las sedes');
      }
    });
  }

  onCreate(): void {
    if (this.sedeForm.invalid || !this.empresaId) {
      return;
    }

    const payload: SedeCreate = this.sedeForm.value;
    this.isCreating.set(true);

    this.sedesService.create(this.empresaId, payload).subscribe({
      next: () => {
        this.isCreating.set(false);
        Popup.toastSucess('', 'Sede creada correctamente');
        this.sedeForm.reset({ activa: true, pais: 'España' });
        this.listarSedes();
      },
      error: (err) => {
        this.isCreating.set(false);
        const msg = err?.error?.mensaje || err?.error?.message || 'No se pudo crear la sede';
        Popup.toastDanger('Error', msg);
      }
    });
  }

  onStartEdit(sede: Sede): void {
    this.editingSedeId.set(sede.id);
    this.editSedeForm.patchValue({
      nombre: sede.nombre,
      email: sede.email,
      telefono: sede.telefono ?? '',
      direccion: sede.direccion,
      codigoPostal: sede.codigoPostal,
      localidad: sede.localidad,
      provincia: sede.provincia,
      pais: sede.pais,
      latitud: sede.latitud,
      longitud: sede.longitud,
      activa: sede.activa
    });
  }

  onCancelEdit(): void {
    this.editingSedeId.set(null);
    this.editSedeForm.reset();
  }

  onUpdate(): void {
    const sedeId = this.editingSedeId();
    if (!sedeId || this.editSedeForm.invalid) {
      return;
    }

    const payload: SedeUpdate = this.editSedeForm.value;
    this.isUpdating.set(true);

    this.sedesService.update(sedeId, payload).subscribe({
      next: () => {
        this.isUpdating.set(false);
        Popup.toastSucess('', 'Sede actualizada correctamente');
        this.onCancelEdit();
        this.listarSedes();
      },
      error: (err) => {
        this.isUpdating.set(false);
        const msg = err?.error?.mensaje || err?.error?.message || 'No se pudo actualizar la sede';
        Popup.toastDanger('Error', msg);
      }
    });
  }

  onDeactivate(sede: Sede): void {
    Popup.dangerConfirmBox(
      `¿Desea desactivar la sede ${sede.nombre}?`,
      'Esta operación no se puede deshacer',
      'SI',
      'NO',
      () => this.deactivateSede(sede.id)
    );
  }

  private deactivateSede(id: number): void {
    this.isDeactivating.set(true);
    this.sedesService.deactivate(id).subscribe({
      next: () => {
        this.isDeactivating.set(false);
        Popup.toastWarning('', 'Sede desactivada');
        this.listarSedes();
      },
      error: (err) => {
        this.isDeactivating.set(false);
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudo desactivar la sede');
      }
    });
  }

  isEditing(sedeId: number): boolean {
    return this.editingSedeId() === sedeId;
  }
}
