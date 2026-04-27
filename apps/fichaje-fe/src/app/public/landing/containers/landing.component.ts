import {Component, OnInit, signal} from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginUsuario } from 'src/app/core/auth/model/login-usuario';
import { AuthService, EmpresaAuthOption } from 'src/app/core/auth/service/auth.service';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { EmpleadosService } from 'src/app/intranet/empleados/service/empleados.service';

@Component({
    selector: 'app-landing',
    templateUrl: './landing.component.html',
    styleUrls: ['./landing.component.css'],
    standalone: false
})
export class LandingComponent implements OnInit {

  loginForm: UntypedFormGroup;
  errMsg: string = '';
  isLoading = signal(false)
  showEmpresaSelectorModal = signal(false)
  isLoadingEmpresas = signal(false)
  isSelectingEmpresa = signal(false)
  empresaOptions = signal<EmpresaAuthOption[]>([])
  selectedEmpresaId = signal<number | null>(null)

  constructor(
    private formBuilder: UntypedFormBuilder,
    private tokenService: TokenService,
    private authService: AuthService,
    private empleadosService: EmpleadosService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      numero: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
  }

  onLogin(): void {
    if (!this.loginForm.valid) {
      return;
    }

    this.isLoading.set(true);
    this.errMsg = '';

    const loginUsuario = new LoginUsuario(
      this.loginForm.get('numero')?.value,
      this.loginForm.get('password')?.value
    );

    this.authService.login(loginUsuario).subscribe(
      data => {
        this.tokenService.setToken(data.token);
        this.loadEmpresasAndContinue();
      },
      err => {
        this.isLoading.set(false);
        this.errMsg = err.error?.mensaje || err.error?.error || 'Error al iniciar sesión.';
      }
    );
  }

  private loadEmpresasAndContinue(): void {
    this.isLoadingEmpresas.set(true);

    this.authService.getEmpresasAuth().subscribe(
      empresas => {
        this.isLoadingEmpresas.set(false);
        this.isLoading.set(false);

        this.empresaOptions.set(empresas ?? []);

        // Caso robusto: sin empresas asignadas, continuar directamente
        if (this.empresaOptions().length === 0) {
          this.router.navigate(['/intranet']);
          return;
        }

        // Si solo hay una empresa, selección automática y salto al dashboard
        if (this.empresaOptions().length === 1) {
          this.applyEmpresaSelection(this.empresaOptions()[0].id);
          return;
        }

        // Si hay más de una, mostrar modal de selección
          this.selectedEmpresaId.set(null)
          this.showEmpresaSelectorModal.set(true)
          this.isLoading.set(false)
      },
      err => {
        this.isLoadingEmpresas.set(false);
        this.isLoading.set(false);
        this.errMsg = err.error?.mensaje || err.error?.error || 'No se pudieron obtener las empresas del usuario desde su perfil.';
      }
    );
  }

  onConfirmEmpresaSelection(): void {
    if (!this.selectedEmpresaId()) {
      return;
    }
    this.applyEmpresaSelection(this.selectedEmpresaId()!);
  }

  private applyEmpresaSelection(empresaId: number): void {
    this.isSelectingEmpresa.set(true);

    this.authService.authEmpresa(empresaId).subscribe(
      data => {
        this.isSelectingEmpresa.set(false);
        this.showEmpresaSelectorModal.set(false)
        this.tokenService.setToken(data.token);
        this.router.navigate(['/intranet']);
      },
      err => {
        this.isSelectingEmpresa.set(false);
        this.errMsg = err.error?.mensaje || err.error?.error || 'No se pudo seleccionar la empresa.';
      }
    );
  }

  clearError(): void {
    this.errMsg = '';
  }
}
