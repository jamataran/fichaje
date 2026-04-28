import { Component, ElementRef, HostListener, OnInit, ViewChild, signal, inject, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NuevoUsuario } from 'src/app/core/auth/model/nuevo-usuario';
import { AuthService } from 'src/app/core/auth/service/auth.service';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { Popup } from 'src/app/shared/helper/popup';
import { HttpClient } from "@angular/common/http";
import { environment } from "src/environments/environment";
import { EmpresasService } from "../../../empresas/service/empresas.service";
import { EmpleadosService } from 'src/app/intranet/empleados/service/empleados.service';

export interface EmpresaDTO {
  id: number;
  nombre: string;
  cif: string;
  activa: boolean;
}

export interface SedeDTO {
  id: number;
  nombre: string;
  direccion: string;
}

@Component({
    selector: 'app-register-form',
    templateUrl: './register-form.component.html',
    styleUrls: ['./register-form.component.css'],
    standalone: false
})
export class RegisterFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(AuthService);
  private tokenService = inject(TokenService);
  private router = inject(Router);
  private http = inject(HttpClient);
  private ngZone = inject(NgZone);
  private empresasService = inject(EmpresasService);
  private empleadosService = inject(EmpleadosService);

  registerForm!: FormGroup;

  isSuperAdmin = signal(false);
  isRRHH = signal(false);
  isAdmin = signal(false);

  selectedEmpresaId = signal<number | null>(null);
  allEmpresas = signal<EmpresaDTO[]>([]);
  filteredEmpresas = signal<EmpresaDTO[]>([]);
  showEmpresaDropdown = signal(false);
  isLoadingEmpresas = signal(false);

  selectedSedeId = signal<number | null>(null);
  allSedes = signal<SedeDTO[]>([]);
  filteredSedes = signal<SedeDTO[]>([]);
  showSedeDropdown = signal(false);
  isLoadingSedes = signal(false);
  selectedSedeAddress = signal('');

  @ViewChild('empresaDropdownContainer') empresaDropdownContainer?: ElementRef<HTMLElement>;
  @ViewChild('sedeDropdownContainer') sedeDropdownContainer?: ElementRef<HTMLElement>;

  ngOnInit(): void {
    this.isSuperAdmin.set(this.tokenService.isSuperAdmin());
    this.isRRHH.set(this.tokenService.isRRHH());
    this.isAdmin.set(this.tokenService.isAdmin());
    this.initForm();

    if (this.isSuperAdmin()){
      this.loadEmpresas();
    }

    const empresaId = this.tokenService.getEmpresaId()
    if (empresaId){
      this.selectedEmpresaId.set(empresaId);
      this.loadEmpresaNombre();
      this.loadSede(empresaId);
    }
  }

  private initForm(): void {
    this.registerForm = this.fb.group({
      numero: ['', Validators.required],
      nombreEmpleado: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      dni: ['', [Validators.required, Validators.pattern(/^[XYZxyz0-9]{1}[0-9]{7,7}[A-Za-z]$/)]],
      rol: ['user'],
      empresaSearchInput: [''],
      sedeSearchInput: ['']
    });

    if (this.isRRHH() && !this.isSuperAdmin()) {
      this.registerForm.get('empresaSearchInput')?.disable();
    }
  }

  loadEmpresaNombre(): void{
    this.empleadosService.getMyUsuario().subscribe({
      next: (usuario) => {
        if (usuario.empresas && usuario.empresas.length > 0) {
          const empresa = usuario.empresas[0];
          this.registerForm.patchValue({
            empresaSearchInput: empresa.nombre
          });
        }
      },
      error: () => {
        Popup.toastDanger('Error', 'No se pudo cargar la empresa desde el perfil');
      }
    });
  }

  loadSede(empresaId: number): void{
    this.isLoadingSedes.set(true);
    this.empresasService.getMisSedes(empresaId).subscribe({
      next: (sedes) => {
        this.allSedes.set(sedes);
        this.filteredSedes.set(sedes);
        this.isLoadingSedes.set(false);
        if (sedes && sedes.length > 0) {
          this.selectSede(sedes[0]);
        }
      },
      error: () => {
        Popup.toastDanger('Error', 'No se pudieron cargar las sedes');
        this.isLoadingSedes.set(false);
      }
    })
  }

  loadEmpresas(): void {
    this.isLoadingEmpresas.set(true);

    this.http.get<any>(`${environment.apiURL}/empresas?page=0&size=200&sort=nombre,asc`)
      .subscribe({
        next: (response) => {
          this.ngZone.run(() => {
            const mappedEmpresas = (response.content ?? response).map((e: any) => ({
              id: e.id,
              nombre: e.nombre,
              cif: e.cif,
              activa: e.activa
            }));
            this.allEmpresas.set(mappedEmpresas);
            this.filteredEmpresas.set(mappedEmpresas);
            this.isLoadingEmpresas.set(false);
          });
        },
        error: () => {
          this.ngZone.run(() => {
            Popup.toastDanger('Error', 'No se pudieron cargar las empresas');
            this.isLoadingEmpresas.set(false);
          });
        }
      });
  }

  onEmpresaInput(): void {
    const term = this.registerForm.get('empresaSearchInput')?.value?.trim().toLowerCase() || '';
    const empresas = this.allEmpresas();
    this.filteredEmpresas.set(term
      ? empresas.filter(e => e.nombre.toLowerCase().includes(term))
      : empresas);
    this.showEmpresaDropdown.set(true);

    this.selectedEmpresaId.set(null);
    this.resetSedes();
  }

  onEmpresaFocus(): void {
    const term = this.registerForm.get('empresaSearchInput')?.value?.trim() || '';
    if (!term) {
      this.filteredEmpresas.set(this.allEmpresas());
    }
    this.showEmpresaDropdown.set(true);
  }

  selectEmpresa(empresa: EmpresaDTO): void {
    this.selectedEmpresaId.set(empresa.id);
    this.registerForm.patchValue({
      empresaSearchInput: empresa.nombre
    });
    this.showEmpresaDropdown.set(false);
    this.loadSedesByEmpresa(empresa.id);
  }

  closeEmpresaDropdown(): void {
    setTimeout(() => {
      this.showEmpresaDropdown.set(false);
    }, 200);
  }

  loadSedesByEmpresa(empresaId: number): void {
    this.isLoadingSedes.set(true);
    this.http.get<SedeDTO[]>(`${environment.apiURL}/empresas/${empresaId}/sedes`)
      .subscribe({
        next: (sedes) => {
          this.ngZone.run(() => {
            const resultSedes = sedes ?? [];
            this.allSedes.set(resultSedes);
            this.filteredSedes.set(resultSedes);
            this.isLoadingSedes.set(false);

            this.selectedSedeId.set(null);
            this.registerForm.patchValue({
              sedeSearchInput: ''
            });
          });
        },
        error: () => {
          this.ngZone.run(() => {
            this.resetSedes();
            this.isLoadingSedes.set(false);
            Popup.toastDanger('Error', 'No se pudieron cargar las sedes de la empresa seleccionada');
          });
        }
      });
  }

  onSedeInput(): void {
    const term = this.registerForm.get('sedeSearchInput')?.value?.trim().toLowerCase() || '';
    const sedes = this.allSedes();
    this.filteredSedes.set(term
      ? sedes.filter(s => s.nombre.toLowerCase().includes(term))
      : sedes);
    this.showSedeDropdown.set(true);
    this.selectedSedeId.set(null);
  }

  onSedeFocus(): void {
    const term = this.registerForm.get('sedeSearchInput')?.value?.trim() || '';
    if (!term) {
      this.filteredSedes.set(this.allSedes());
    }
    this.showSedeDropdown.set(true);
  }

  selectSede(sede: SedeDTO): void {
    this.selectedSedeId.set(sede.id);
    this.registerForm.patchValue({
      sedeSearchInput: sede.nombre
    });
    this.selectedSedeAddress.set(sede.direccion);
    this.showSedeDropdown.set(false);
  }

  closeSedeDropdown(): void {
    setTimeout(() => {
      this.showSedeDropdown.set(false);
    }, 200);
  }

  private resetSedes(): void {
    this.registerForm.patchValue({
      sedeSearchInput: ''
    });
    this.selectedSedeId.set(null);
    this.allSedes.set([]);
    this.filteredSedes.set([]);
    this.showSedeDropdown.set(false);
    this.selectedSedeAddress.set('');
  }

  @HostListener('document:pointerdown', ['$event'])
  onDocumentPointerDown(event: PointerEvent): void {
    const target = event.target as Node | null;
    if (!target) {
      return;
    }

    if (this.showEmpresaDropdown()) {
      const insideEmpresa = this.empresaDropdownContainer?.nativeElement.contains(target) ?? false;
      if (!insideEmpresa) {
        this.showEmpresaDropdown.set(false);
      }
    }

    if (this.showSedeDropdown()) {
      const insideSede = this.sedeDropdownContainer?.nativeElement.contains(target) ?? false;
      if (!insideSede) {
        this.showSedeDropdown.set(false);
      }
    }
  }

  clear(): void {
    this.registerForm.reset({
      numero: '',
      nombreEmpleado: '',
      email: '',
      dni: '',
      rol: '',
      empresaSearchInput: '',
      sedeSearchInput: ''
    });
    this.selectedEmpresaId.set(null);
    this.resetSedes();
  }

  onRegister(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const sedeId = this.selectedSedeId();
    const empresaId = this.selectedEmpresaId();

    if (!empresaId) {
      Popup.toastDanger('Error', 'Debes seleccionar una empresa válida');
      return;
    }

    if (!sedeId) {
      Popup.toastDanger('Error', 'Debes seleccionar una sede de la empresa elegida');
      return;
    }

    const { numero, nombreEmpleado, email, dni, rol } = this.registerForm.value;
    let roles: string[] = [];

    if (rol === 'admin') {
      roles = ['admin', 'rrhh']; // El backend añadirá 'user' por defecto
    } else if (rol === 'rrhh') {
      roles = ['rrhh']; // El backend añadirá 'user' por defecto
    } else {
      roles = []; // El backend añadirá 'user' por defecto
    }

    let nuevoUsuario = new NuevoUsuario(
      numero,
      nombreEmpleado,
      email,
      dni,
      roles,
      sedeId
    );

    this.service.nuevo(nuevoUsuario).subscribe({
      next: (data) => {
        Popup.toastSucess('', data.mensaje);
        this.router.navigate([`intranet/empleados`])
      },
      error: (err) => {
        console.error(err);
        Popup.toastDanger('Error', err.error?.mensaje || 'Error al crear el empleado');
      }
    });
  }
}
