import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SedesRoutingModule } from './sedes-routing.module';
import { SedesComponent } from './containers/sedes/sedes.component';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [
    SedesComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SedesRoutingModule,
    SharedModule
  ]
})
export class SedesModule { }
