import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { MessageService, ConfirmationService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { PopupBridgeService } from './shared/helper/popup-bridge.service';

import { interceptorProvider } from './core/auth/interceptor/interceptor.service';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';


@NgModule({ declarations: [
        AppComponent,
    ],
    bootstrap: [AppComponent], imports: [BrowserModule,
        AppRoutingModule,
        ToastModule,
        ConfirmDialogModule], providers: [
        interceptorProvider,
        MessageService,
        ConfirmationService,
        PopupBridgeService,
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class AppModule { }
