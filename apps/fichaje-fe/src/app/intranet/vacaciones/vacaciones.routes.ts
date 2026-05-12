import { Routes } from '@angular/router';
import { VacacionesComponent } from './containers/vacaciones/vacaciones.component';
import { VacacionesDetallesComponent } from './containers/vacaciones-detalles/vacaciones-detalles.component';

export const VACACIONES_ROUTES: Routes = [
  { path: '', redirectTo: 'list', pathMatch: 'full' },
  { path: 'list', component: VacacionesComponent },
  { path: 'list/empleado/:numero', component: VacacionesComponent },
  { path: 'list/:id', component: VacacionesDetallesComponent },
];
