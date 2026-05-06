import { Component, OnInit } from '@angular/core';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { PermisoService } from 'src/app/intranet/permisos/service/permiso.service';
import { NuevoPermiso } from '../../models/nuevoPermiso';
import { Popup } from 'src/app/shared/helper/popup';
import { Router } from '@angular/router';

@Component({
    selector: 'app-nuevo-permiso',
    templateUrl: './nuevo-permiso.component.html',
    styleUrls: ['./nuevo-permiso.component.css'],
    standalone: false
})
export class NuevoPermisoComponent implements OnInit {

  dia = '';
  horaInicio = '';
  horaFin = '';
  descripcion = '';
  numeroUsuario = '';
  nombreUsuario = '';


  constructor(
    private service: PermisoService,
    private tokenService: TokenService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.numeroUsuario = this.tokenService.getNumero();
    this.nombreUsuario = this.tokenService.getNombre();
  }

  clear(): void {
  }

  onRegister(): void {

    let permiso = new NuevoPermiso(this.dia, this.horaInicio, this.horaFin, this.descripcion, this.numeroUsuario, this.nombreUsuario);

    this.service.create(permiso).subscribe(
      data => {
        console.log(data)
        Popup.toastSucess('', 'Permiso Guardado');
        this.router.navigate([`intranet/home/permisos/${this.numeroUsuario}`])
      },
      err => {
        console.log(err)
        let msg = 'Error al guardar el permiso';
        if (err.error && err.error.detail) {
          msg = err.error.detail;
        } else if (err.error && err.error.mensaje) {
          msg = err.error.mensaje;
        }
        Popup.toastDanger('Error', msg);
      }
    )
  }


}
