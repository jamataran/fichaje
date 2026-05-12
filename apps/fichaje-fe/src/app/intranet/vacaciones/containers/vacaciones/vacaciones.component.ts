import { Component } from '@angular/core';
import { VacacionesListaComponent } from '../../components/vacaciones-lista/vacaciones-lista.component';

@Component({
  selector: 'app-vacaciones',
  templateUrl: './vacaciones.component.html',
  styleUrls: ['./vacaciones.component.css'],
  standalone: true,
  imports: [VacacionesListaComponent]
})
export class VacacionesComponent {
}
