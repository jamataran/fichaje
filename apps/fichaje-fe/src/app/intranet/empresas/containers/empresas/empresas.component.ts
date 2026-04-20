import { Component, OnInit, signal } from '@angular/core';
import { Popup } from 'src/app/shared/helper/popup';
import { Pagination } from 'src/app/shared/components/pagination/model/pagination.model';
import { Empresa, EmpresaCreate, EmpresaUpdate, SedeEmpresa, SedeUpdate } from '../../model/empresa.model';
import { EmpresasService } from '../../service/empresas.service';

@Component({
  selector: 'app-empresas',
  templateUrl: './empresas.component.html',
  styleUrls: ['./empresas.component.css'],
  standalone: false
})
export class EmpresasComponent implements OnInit {

  listaEmpresas: Empresa[] = [];
  isCreating = signal(false);
  isUpdating = signal(false);
  isUpdatingSede = signal(false);
  isDeletingSede = signal(false);
  loadingSedesEmpresaId = signal<number | null>(null);
  expandedEmpresaId = signal<number | null>(null);
  editingEmpresaId = signal<number | null>(null);
  editingSedeId = signal<number | null>(null);
  editingSedeEmpresaId = signal<number | null>(null);

  sedesByEmpresa: Record<number, SedeEmpresa[]> = {};

  order = 'nombre';
  asc = true;

  pag: Pagination = {
    totalPages: [],
    page: 0,
    isFirst: false,
    isLast: false,
    size: 15,
    sizeLimit: 100,
    listPagesLimits: 4,
  };

  formModel: EmpresaCreate = this.getEmptyEmpresa();
  editModel: EmpresaUpdate = this.getEmptyUpdateEmpresa();
  sedeEditModel: SedeUpdate = this.getEmptyUpdateSede();

  constructor(
    private empresasService: EmpresasService
  ) { }

  ngOnInit(): void {
    this.listarEmpresas();
  }

  private getEmptyEmpresa(): EmpresaCreate {
    return {
      nombre: '',
      razonSocial: '',
      cif: '',
      activa: true,
      email: '',
      telefono: '',
      direccion: '',
      codigoPostal: '',
      localidad: '',
      provincia: '',
      pais: 'España',
      latitud: null,
      longitud: null
    };
  }

  private getEmptyUpdateEmpresa(): EmpresaUpdate {
    return {
      nombre: '',
      razonSocial: '',
      cif: '',
      activa: true,
    };
  }

  private getEmptyUpdateSede(): SedeUpdate {
    return {
      nombre: '',
      email: '',
      telefono: '',
      direccion: '',
      codigoPostal: '',
      localidad: '',
      provincia: '',
      pais: 'España',
      activa: true,
    };
  }

  listarEmpresas(): void {
    this.empresasService.getElements(this.pag.page, this.pag.size, this.order, this.asc).subscribe({
      next: (data) => {
        this.listaEmpresas = data.content ?? [];
        this.pag.isFirst = data.first;
        this.pag.isLast = data.last;
        this.pag.totalPages = new Array(data.totalPages);
      },
      error: (err) => {
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudieron cargar las empresas');
      }
    });
  }

  onPaginate(pag: Pagination): void {
    this.pag = pag;
    this.listarEmpresas();
  }

  setOrder(order: string): void {
    this.order = order;
    this.asc = !this.asc;
    this.listarEmpresas();
  }

  onCreate(): void {
    const payload: EmpresaCreate = {
      ...this.formModel,
      nombre: this.formModel.nombre.trim(),
      razonSocial: this.formModel.razonSocial?.trim() || '',
      cif: this.formModel.cif.trim().toUpperCase(),
      email: this.formModel.email.trim(),
      telefono: this.formModel.telefono?.trim() || '',
      direccion: this.formModel.direccion.trim(),
      codigoPostal: this.formModel.codigoPostal.trim(),
      localidad: this.formModel.localidad.trim(),
      provincia: this.formModel.provincia.trim(),
      pais: this.formModel.pais.trim(),
      latitud: this.formModel.latitud ?? null,
      longitud: this.formModel.longitud ?? null,
    };

    this.isCreating.set(true);

    this.empresasService.create(payload).subscribe({
      next: () => {
        this.isCreating.set(false);
        Popup.toastSucess('', 'Empresa creada correctamente');
        this.formModel = this.getEmptyEmpresa();
        this.pag.page = 0;
        this.listarEmpresas();
      },
      error: (err) => {
        this.isCreating.set(false);
        const msg = err?.error?.mensaje || err?.error?.message || 'No se pudo crear la empresa';
        Popup.toastDanger('Error', msg);
      }
    });
  }

  onDelete(empresa: Empresa): void {
    Popup.dangerConfirmBox(
      `¿Desea eliminar la empresa ${empresa.nombre}?`,
      'Esta operación no se puede deshacer',
      'SI',
      'NO',
      () => this.deleteEmpresa(empresa.id)
    );
  }

  onStartEdit(empresa: Empresa): void {
    this.editingEmpresaId.set(empresa.id);
    this.editModel = {
      nombre: empresa.nombre,
      razonSocial: empresa.razonSocial ?? '',
      cif: empresa.cif,
      activa: empresa.activa,
    };
  }

  onCancelEdit(): void {
    this.editingEmpresaId.set(null);
    this.editModel = this.getEmptyUpdateEmpresa();
  }

