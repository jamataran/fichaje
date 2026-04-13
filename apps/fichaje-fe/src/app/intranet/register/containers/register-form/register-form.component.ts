import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NuevoUsuario } from 'src/app/core/auth/model/nuevo-usuario';
import { AuthService } from 'src/app/core/auth/service/auth.service';
import { TokenService } from 'src/app/core/auth/service/token.service';
import { SedeService, SedeDTO } from 'src/app/intranet/sedes/service/sede.service';
import { Popup } from 'src/app/shared/helper/popup';

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
  sedeSearchInput = ''
  selectedSedeId: number | null = null
  allSedes: SedeDTO[] = []
  filteredSedes: SedeDTO[] = []
  isLoadingSedes = false


  constructor(
    private service: AuthService,
    private tokenService: TokenService,
    private sedeService: SedeService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadSedes();
  }

  loadSedes(): void {
    this.isLoadingSedes = true;

    const usuarioId = this.tokenService.getId();

    this.sedeService.getSedesByUsuarioId(usuarioId).subscribe(
      (sedes: SedeDTO[]) => {
        this.allSedes = sedes;
        this.filteredSedes = sedes;
        this.isLoadingSedes = false;
        
        // Establecer la sede principal por defecto (primera sede)
        if (sedes && sedes.length > 0) {
          this.selectedSedeId = sedes[0].id;
          this.sedeSearchInput = sedes[0].nombre;
        }
      },
      (error) => {
        console.error('Error cargando sedes', error);
        Popup.toastDanger('Error', 'No se pudieron cargar las sedes');
        this.isLoadingSedes = false;
      }
    );
  }

  /**
   * Buscar sedes cuando el usuario presiona Enter
   */
  onSedeKeyPress(event: KeyboardEvent): void {
    // Solo buscar si presiona Enter
    if (event.key === 'Enter') {
      event.preventDefault();
      this.performSedeSearch();
    }
  }

  /**
   * Buscar sedes cuando el usuario hace click en el botón de búsqueda
   */
  performSedeSearch(): void {
    const searchTerm = this.sedeSearchInput;

    // Si el input está vacío, mostrar todas las sedes
    if (!searchTerm || searchTerm.trim() === '') {
      this.filteredSedes = this.allSedes;
      return;
    }

    // Filtrar sedes localmente (sin llamar al backend)
    this.filteredSedes = this.sedeService.filterSedes(this.allSedes, searchTerm);
  }

  selectSede(sede: SedeDTO): void {
    this.selectedSedeId = sede.id;
    this.sedeSearchInput = sede.nombre;  // ← Mostrar el nombre en el input
    this.filteredSedes = [];  // ← Cerrar el dropdown
  }

  clear(): void {
    this.numero = ''
    this.nombreEmpleado = ''
    this.email = ''
    this.dni = ''
    this.rol = ''
    this.sedeSearchInput = ''
    this.selectedSedeId = null
  }

  onRegister(): void {
    // La sede es opcional, pero si no hay sedes cargadas, mostrar error
    if (this.allSedes.length === 0) {
      Popup.toastDanger('Error', 'No hay sedes disponibles');
      return;
    }

    // Si no hay sede seleccionada, usar la primera (principal)
    if (!this.selectedSedeId) {
      this.selectedSedeId = this.allSedes[0].id;
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
