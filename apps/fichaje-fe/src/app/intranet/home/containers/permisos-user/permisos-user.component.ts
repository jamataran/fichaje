import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PermisosListaComponent } from '../../../permisos/components/permisos-lista/permisos-lista.component';

@Component({
  selector: 'app-permisos-user',
  templateUrl: './permisos-user.component.html',
  styleUrls: ['./permisos-user.component.css'],
  standalone: true,
  imports: [PermisosListaComponent, RouterLink]
})
export class PermisosUserComponent {}
