import { Component } from '@angular/core';
import { FichajesListaComponent } from '../../components/fichajes-lista/fichajes-lista.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-fichajes',
  templateUrl: './fichajes.component.html',
  styleUrls: ['./fichajes.component.css'],
  standalone: true,
  imports: [CommonModule, FichajesListaComponent]
})
export class FichajesComponent {
}
