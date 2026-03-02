import { Injectable } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';

@Injectable({ providedIn: 'root' })
export class PopupBridgeService {

  private static messageService: MessageService;
  private static confirmationService: ConfirmationService;

  constructor(
    messageService: MessageService,
    confirmationService: ConfirmationService
  ) {
    PopupBridgeService.messageService = messageService;
    PopupBridgeService.confirmationService = confirmationService;
  }

  static getMessageService(): MessageService {
    return PopupBridgeService.messageService;
  }

  static getConfirmationService(): ConfirmationService {
    return PopupBridgeService.confirmationService;
  }
}
