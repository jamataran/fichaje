import { NgModule } from '@angular/core';
import { SedesRoutingModule } from './sedes-routing.module';
import { SedesComponent } from './containers/sedes/sedes.component';

@NgModule({
  imports: [
    SedesRoutingModule,
    SedesComponent
  ]
})
export class SedesModule { }
