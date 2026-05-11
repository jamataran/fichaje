import { Component, signal, inject } from '@angular/core';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { EmpleadosService } from 'src/app/intranet/empleados/service/empleados.service';
import { Popup } from 'src/app/shared/helper/popup';
import { Password } from 'src/app/intranet/empleados/model/password';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-cuenta',
  templateUrl: './cuenta.component.html',
  styleUrls: ['./cuenta.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule]
})
export class CuentaComponent {
  private readonly service = inject(EmpleadosService);
  private readonly tokenService = inject(TokenService);

  pass1 = signal<string>('');
  pass2 = signal<string>('');

  changePassword(): void {
    if (this.checkPasswords()) {
      const userId = this.tokenService.getId();
      this.service.changePassword(userId, new Password(this.pass1())).subscribe({
        next: () => {
          Popup.toastSucess('', 'Contraseña cambiada');
          this.pass1.set('');
          this.pass2.set('');
        },
        error: (err) => {
          Popup.toastDanger('Ocurrió un error', err.message);
          console.error(err);
        }
      });
    }
  }

  private checkPasswords(): boolean {
    if (this.pass1() === this.pass2()) {
      return true;
    } else {
      Popup.toastDanger('', 'Las contraseñas no coinciden');
      return false;
    }
  }

  updatePass1(val: string) {
    this.pass1.set(val);
  }

  updatePass2(val: string) {
    this.pass2.set(val);
  }
}
