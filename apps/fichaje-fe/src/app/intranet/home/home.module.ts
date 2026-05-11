import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeRoutingModule } from './home-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { CoreModule } from 'src/app/core/core.module';
import { HomeService } from './service/home.service';
import { HomeComponent } from './containers/home/home.component';
import { FichajesUserComponent } from './containers/fichajes-user/fichajes-user.component';
import { PermisosUserComponent } from './containers/permisos-user/permisos-user.component';
import { NuevoPermisoComponent } from './containers/nuevo-permiso/nuevo-permiso.component';
import { NuevasVacacionesComponent } from './containers/nuevas-vacaciones/nuevas-vacaciones.component';
import { VacacionesUserComponent } from './containers/vacaciones-user/vacaciones-user.component';
import { CuentaComponent } from './containers/cuenta/cuenta.component';
import { EmpleadosService } from '../empleados/service/empleados.service';


@NgModule({
  declarations: [
    HomeComponent,
    CuentaComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    SharedModule,
    CoreModule,
    PermisosUserComponent,
    NuevoPermisoComponent,
    NuevasVacacionesComponent,
    VacacionesUserComponent,
    FichajesUserComponent
  ],
  providers: [
    HomeService,
    EmpleadosService
  ]
})
export class HomeModule { }
