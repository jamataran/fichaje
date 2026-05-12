import { Component, OnInit, signal, computed, inject, OnDestroy } from '@angular/core';
import { HomeService } from '../../service/home.service';
import { Popup } from 'src/app/shared/helper/popup';
import { FichajeDto } from 'src/app/intranet/fichajes/model/fichajeDto';
import { Empleado } from 'src/app/intranet/empleados/model/empleado';
import { Router } from '@angular/router';
import { EmpleadosService } from 'src/app/intranet/empleados/service/empleados.service';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,
  imports: [CommonModule, SharedModule]
})
export class HomeComponent implements OnInit, OnDestroy {
  private readonly service = inject(HomeService);
  private readonly empleadoService = inject(EmpleadosService);
  private readonly router = inject(Router);

  model = signal<Empleado>(new Empleado('', '', '', '', null, null, null, null, null, ''));
  isLoading = signal<boolean>(false);
  currentTime = signal<string>('');
  
  private timeInterval?: any;

  dto = signal<FichajeDto>({
    diaDesde: '',
    diaHasta: '',
    horaDesde: '',
    horaHasta: '',
    hora: '',
    dia: '',
    origen: 'web',
    tipo: '',
    numeroUsuario: '',
    nombreUsuario: '',
  });

  ngOnInit(): void {
    this.loadData();
    this.updateTime();
    this.timeInterval = setInterval(() => this.updateTime(), 1000);
  }

  ngOnDestroy(): void {
    if (this.timeInterval) {
      clearInterval(this.timeInterval);
    }
  }

  loadData(): void {
    this.empleadoService.getMyUsuario().subscribe({
      next: data => {
        this.model.set(data);
        this.dto.update(d => ({ ...d, numeroUsuario: data.numero }));
      },
      error: err => {
        Popup.toastDanger('Ocurrió un error', err.message);
        console.error(err);
      }
    });
  }

  fichar(): void {
    if (this.isLoading()) return;

    const currentDto = this.dto();
    if (!currentDto.numeroUsuario) {
      Popup.toastDanger('Error', 'No se ha podido identificar al usuario. Por favor, recargue la página.');
      return;
    }

    this.isLoading.set(true);
    this.service.now(currentDto).subscribe({
      next: () => {
        this.isLoading.set(false);
        Popup.toastSucess('Éxito', 'Fichaje realizado correctamente');
        this.loadData();
      },
      error: err => {
        this.isLoading.set(false);
        Popup.toastDanger('Ocurrió un error', err.message || 'Error al registrar fichaje');
        console.error(err);
      }
    });
  }

  private updateTime(): void {
    const now = new Date();
    this.currentTime.set(now.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    }));
  }
}
