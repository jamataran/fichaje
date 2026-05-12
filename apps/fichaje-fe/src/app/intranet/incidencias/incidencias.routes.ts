import { Routes } from '@angular/router';
import { IncidenciasComponent } from './containers/incidencias/incidencias.component';
import { IncidenciasDetallesComponent } from './containers/incidencias-detalles/incidencias-detalles.component';
import { IncidenciasStatisticsComponent } from './containers/incidencias-statistics/incidencias-statistics.component';

export const INCIDENCIAS_ROUTES: Routes = [
  { path: '', redirectTo: 'list', pathMatch: 'full' },
  { path: 'list', component: IncidenciasComponent },
  { path: 'list/empleado/:numero', component: IncidenciasComponent },
  { path: 'list/:id', component: IncidenciasDetallesComponent },
  { path: 'statistics', component: IncidenciasStatisticsComponent },
];
