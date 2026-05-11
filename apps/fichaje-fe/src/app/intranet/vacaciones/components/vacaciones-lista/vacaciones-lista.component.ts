import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { Pagination } from 'src/app/shared/components/pagination/model/pagination.model';
import { VacacionesDto } from '../../models/vacacionesDto';
import { VacacionesService } from '../../service/vacaciones.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-vacaciones-lista',
  templateUrl: './vacaciones-lista.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SharedModule]
})
export class VacacionesListaComponent implements OnInit {
  public readonly service = inject(VacacionesService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tokenService = inject(TokenService);

  readonly headers = [
    "inicio",
    "fin",
    "estado",
    "consumidas",
    "usuario.nombreEmpleado",
    "usuario.numero"
  ];

  // State Signals
  listaElementos = signal<any[]>([]);
  order = signal<string>('id');
  asc = signal<boolean>(false);
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

  dto = signal<VacacionesDto>({
    consumidas: null,
    estado: '',
    inicioDesde: '',
    inicioHasta: '',
    finDesde: '',
    finHasta: '',
    usuarioEmail: '',
    usuarioNumero: '',
    usuarioNombre: '',
    usuarioDni: '',
  });

  ngOnInit(): void {
    const numero = this.activatedRoute.snapshot.params.numero;
    if (numero) {
      this.updateDto('usuarioNumero', numero);
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

  onPaginate(newPag: Pagination) {
    this.pag.set(newPag);
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

  updateDto(field: keyof VacacionesDto, value: any): void {
    this.dto.update(d => ({ ...d, [field]: value }));
    this.listarElementos();
  }

  clearField(field: keyof VacacionesDto): void {
    this.updateDto(field, field === 'consumidas' ? null : '');
  }

  clear(): void {
    this.dto.set({
      consumidas: null,
      estado: '',
      inicioDesde: '',
      inicioHasta: '',
      finDesde: '',
      finHasta: '',
      usuarioEmail: '',
      usuarioNumero: '',
      usuarioNombre: '',
      usuarioDni: '',
    });
    this.listarElementos();
  }
}
