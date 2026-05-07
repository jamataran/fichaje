import { Routes } from '@angular/router';
import { PermisosComponent } from './containers/permisos/permisos.component';
import { PermisosDetallesComponent } from './containers/permisos-detalles/permisos-detalles.component';
import { PermisosStatisticsComponent } from './containers/permisos-statistics/permisos-statistics.component';

export const PERMISOS_ROUTES: Routes = [
  { path: '', redirectTo: 'list', pathMatch: 'full' },
  { path: 'list', component: PermisosComponent },
  { path: 'list/empleado/:numero', component: PermisosComponent },
  { path: 'list/:id', component: PermisosDetallesComponent },
  { path: 'statistics', component: PermisosStatisticsComponent },
];
