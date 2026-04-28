import { Component, OnInit, signal, inject } from '@angular/core';
import { EmpleadosService } from '../../service/empleados.service';
import { EmpleadoDto } from '../../model/empleadoDto'
import { Pagination } from 'src/app/shared/components/pagination/model/pagination.model';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { EmpresasService } from 'src/app/intranet/empresas/service/empresas.service';
import { Empresa, SedeEmpresa } from 'src/app/intranet/empresas/model/empresa.model';
import { Popup } from 'src/app/shared/helper/popup';


@Component({
    selector: 'app-empleados',
    templateUrl: './empleados.component.html',
    styleUrls: ['./empleados.component.css'],
    standalone: false
})
export class EmpleadosComponent implements OnInit {
  public service = inject(EmpleadosService);
  private tokenService = inject(TokenService);
  private empresasService = inject(EmpresasService);

  headers = [
    "nombreEmpleado",
    "email",
    "numero",
    "dni",
    "diasVacaciones",
    "horasGeneradas",
    "enVacaciones",
    "deBaja",
    "working",
    "empresaIds",
    "sedeIds"
  ];

  // Señales para el estado
  order = signal('id');
  asc = signal(true);
  listaElementos = signal<any[]>([]);
  
  pag = signal<Pagination>({
    totalPages: [],
    page: 0,
    isFirst: false,
    isLast: false,
    size: 15,
    sizeLimit: 100,
    listPagesLimits: 4,
  });

  dto = signal<EmpleadoDto>({
    email: '',
    numero: '',
    nombreEmpleado: '',
    dni: '',
    diasVacacionesDesde: null,
    diasVacacionesHasta: null,
    horasGeneradasDesde: null,
    horasGeneradasHasta: null,
    enVacaciones: null,
    deBaja: null,
    working: null,
    empresaId: null,
    sedeId: null
  });

  isAdmin = signal(false);
  isRRHH = signal(false);
  isSuperAdmin = signal(false);

  listaEmpresas = signal<Empresa[]>([]);
  listaSedes = signal<SedeEmpresa[]>([]);
  lockEmpresa = signal(false);

  ngOnInit(): void {
    this.isRRHH.set(this.tokenService.isRRHH());
    this.isSuperAdmin.set(this.tokenService.isAdmin());
    this.isAdmin.set(this.isRRHH() || this.isSuperAdmin());

    this.cargarFiltrosIniciales();
    this.listarElementos();
  }

  private cargarFiltrosIniciales(): void {
    if (this.isSuperAdmin()) {
      this.empresasService.getAllEmpresas().subscribe({
        next: (data) => {
          this.listaEmpresas.set(data);
        },
        error: (err) => {
          console.error('Error al cargar empresas', err);
          Popup.toastDanger('Error', 'No se pudieron cargar las empresas para el filtro');
        }
      });
    } else if (this.isRRHH()) {
      const empresaId = this.tokenService.getEmpresaId();
      if (empresaId) {
        this.dto.update(d => ({ ...d, empresaId }));
        this.lockEmpresa.set(true);
        this.cargarSedes(empresaId);

        this.service.getMyUsuario().subscribe({
          next: (usuario) => {
            if (usuario.empresas && usuario.empresas.length > 0) {
              const empresa = usuario.empresas[0];
              this.listaEmpresas.set([empresa]);
            }
          },
          error: (err) => {
            console.error('Error al cargar mi perfil de usuario', err);
          }
        });
      }
    }
  }

  cargarSedes(empresaId: number): void {
    this.empresasService.getSedesByEmpresa(empresaId).subscribe({
      next: (sedes) => {
        this.listaSedes.set(sedes || []);
      },
      error: (err) => {
        console.error('Error al cargar sedes', err);
        Popup.toastDanger('Error', 'No se pudieron cargar las sedes de la empresa seleccionada');
        this.listaSedes.set([]);
      }
    });
  }

  onEmpresaChange(): void {
    const currentDto = this.dto();
    this.dto.update(d => ({ ...d, sedeId: null }));
    this.listaSedes.set([]);
    if (currentDto.empresaId) {
      this.cargarSedes(currentDto.empresaId);
    }
    this.listarElementos();
  }

  onSedeChange(): void {
    this.listarElementos();
  }

  listarElementos(): void {
    const currentPag = this.pag();
    this.service.getElements(this.dto(), currentPag.page, currentPag.size, this.order(), this.asc()).subscribe({
      next: (data) => {
        this.listaElementos.set(data.content);
        this.pag.update(p => ({
          ...p,
          isFirst: data.first,
          isLast: data.last,
          totalPages: new Array(data.totalPages)
        }));
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  onPaginate(pag: Pagination) {
    this.pag.set(pag);
    this.listarElementos();
  }

  setOrder(order: string): void {
    if (this.order() === order) {
      this.asc.update(a => !a);
    } else {
      this.order.set(order);
      this.asc.set(true);
    }
    this.listarElementos();
  }

  clearNombre(): void {
    this.dto.update(d => ({ ...d, nombreEmpleado: '' }));
    this.listarElementos();
  }

  clearEmail(): void {
    this.dto.update(d => ({ ...d, email: '' }));
    this.listarElementos();
  }

  clearNumero(): void {
    this.dto.update(d => ({ ...d, numero: '' }));
    this.listarElementos();
  }

  clearDiasVacacionesDesde(): void {
    this.dto.update(d => ({ ...d, diasVacacionesDesde: null }));
    this.listarElementos();
  }

  clearDiasVacacionesHasta(): void {
    this.dto.update(d => ({ ...d, diasVacacionesHasta: null }));
    this.listarElementos();
  }

  clearHorasDesde(): void {
    this.dto.update(d => ({ ...d, horasGeneradasDesde: null }));
    this.listarElementos();
  }

  clearHorasHasta(): void {
    this.dto.update(d => ({ ...d, horasGeneradasHasta: null }));
    this.listarElementos();
  }

  clear(): void {
    this.dto.set({
      email: '',
      numero: '',
      nombreEmpleado: '',
      dni: '',
      diasVacacionesDesde: null,
      diasVacacionesHasta: null,
      horasGeneradasDesde: null,
      horasGeneradasHasta: null,
      enVacaciones: null,
      deBaja: null,
      working: null,
      empresaId: this.lockEmpresa() ? this.dto().empresaId : null,
      sedeId: null
    });

    if (!this.lockEmpresa()) {
      this.listaSedes.set([]);
    }

    this.listarElementos();
  }
}
