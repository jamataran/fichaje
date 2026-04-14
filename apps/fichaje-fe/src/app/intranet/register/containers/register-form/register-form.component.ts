import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NuevoUsuario } from 'src/app/core/auth/model/nuevo-usuario';
import { AuthService } from 'src/app/core/auth/service/auth.service';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { Popup } from 'src/app/shared/helper/popup';
import { HttpClient } from "@angular/common/http";
import {environment } from "src/environments/environment";
import { NgZone } from '@angular/core';

export interface EmpresaDTO {
  id: number;
  nombre: string;
  cif: string;
  activa: boolean;
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

  isAdmin = false;
  empresaSearchInput = '';
  selectedEmpresaId: number | null = null;
  allEmpresas: EmpresaDTO[] = [];
  filteredEmpresas: EmpresaDTO[] = [];
  showEmpresaDropdown = false;
  isLoadingEmpresas = false;


  constructor(
    private service: AuthService,
    private tokenService: TokenService,
    private router: Router,
    private http: HttpClient,
    private ngZone: NgZone
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.tokenService.isAdmin();
    if (this.isAdmin){
      this.loadEmpresas();
    }
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
    this.showEmpresaDropdown = true;
    if (!this.empresaSearchInput.trim()) {
      this.selectedEmpresaId = null;
    }
  }

  onEmpresaFocus(): void {
    if (!this.empresaSearchInput.trim()) {
      this.filteredEmpresas = this.allEmpresas;
    }
    this.showEmpresaDropdown = true;
  }

  selectEmpresa(empresa: EmpresaDTO): void {
    this.selectedEmpresaId = empresa.id;
    this.empresaSearchInput = empresa.nombre;
    this.showEmpresaDropdown = false;
  }

  closeEmpresaDropdown(): void {
    setTimeout(() => {
      this.showEmpresaDropdown = false;
    }, 200);
  }

  clear(): void {
    this.numero = ''
    this.nombreEmpleado = ''
    this.email = ''
    this.dni = ''
    this.rol = ''
  }

  onRegister(): void {

    let roles = [this.rol];

    let nuevoUsuario = new NuevoUsuario(
      this.numero,
      this.nombreEmpleado,
      this.email,
      this.dni,
      roles
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
