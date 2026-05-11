import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-boton-guardar',
  templateUrl: './boton-guardar.component.html',
  styleUrls: ['./boton-guardar.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class BotonGuardarComponent {
  @Input() valid: boolean | null = null;
}
