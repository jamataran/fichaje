import { Component, OnInit } from '@angular/core';
import { PermisoService } from '../../service/permiso.service';

@Component({
    selector: 'app-permisos-statistics',
    templateUrl: './permisos-statistics.component.html',
    styleUrls: ['./permisos-statistics.component.css'],
    standalone: false
})
export class PermisosStatisticsComponent implements OnInit {

  constructor(
    public service: PermisoService,
  ) { }

  ngOnInit(): void {
  }

}
