import { Component, inject } from '@angular/core';
import { PermisoService } from '../../service/permiso.service';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-permisos-statistics',
  templateUrl: './permisos-statistics.component.html',
  styleUrls: ['./permisos-statistics.component.css'],
  standalone: true,
  imports: [SharedModule]
})
export class PermisosStatisticsComponent {
  readonly service = inject(PermisoService);
}
