import { Routes } from '@angular/router';
import { HomeComponent } from './containers/home/home.component';
import { FichajesUserComponent } from './containers/fichajes-user/fichajes-user.component';
import { NuevoPermisoComponent } from './containers/nuevo-permiso/nuevo-permiso.component';
import { PermisosUserComponent } from './containers/permisos-user/permisos-user.component';
import { NuevasVacacionesComponent } from './containers/nuevas-vacaciones/nuevas-vacaciones.component';
import { VacacionesUserComponent } from './containers/vacaciones-user/vacaciones-user.component';
import { CuentaComponent } from './containers/cuenta/cuenta.component';

export const HOME_ROUTES: Routes = [
  { path: '', component: HomeComponent },
  { path: 'fichajes/:numero', component: FichajesUserComponent },
  { path: 'permisos/nuevo', component: NuevoPermisoComponent },
  { path: 'permisos/:numero', component: PermisosUserComponent },
  { path: 'vacaciones/nuevo', component: NuevasVacacionesComponent },
  { path: 'vacaciones/:numero', component: VacacionesUserComponent },
  { path: 'cuenta', component: CuentaComponent },
];
