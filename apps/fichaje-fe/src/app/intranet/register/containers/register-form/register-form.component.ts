import { Component, ElementRef, HostListener, OnInit, ViewChild, signal } from '@angular/core';
import { Router } from '@angular/router';
import { NuevoUsuario } from 'src/app/core/auth/model/nuevo-usuario';
import { AuthService } from 'src/app/core/auth/service/auth.service';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { Popup } from 'src/app/shared/helper/popup';
import { HttpClient } from "@angular/common/http";
import {environment } from "src/environments/environment";
import { NgZone } from '@angular/core';
import {EmpresasService} from "../../../empresas/service/empresas.service";

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

  numero = ''
  nombreEmpleado = ''
  email = ''
  dni = ''
  rol = ''
  empresaNombre = ''

  isAdmin = false;
  isRRHH = false;
  empresaSearchInput = '';
  selectedEmpresaId: number | null = null;
  allEmpresas: EmpresaDTO[] = [];
  filteredEmpresas: EmpresaDTO[] = [];
  showEmpresaDropdown = signal(false);
  isLoadingEmpresas = false;

  sedeSearchInput = '';
  selectedSedeId: number | null = null;
  allSedes: SedeDTO[] = [];
  filteredSedes: SedeDTO[] = [];
  showSedeDropdown = signal(false);
  isLoadingSedes = false;
  selectedSedeAddres: string = '';

  @ViewChild('empresaDropdownContainer') empresaDropdownContainer?: ElementRef<HTMLElement>;
  @ViewChild('sedeDropdownContainer') sedeDropdownContainer?: ElementRef<HTMLElement>;


  constructor(
    private service: AuthService,
    private tokenService: TokenService,
    private router: Router,
    private http: HttpClient,
    private ngZone: NgZone,
    private empresasService: EmpresasService
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.tokenService.isAdmin();
    this.isRRHH = this.tokenService.isRRHH();
    if (this.isAdmin){
      this.loadEmpresas();
    }

    if(this.isRRHH){
      const empresaId = this.tokenService.getEmpresaId()
      if (empresaId){
        this.selectedEmpresaId = empresaId
        this.loadEmpresaNombre()
        this.loadSede()
      }
    }
  }

  loadEmpresaNombre(): void{
    this.empresasService.getMiEmpresa().subscribe({
      next: (empresa) => {
        this.empresaNombre = empresa.nombre
        this.empresaSearchInput = empresa.nombre
      },
      error: () => {
        Popup.toastDanger('Error', 'No se pudo cargar la empresa')
      }
    })
  }

  loadSede(): void{
    this.isLoadingSedes = true
    this.empresasService.getMisSedes().subscribe({
      next: (sedes) => {
        this.allSedes = sedes
        this.filteredSedes = sedes
        this.isLoadingSedes = false
      },
      error: () => {
        Popup.toastDanger('Error', 'No se pudieron cargar las sedes')
        this.isLoadingSedes = false
      }
    })
  }

  loadEmpresas(): void {
    this.isLoadingEmpresas = true;

    this.http.get<any>(`${environment.apiURL}/empresas?page=0&size=200&sort=nombre,asc`)
      .subscribe({
        next: (response) => {
          this.ngZone.run(() => {   // ← envolver aquí
            this.allEmpresas = (response.content ?? response).map((e: any) => ({
              id: e.id,
              nombre: e.nombre,
              cif: e.cif,
              activa: e.activa
            }));
            this.filteredEmpresas = this.allEmpresas;
            this.isLoadingEmpresas = false;
          });
        },
        error: (error) => {
          this.ngZone.run(() => {   // ← y aquí
            Popup.toastDanger('Error', 'No se pudieron cargar las empresas');
            this.isLoadingEmpresas = false;
          });
        }
      });
  }

  onEmpresaInput(): void {
    const term = this.empresaSearchInput.trim().toLowerCase();
    this.filteredEmpresas = term
      ? this.allEmpresas.filter(e => e.nombre.toLowerCase().includes(term))
      : this.allEmpresas;
  this.showEmpresaDropdown.set(true);

    this.selectedEmpresaId = null;
    this.resetSedes();
  }

  onEmpresaFocus(): void {
    if (!this.empresaSearchInput.trim()) {
      this.filteredEmpresas = this.allEmpresas;
    }
    this.showEmpresaDropdown.set(true);
  }

  selectEmpresa(empresa: EmpresaDTO): void {
    this.selectedEmpresaId = empresa.id;
    this.empresaSearchInput = empresa.nombre;
    this.showEmpresaDropdown.set(false);
    this.loadSedesByEmpresa(empresa.id);
  }

  closeEmpresaDropdown(): void {
    setTimeout(() => {
      this.showEmpresaDropdown.set(false);
    }, 200);
  }

  loadSedesByEmpresa(empresaId: number): void {
    this.isLoadingSedes = true;
    this.http.get<SedeDTO[]>(`${environment.apiURL}/sedes/empresa/${empresaId}`)
      .subscribe({
        next: (sedes) => {
          this.ngZone.run(() => {
            this.allSedes = sedes ?? [];
            this.filteredSedes = this.allSedes;
            this.isLoadingSedes = false;

            this.selectedSedeId = null;
            this.sedeSearchInput = '';
          });
        },
        error: () => {
          this.ngZone.run(() => {
            this.resetSedes();
            this.isLoadingSedes = false;
            Popup.toastDanger('Error', 'No se pudieron cargar las sedes de la empresa seleccionada');
          });
        }
      });
  }

  onSedeInput(): void {
    const term = this.sedeSearchInput.trim().toLowerCase();
    this.filteredSedes = term
      ? this.allSedes.filter(s => s.nombre.toLowerCase().includes(term))
      : this.allSedes;
    this.showSedeDropdown.set(true);
    this.selectedSedeId = null;
  }

  onSedeFocus(): void {
    if (!this.sedeSearchInput.trim()) {
      this.filteredSedes = this.allSedes;
    }
    this.showSedeDropdown.set(true);
  }

  selectSede(sede: SedeDTO): void {
    this.selectedSedeId = sede.id;
    this.sedeSearchInput = sede.nombre;
    this.selectedSedeAddres = sede.direccion;
    this.showSedeDropdown.set(false);
  }

  closeSedeDropdown(): void {
    setTimeout(() => {
      this.showSedeDropdown.set(false);
    }, 200);
  }

  private resetSedes(): void {
    this.sedeSearchInput = '';
    this.selectedSedeId = null;
    this.allSedes = [];
    this.filteredSedes = [];
    this.showSedeDropdown.set(false);
    this.selectedSedeAddres = ''
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
    this.numero = ''
    this.nombreEmpleado = ''
    this.email = ''
    this.dni = ''
    this.rol = ''
    this.empresaSearchInput = ''
    this.selectedEmpresaId = null
    this.resetSedes()
  }

  onRegister(): void {

    if (!this.selectedEmpresaId) {
      Popup.toastDanger('Error', 'Debes seleccionar una empresa válida');
      return;
    }

    if (!this.selectedSedeId) {
      Popup.toastDanger('Error', 'Debes seleccionar una sede de la empresa elegida');
      return;
    }

    let roles = [this.rol];

    let nuevoUsuario = new NuevoUsuario(
      this.numero,
      this.nombreEmpleado,
      this.email,
      this.dni,
      roles,
      this.selectedSedeId
    );

    this.service.nuevo(nuevoUsuario).subscribe(
      data => {
        Popup.toastSucess('', data.mensaje);
        this.router.navigate([`intranet/empleados`])
      },
      err => {
        console.log(err)
        Popup.toastDanger('Error', err.error.mensaje);
      }
    )
  }

}
