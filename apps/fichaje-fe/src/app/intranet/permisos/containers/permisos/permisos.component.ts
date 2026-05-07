import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PermisosListaComponent } from '../../components/permisos-lista/permisos-lista.component';

@Component({
  selector: 'app-permisos',
  templateUrl: './permisos.component.html',
  styleUrls: ['./permisos.component.css'],
  standalone: true,
  imports: [PermisosListaComponent, RouterLink]
})
export class PermisosComponent {}
