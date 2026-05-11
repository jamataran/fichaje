import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { VacacionesListaComponent } from '../../../vacaciones/components/vacaciones-lista/vacaciones-lista.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-vacaciones-user',
  templateUrl: './vacaciones-user.component.html',
  styleUrls: ['./vacaciones-user.component.css'],
  standalone: true,
  imports: [CommonModule, RouterLink, VacacionesListaComponent]
})
export class VacacionesUserComponent {
}
