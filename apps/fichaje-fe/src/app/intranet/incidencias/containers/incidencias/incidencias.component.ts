import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { Pagination } from 'src/app/shared/components/pagination/model/pagination.model';
import { IncidenciaDto } from '../../model/incidenciaDto';
import { IncidenciaService } from '../../service/incidencia.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-incidencias',
  templateUrl: './incidencias.component.html',
  styleUrls: ['./incidencias.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule, RouterModule]
})
export class IncidenciasComponent implements OnInit {
  public readonly service = inject(IncidenciaService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tokenService = inject(TokenService);

  headers = [
    "dia",
    "resuelta",
    "descripcion",
    "usuario.nombreEmpleado",
    "usuario.numero"
  ];

  order = signal<string>('id');
  asc = signal<boolean>(false);
  listaElementos = signal<any[]>([]);
  isAdmin = signal<boolean>(false);

  pag = signal<Pagination>({
    totalPages: [],
    page: 0,
    isFirst: false,
    isLast: false,
    size: 15,
    sizeLimit: 100,
    listPagesLimits: 4,
  });

  dto = signal<IncidenciaDto>({
    descripcion: '',
    diaDesde: '',
    diaHasta: '',
    explicacion: null,
    resuelta: null,
    usuarioEmail: '',
    usuarioNumero: '',
    usuarioNombre: '',
    usuarioDni: '',
  });

  ngOnInit(): void {
    const numero = this.activatedRoute.snapshot.params.numero;
    if (numero) {
      this.dto.update(d => ({ ...d, usuarioNumero: numero }));
    }
    this.isAdmin.set(this.tokenService.isRRHH());
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
      error: err => console.error(err)
    });
  }

  onPaginate(pag: Pagination): void {
    this.pag.set(pag);
    this.listarElementos();
  }

  setOrder(order: string): void {
    this.order.set(order);
    this.asc.update(a => !a);
    this.listarElementos();
  }

  clearField(field: keyof IncidenciaDto): void {
    this.dto.update(d => ({ ...d, [field]: field === 'resuelta' ? null : '' }));
    this.listarElementos();
  }

  clear(): void {
    this.dto.set({
      descripcion: '',
      diaDesde: '',
      diaHasta: '',
      explicacion: '',
      resuelta: null,
      usuarioEmail: '',
      usuarioNumero: '',
      usuarioNombre: '',
      usuarioDni: '',
    });
    this.listarElementos();
  }
}
