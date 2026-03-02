import { PopupBridgeService } from './popup-bridge.service';

export class Popup {

  static toastSucess(title: string, msg: string): void {
    PopupBridgeService.getMessageService().add({
      severity: 'success',
      summary: title,
      detail: msg,
      life: 8000
    });
  }

  static toastDanger(title: string, msg: string): void {
    PopupBridgeService.getMessageService().add({
      severity: 'error',
      summary: title,
      detail: msg,
      life: 8000
    });
  }

  static toastWarning(title: string, msg: string): void {
    PopupBridgeService.getMessageService().add({
      severity: 'warn',
      summary: title,
      detail: msg,
      life: 8000
    });
  }

  static toastINFO(title: string, msg: string): void {
    PopupBridgeService.getMessageService().add({
      severity: 'info',
      summary: title,
      detail: msg,
      life: 8000
    });
  }

  static infoConfirmBox(title: string, msg: string, confirmBtn: string, denyBtn: string, onConfirm: () => void): void {
    PopupBridgeService.getConfirmationService().confirm({
      header: title,
      message: msg,
      acceptLabel: confirmBtn,
      rejectLabel: denyBtn,
      acceptButtonStyleClass: 'p-button-info',
      accept: onConfirm
    });
  }

  static successConfirmBox(title: string, msg: string, confirmBtn: string, denyBtn: string, onConfirm: () => void): void {
    PopupBridgeService.getConfirmationService().confirm({
      header: title,
      message: msg,
      acceptLabel: confirmBtn,
      rejectLabel: denyBtn,
      acceptButtonStyleClass: 'p-button-success',
      accept: onConfirm
    });
  }

  static warningConfirmBox(title: string, msg: string, confirmBtn: string, denyBtn: string, onConfirm: () => void): void {
    PopupBridgeService.getConfirmationService().confirm({
      header: title,
      message: msg,
      acceptLabel: confirmBtn,
      rejectLabel: denyBtn,
      acceptButtonStyleClass: 'p-button-warning',
      accept: onConfirm
    });
  }

  static dangerConfirmBox(title: string, msg: string, confirmBtn: string, denyBtn: string, onConfirm: () => void): void {
    PopupBridgeService.getConfirmationService().confirm({
      header: title,
      message: msg,
      acceptLabel: confirmBtn,
      rejectLabel: denyBtn,
      acceptButtonStyleClass: 'p-button-danger',
      accept: onConfirm
    });
  }
}
