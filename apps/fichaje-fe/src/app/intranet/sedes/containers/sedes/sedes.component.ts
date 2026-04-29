import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Popup } from 'src/app/shared/helper/popup';
import { Sede, SedeCreate, SedeUpdate } from '../../model/sede.model';
import { SedesService } from '../../service/sedes.service';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { EmpleadosService } from 'src/app/intranet/empleados/service/empleados.service';

@Component({
  selector: 'app-sedes',
  templateUrl: './sedes.component.html',
  styleUrls: ['./sedes.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class SedesComponent implements OnInit {
  private sedesService = inject(SedesService);
  private empleadosService = inject(EmpleadosService);
  private tokenService = inject(TokenService);
  private fb = inject(FormBuilder).nonNullable;

  listaSedes = signal<Sede[]>([]);
  empresaId = signal<number | null>(null);
  nombreEmpresa = signal<string>('');

  isCreating = signal(false);
  isUpdating = signal(false);
  isActivatingSede = signal(false);
  isDeactivatingSede = signal(false);
  editingSedeId = signal<number | null>(null);

  // Formularios Reactivos Tipados con validaciones que coinciden con el backend
  sedeForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'&()-]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    telefono: ['', [Validators.pattern(/^[+]?[0-9\s()]{6,20}$/)]],
    direccion: ['', [Validators.required]],
    codigoPostal: ['', [Validators.required, Validators.pattern(/^[0-9]{5}$/)]],
    localidad: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'()-]+$/)]],
    provincia: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'()-]+$/)]],
    pais: ['España', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'()-]+$/)]],
    latitud: [null as number | null],
    longitud: [null as number | null],
    activa: [true]
  });

  editSedeForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'&()-]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    telefono: ['', [Validators.pattern(/^[+]?[0-9\s()]{6,20}$/)]],
    direccion: ['', [Validators.required]],
    codigoPostal: ['', [Validators.required, Validators.pattern(/^[0-9]{5}$/)]],
    localidad: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'()-]+$/)]],
    provincia: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'()-]+$/)]],
    pais: ['España', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,'()-]+$/)]],
    latitud: [null as number | null],
    longitud: [null as number | null],
    activa: [true]
  });

  ngOnInit(): void {
    this.empresaId.set(this.tokenService.getEmpresaId());
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.empleadosService.getMyUsuario().subscribe({
      next: (usuario) => {
        if (usuario.empresas && usuario.empresas.length > 0) {
          const empresa = usuario.empresas[0];
          this.nombreEmpresa.set(empresa.nombre);
          if (!this.empresaId()) {
            this.empresaId.set(empresa.id);
            this.listarSedes(); // Listar cuando tengamos el ID
          }
        }
      },
      error: () => {
        Popup.toastDanger('Error', 'No se pudo cargar la información de la empresa');
      }
    });

    // Intento inicial con el ID del token si existe
    if (this.empresaId()) {
      this.listarSedes();
    }
  }

  listarSedes(): void {
    const id = this.empresaId();
    if (!id) return;

    this.sedesService.getSedesByEmpresa(id).subscribe({
      next: (sedes) => {
        this.listaSedes.set(sedes ?? []);
      },
      error: (err) => {
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudieron cargar las sedes');
      }
    });
  }

  onCreate(): void {
    const idEmpresa = this.empresaId();
    if (this.sedeForm.invalid || !idEmpresa) {
      this.sedeForm.markAllAsTouched();
      Popup.toastDanger('Error', 'Por favor, revisa los campos del formulario. El CP debe tener 5 dígitos.');
      return;
    }

    const rawValue = this.sedeForm.getRawValue();
    const payload: any = {
      ...rawValue,
      empresaId: idEmpresa,
      latitud: rawValue.latitud || null,
      longitud: rawValue.longitud || null,
      telefono: rawValue.telefono || null
    };

    this.isCreating.set(true);

    this.sedesService.create(idEmpresa, payload).subscribe({
      next: () => {
        this.isCreating.set(false);
        Popup.toastSucess('', 'Sede creada correctamente');
        this.sedeForm.reset({ activa: true, pais: 'España' });
        this.listarSedes();
      },
      error: (err) => {
        this.isCreating.set(false);
        const msg = err?.error?.mensaje || err?.error?.message || 'Error 400: Datos inválidos. Revisa el CP (5 dígitos) y el formato del nombre.';
        Popup.toastDanger('Error', msg);
        console.error('Error 400 details:', err.error);
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
      this.editSedeForm.markAllAsTouched();
      return;
    }

    const payload: SedeUpdate = this.editSedeForm.getRawValue();
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

  onToggleSede(sede: Sede): void {
    if (sede.activa) {
      Popup.dangerConfirmBox(
        `¿Desea desactivar la sede ${sede.nombre}?`,
        'Los empleados no podrán fichar en esta sede temporalmente.',
        'DESACTIVAR',
        'CANCELAR',
        () => this.deactivateSede(sede)
      );
    } else {
      this.activateSede(sede);
    }
  }

  private deactivateSede(sede: Sede): void {
    this.isDeactivatingSede.set(true);
    this.sedesService.deactivate(sede.id).subscribe({
      next: () => {
        this.isDeactivatingSede.set(false);
        this.listaSedes.update(sedes =>
          sedes.map(s => s.id === sede.id ? { ...s, activa: false } : s)
        );
        Popup.toastWarning('', 'Sede desactivada');
      },
      error: (err) => {
        this.isDeactivatingSede.set(false);
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudo desactivar la sede');
      }
    });
  }

  private activateSede(sede: Sede): void {
    this.isActivatingSede.set(true);
    this.sedesService.activate(sede.id).subscribe({
      next: () => {
        this.isActivatingSede.set(false);
        this.listaSedes.update(sedes =>
          sedes.map(s => s.id === sede.id ? { ...s, activa: true } : s)
        );
        Popup.toastSucess('', 'Sede activada');
      },
      error: (err) => {
        this.isActivatingSede.set(false);
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudo activar la sede');
      }
    });
  }

  isEditing(sedeId: number): boolean {
    return this.editingSedeId() === sedeId;
  }
}
