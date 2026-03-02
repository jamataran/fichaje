import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { MessageService, ConfirmationService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { PopupBridgeService } from './shared/helper/popup-bridge.service';

import { interceptorProvider } from './core/auth/interceptor/interceptor.service';
import { HttpClientModule } from '@angular/common/http';


@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ToastModule,
    ConfirmDialogModule,
  ],
  providers: [
    interceptorProvider,
    MessageService,
    ConfirmationService,
    PopupBridgeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
