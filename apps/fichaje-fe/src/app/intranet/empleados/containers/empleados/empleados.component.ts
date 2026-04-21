import { Component, OnInit } from '@angular/core';
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

  headers =
    [
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

  //paginación
  order = 'id'
  asc = true

  listaElementos: Array<any> = []

  pag: Pagination = {
    totalPages: [],
    page: 0,
    isFirst: false,
    isLast: false,
    size: 15,
    sizeLimit: 100,
    listPagesLimits: 4,
  }

  dto: EmpleadoDto = {
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
  }

  isAdmin: boolean = false;
  isRRHH: boolean = false;
  isSuperAdmin: boolean = false;

  listaEmpresas: Empresa[] = [];
  listaSedes: SedeEmpresa[] = [];
  lockEmpresa: boolean = false;

  constructor(
    public service: EmpleadosService,
    private tokenService: TokenService,
    private empresasService: EmpresasService
  ) { }

  ngOnInit(): void {
    this.isRRHH = this.tokenService.isRRHH();
    this.isSuperAdmin = this.tokenService.isAdmin();
    this.isAdmin = this.isRRHH || this.isSuperAdmin;

    this.cargarFiltrosIniciales();
    this.listarElementos();
  }

  private cargarFiltrosIniciales(): void {
    if (this.isSuperAdmin) {
      this.empresasService.getAllEmpresas().subscribe({
        next: (data) => {
          this.listaEmpresas = data;
        },
        error: (err) => {
          console.error('Error al cargar empresas', err);
          Popup.toastDanger('Error', 'No se pudieron cargar las empresas para el filtro');
        }
      });
    } else if (this.isRRHH) {
      const empresaId = this.tokenService.getEmpresaId();
      if (empresaId) {
        this.dto.empresaId = empresaId;
        this.lockEmpresa = true;
        this.cargarSedes(empresaId);
        
        this.empresasService.getMiEmpresa().subscribe({
          next: (empresa) => {
            this.listaEmpresas = [empresa];
          },
          error: (err) => {
            console.error('Error al cargar mi empresa', err);
          }
        });
      }
    }
  }

  cargarSedes(empresaId: number): void {
    this.empresasService.getSedesByEmpresa(empresaId).subscribe({
      next: (sedes) => {
        this.listaSedes = sedes || [];
      },
      error: (err) => {
        console.error('Error al cargar sedes', err);
        Popup.toastDanger('Error', 'No se pudieron cargar las sedes de la empresa seleccionada');
        this.listaSedes = [];
      }
    });
  }

  onEmpresaChange(): void {
    this.dto.sedeId = null;
    this.listaSedes = [];
    if (this.dto.empresaId) {
      this.cargarSedes(this.dto.empresaId);
    }
    this.listarElementos();
  }

  onSedeChange(): void {
    this.listarElementos();
  }


  listarElementos(): void {
    this.service.getElements(this.dto, this.pag.page, this.pag.size, this.order, this.asc).subscribe(
      data => {
        this.listaElementos = data.content
        this.pag.isFirst = data.first
        this.pag.isLast = data.last
        this.pag.totalPages = new Array(data.totalPages)
      },
      err => {
        console.log(err)
      }
    )
  }

  onPaginate(pag: Pagination) {
    this.pag = pag;
    this.listarElementos()
  }

  setOrder(order: string): void {
    this.order = order
    this.asc = !this.asc
    this.listarElementos()
  }
  clearNombre(): void {
    this.dto.nombreEmpleado = ''
    this.listarElementos()
  }
  clearEmail(): void {
    this.dto.email = ''
    this.listarElementos()
  }
  clearNumero(): void {
    this.dto.numero = ''
    this.listarElementos()
  }
  clearDiasVacacionesDesde(): void {
    this.dto.diasVacacionesDesde = null
    this.listarElementos()
  }
  clearDiasVacacionesHasta(): void {
    this.dto.diasVacacionesHasta = null
    this.listarElementos()
  }
  clearHorasDesde(): void {
    this.dto.horasGeneradasDesde = null
    this.listarElementos()
  }
  clearHorasHasta(): void {
    this.dto.horasGeneradasHasta = null
    this.listarElementos()
  }
  clear(): void {
    this.dto.email = ''
    this.dto.numero = ''
    this.dto.nombreEmpleado = ''
    this.dto.dni = ''
    this.dto.diasVacacionesDesde = null
    this.dto.diasVacacionesHasta = null
    this.dto.horasGeneradasDesde = null
    this.dto.horasGeneradasHasta = null
    this.dto.enVacaciones = null
    this.dto.deBaja = null
    this.dto.working = null
    
    if (!this.lockEmpresa) {
      this.dto.empresaId = null;
      this.listaSedes = [];
    }
    this.dto.sedeId = null;

    this.listarElementos()
  }

}
