import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { PermisoService } from 'src/app/intranet/permisos/service/permiso.service';
import { NuevoPermiso } from '../../models/nuevoPermiso';
import { Popup } from 'src/app/shared/helper/popup';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-nuevo-permiso',
  templateUrl: './nuevo-permiso.component.html',
  styleUrls: ['./nuevo-permiso.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule, RouterLink]
})
export class NuevoPermisoComponent implements OnInit {
  private readonly service = inject(PermisoService);
  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  readonly dia = signal('');
  readonly horaInicio = signal('');
  readonly horaFin = signal('');
  readonly descripcion = signal('');
  readonly numeroUsuario = signal('');
  readonly nombreUsuario = signal('');

  ngOnInit(): void {
    this.numeroUsuario.set(this.tokenService.getNumero());
    this.nombreUsuario.set(this.tokenService.getNombre());
  }

  onRegister(): void {
    const permiso: NuevoPermiso = {
      dia: this.dia(), 
      horaInicio: this.horaInicio(), 
      horaFin: this.horaFin(), 
      descripcion: this.descripcion(), 
      numeroUsuario: this.numeroUsuario(), 
      nombreUsuario: this.nombreUsuario()
    };

    this.service.create(permiso).subscribe({
      next: () => {
        Popup.toastSucess('', 'Permiso Guardado');
        this.router.navigate([`intranet/home/permisos/${this.numeroUsuario()}`]);
      },
      error: err => {
        const msg = err.error?.detail || err.error?.mensaje || 'Error al guardar el permiso';
        Popup.toastDanger('Error', msg);
      }
    });
  }
}