  onUpdate(): void {
    const empresaId = this.editingEmpresaId();
    if (!empresaId) {
      return;
    }

    const payload: EmpresaUpdate = {
      nombre: this.editModel.nombre.trim(),
      razonSocial: this.editModel.razonSocial?.trim() || '',
      cif: this.editModel.cif.trim().toUpperCase(),
      activa: this.editModel.activa,
    };

    this.isUpdating.set(true);

    this.empresasService.update(empresaId, payload).subscribe({
      next: () => {
        this.isUpdating.set(false);
        Popup.toastSucess('', 'Empresa actualizada correctamente');
        this.onCancelEdit();
        this.listarEmpresas();
      },
      error: (err) => {
        this.isUpdating.set(false);
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudo actualizar la empresa');
      }
    });
  }

  toggleSedes(empresa: Empresa): void {
    const currentExpanded = this.expandedEmpresaId();

    if (currentExpanded === empresa.id) {
      this.expandedEmpresaId.set(null);
      return;
    }

    this.expandedEmpresaId.set(empresa.id);

    if (!this.sedesByEmpresa[empresa.id]) {
      this.loadSedesByEmpresa(empresa.id);
    }
  }

  private loadSedesByEmpresa(empresaId: number): void {
    this.loadingSedesEmpresaId.set(empresaId);

    this.empresasService.getSedesByEmpresa(empresaId).subscribe({
      next: (sedes) => {
        this.sedesByEmpresa[empresaId] = sedes ?? [];
        this.loadingSedesEmpresaId.set(null);
      },
      error: () => {
        this.sedesByEmpresa[empresaId] = [];
        this.loadingSedesEmpresaId.set(null);
        Popup.toastDanger('Error', 'No se pudieron cargar las sedes de la empresa');
      }
    });
  }

  isExpanded(empresaId: number): boolean {
    return this.expandedEmpresaId() === empresaId;
  }

  isEditing(empresaId: number): boolean {
    return this.editingEmpresaId() === empresaId;
  }

  isEditingSede(empresaId: number, sedeId: number): boolean {
    return this.editingSedeEmpresaId() === empresaId && this.editingSedeId() === sedeId;
  }

  getSedes(empresaId: number): SedeEmpresa[] {
    return this.sedesByEmpresa[empresaId] ?? [];
  }

  onStartEditSede(empresaId: number, sede: SedeEmpresa): void {
    this.editingSedeEmpresaId.set(empresaId);
    this.editingSedeId.set(sede.id);
    this.sedeEditModel = {
      nombre: sede.nombre,
      email: sede.email,
      telefono: sede.telefono ?? '',
      direccion: sede.direccion,
      codigoPostal: sede.codigoPostal,
      localidad: sede.localidad,
      provincia: sede.provincia,
      pais: sede.pais,
      activa: sede.activa,
    };
  }

  onCancelEditSede(): void {
    this.editingSedeEmpresaId.set(null);
    this.editingSedeId.set(null);
    this.sedeEditModel = this.getEmptyUpdateSede();
  }

  onUpdateSede(empresaId: number): void {
    const sedeId = this.editingSedeId();
    if (!sedeId) {
      return;
    }

    const payload: SedeUpdate = {
      nombre: this.sedeEditModel.nombre.trim(),
      email: this.sedeEditModel.email.trim(),
      telefono: this.sedeEditModel.telefono?.trim() || '',
      direccion: this.sedeEditModel.direccion.trim(),
      codigoPostal: this.sedeEditModel.codigoPostal.trim(),
      localidad: this.sedeEditModel.localidad.trim(),
      provincia: this.sedeEditModel.provincia.trim(),
      pais: this.sedeEditModel.pais.trim(),
      activa: this.sedeEditModel.activa,
    };

    this.isUpdatingSede.set(true);

    this.empresasService.updateSede(sedeId, payload).subscribe({
      next: () => {
        this.isUpdatingSede.set(false);
        Popup.toastSucess('', 'Sede actualizada correctamente');
        this.onCancelEditSede();
        this.loadSedesByEmpresa(empresaId);
      },
      error: (err) => {
        this.isUpdatingSede.set(false);
        const msg = err?.error?.mensaje || err?.error?.message || 'No se pudo actualizar la sede';
        Popup.toastDanger('Error', msg);
      }
    });
  }

  onDeleteSede(empresaId: number, sede: SedeEmpresa): void {
    Popup.dangerConfirmBox(
      `¿Desea borrar la sede ${sede.nombre}?`,
      'Esta operación desactivará la sede y no se podrá usar para nuevas asignaciones',
      'SI',
      'NO',
      () => this.deactivateSede(empresaId, sede.id)
    );
  }

  private deactivateSede(empresaId: number, sedeId: number): void {
    this.isDeletingSede.set(true);

    this.empresasService.deactivateSede(sedeId).subscribe({
      next: () => {
        Popup.toastWarning('', 'Sede desactivada');
        this.isDeletingSede.set(false);
        this.onCancelEditSede();
        this.loadSedesByEmpresa(empresaId);
      },
      error: (err) => {
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudo desactivar la sede');
        this.isDeletingSede.set(false);
      }
    });
  }

  private deleteEmpresa(id: number): void {
    this.empresasService.delete(id).subscribe({
      next: () => {
        Popup.toastWarning('', 'Empresa eliminada');

        if (this.listaEmpresas.length === 1 && this.pag.page > 0) {
          this.pag.page--;
        }

        this.listarEmpresas();
      },
      error: (err) => {
        Popup.toastDanger('Error', err?.error?.mensaje ?? 'No se pudo eliminar la empresa');
      }
    });
  }
}
