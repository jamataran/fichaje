import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GuardService } from '../core/auth/guards/prod-guards.service';
import { IntranetComponent } from './intranet.component';

const routes: Routes = [
  {
    path: '', component: IntranetComponent, children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      {
        path: 'empleados', loadChildren: () => import('./empleados/empleados.module').then(m => m.EmpleadosModule)
        , canActivate: [GuardService], data: { expectedRol: ['admin', 'rrhh'] }
      },
      {
        path: 'incidencias', loadChildren: () => import('./incidencias/incidencias.module').then(m => m.IncidenciasModule)
        , canActivate: [GuardService], data: { expectedRol: ['admin', 'rrhh'] }
      },
      {
        path: 'fichajes', loadChildren: () => import('./fichajes/fichajes.routes').then(m => m.FICHAJES_ROUTES)
        , canActivate: [GuardService], data: { expectedRol: ['admin', 'rrhh'] }
      },
      {
        path: 'permisos', loadChildren: () => import('./permisos/permisos.routes').then(m => m.PERMISOS_ROUTES)
        , canActivate: [GuardService], data: { expectedRol: ['admin', 'rrhh'] }
      },      {
        path: 'calendario', loadChildren: () => import('./calendario/calendario.module').then(m => m.CalendarioModule)
        , canActivate: [GuardService], data: { expectedRol: ['admin', 'rrhh'] }
      },
      {
        path: 'vacaciones', loadChildren: () => import('./vacaciones/vacaciones.routes').then(m => m.VACACIONES_ROUTES)
        , canActivate: [GuardService], data: { expectedRol: ['admin', 'rrhh'] }
      },      {
        path: 'register', loadChildren: () => import('./register/register.module').then(m => m.RegisterModule)
        , canActivate: [GuardService], data: { expectedRol: ['admin', 'rrhh'] }
      },
      {
        path: 'empresas', loadChildren: () => import('./empresas/empresas.module').then(m => m.EmpresasModule)
        , canActivate: [GuardService], data: { expectedRol: ['admin'] }
      },
      {
        path: 'sedes', loadChildren: () => import('./sedes/sedes.module').then(m => m.SedesModule)
        , canActivate: [GuardService], data: { expectedRol: ['rrhh'] }
      },
      {
        path: 'cuenta', loadChildren: () => import('./cuenta/cuenta.routes').then(m => m.CUENTA_ROUTES)
      },
      { path: 'home', loadChildren: () => import('./home/home.routes').then(m => m.HOME_ROUTES) },
    ]
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IntranetRoutingModule { }
