import { Component, OnInit, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { VacacionesService } from 'src/app/intranet/vacaciones/service/vacaciones.service';
import { NuevasVacaciones } from 'src/app/intranet/vacaciones/models/nuevasVacaciones';
import { Popup } from 'src/app/shared/helper/popup';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-nuevas-vacaciones',
  templateUrl: './nuevas-vacaciones.component.html',
  styleUrls: ['./nuevas-vacaciones.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule]
})
export class NuevasVacacionesComponent implements OnInit {
  private readonly service = inject(VacacionesService);
  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  // Form Signals
  inicio = signal<string>('');
  fin = signal<string>('');
  numeroUsuario = signal<string>('');
  nombreUsuario = signal<string>('');

  ngOnInit(): void {
    this.numeroUsuario.set(this.tokenService.getNumero());
    this.nombreUsuario.set(this.tokenService.getNombre());
  }

  clear(): void {
    this.inicio.set('');
    this.fin.set('');
  }

  onRegister(): void {
    const vacaciones: NuevasVacaciones = {
      inicio: this.inicio(),
      fin: this.fin(),
      numeroUsuario: this.numeroUsuario(),
      nombreUsuario: this.nombreUsuario()
    };

    this.service.create(vacaciones).subscribe({
      next: data => {
        console.log(data);
        Popup.toastSucess('', 'Vacaciones Guardadas');
        this.router.navigate([`intranet/home/vacaciones/${this.numeroUsuario()}`]);
      },
      error: err => {
        console.error(err);
        Popup.toastDanger('Error', err.error.mensaje || 'Error al guardar vacaciones');
      }
    });
  }
}
