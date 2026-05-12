import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';
import { CoreModule } from 'src/app/core/core.module';

import { EmpresasRoutingModule } from './empresas-routing.module';
import { EmpresasComponent } from './containers/empresas/empresas.component';
import { EmpresasService } from './service/empresas.service';

@NgModule({
  declarations: [
    EmpresasComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    EmpresasRoutingModule,
    SharedModule,
    CoreModule
  ],
  providers: [
    EmpresasService
  ]
})
export class EmpresasModule { }
