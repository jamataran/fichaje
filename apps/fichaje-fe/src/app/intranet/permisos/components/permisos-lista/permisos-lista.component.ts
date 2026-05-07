import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { Pagination } from 'src/app/shared/components/pagination/model/pagination.model';
import { PermisoDto } from '../../model/permisoDto';
import { PermisoService } from '../../service/permiso.service';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-permisos-lista',
  templateUrl: './permisos-lista.component.html',
  styleUrls: ['./permisos-lista.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule, RouterLink]
})
export class PermisosListaComponent implements OnInit {
  public readonly service = inject(PermisoService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tokenService = inject(TokenService);

  readonly headers = [
    "dia",
    "horaInicio",
    "horaFin",
    "estado",
    "descripcion",
    "usuario.nombreEmpleado",
    "usuario.numero"
  ];

  // State with Signals
  readonly order = signal<string>('id');
  readonly asc = signal<boolean>(false);
  readonly listaElementos = signal<any[]>([]);
  readonly isAdmin = signal<boolean>(false);

  readonly pag = signal<Pagination>({
    totalPages: [],
    page: 0,
    isFirst: false,
    isLast: false,
    size: 15,
    sizeLimit: 100,
    listPagesLimits: 4,
  });

  readonly dto = signal<PermisoDto>({
    descripcion: '',
    diaDesde: '',
    diaHasta: '',
    horaFinDesde: '',
    horaFinHasta: '',
    horaInicioDesde: '',
    horaInicioHasta: '',
    estado: '',
    usuarioEmail: '',
    usuarioNumero: '',
    usuarioNombre: '',
    usuarioDni: '',
  });

  ngOnInit(): void {
    const numero = this.activatedRoute.snapshot.params.numero;
    this.isAdmin.set(this.tokenService.isRRHH());
    
    if (numero) {
      this.dto.update(d => ({ ...d, usuarioNumero: numero }));
    }
    
    this.listarElementos();
  }

  listarElementos(): void {
    this.service.getElements(
      this.dto(), 
      this.pag().page, 
      this.pag().size, 
      this.order(), 
      this.asc()
    ).subscribe({
      next: data => {
        this.listaElementos.set(data.content);
        this.pag.update(p => ({
          ...p,
          isFirst: data.first,
          isLast: data.last,
          totalPages: new Array(data.totalPages)
        }));
      },
      error: err => console.error('Error fetching permisos:', err)
    });
  }

  onPaginate(newPag: Pagination) {
    this.pag.set(newPag);
    this.listarElementos();
  }

  setOrder(newOrder: string): void {
    if (this.order() === newOrder) {
      this.asc.update(a => !a);
    } else {
      this.order.set(newOrder);
      this.asc.set(false);
    }
    this.listarElementos();
  }

  updateDto(partial: Partial<PermisoDto>): void {
    this.dto.update(d => ({ ...d, ...partial }));
    this.listarElementos();
  }

  clearField(field: keyof PermisoDto): void {
    this.updateDto({ [field]: '' });
  }

  clear(): void {
    this.dto.set({
      descripcion: '',
      diaDesde: '',
      diaHasta: '',
      horaFinDesde: '',
      horaFinHasta: '',
      horaInicioDesde: '',
      horaInicioHasta: '',
      estado: '',
      usuarioEmail: '',
      usuarioNumero: '',
      usuarioNombre: '',
      usuarioDni: '',
    });
    this.listarElementos();
  }
}
