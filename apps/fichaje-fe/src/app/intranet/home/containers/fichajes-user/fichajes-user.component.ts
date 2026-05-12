import { Component } from '@angular/core';
import { FichajesListaComponent } from '../../../fichajes/components/fichajes-lista/fichajes-lista.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-fichajes-user',
  templateUrl: './fichajes-user.component.html',
  styleUrls: ['./fichajes-user.component.css'],
  standalone: true,
  imports: [CommonModule, FichajesListaComponent]
})
export class FichajesUserComponent {
}
