import { Component, OnInit, signal, inject } from '@angular/core';
import { IncidenciaService } from '../../service/incidencia.service';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-incidencias-statistics',
  templateUrl: './incidencias-statistics.component.html',
  styleUrls: ['./incidencias-statistics.component.css'],
  standalone: true,
  imports: [CommonModule, SharedModule]
})
export class IncidenciasStatisticsComponent implements OnInit {
  public readonly service = inject(IncidenciaService);
  listaElementos = signal<any[]>([]);

  ngOnInit(): void {
    this.service.getRankingIncidencias().subscribe({
      next: data => this.listaElementos.set(data),
      error: err => console.error(err)
    });
  }
}
