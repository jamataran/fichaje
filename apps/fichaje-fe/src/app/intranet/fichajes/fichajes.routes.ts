import { Routes } from '@angular/router';
import { FichajesComponent } from './containers/fichajes/fichajes.component';
import { FichajesDetalleComponent } from './containers/fichaje-detalle/fichajes-detalle.component';

export const FICHAJES_ROUTES: Routes = [
  { path: '', redirectTo: 'list', pathMatch: 'full' },
  { path: 'list', component: FichajesComponent },
  { path: 'list/empleado/:numero', component: FichajesComponent },
  { path: 'list/:id', component: FichajesDetalleComponent },
];
