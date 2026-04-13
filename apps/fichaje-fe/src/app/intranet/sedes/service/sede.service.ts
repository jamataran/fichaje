import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface SedeDTO {
  id: number;
  nombre: string;
  [key: string]: any; // Otros campos
}

@Injectable({
  providedIn: 'root'
})
export class SedeService {

  constructor(private httpClient: HttpClient) { }

  public getSedesByUsuarioId(usuarioId: number): Observable<SedeDTO[]> {
    return this.httpClient.get<SedeDTO[]>(
      environment.apiURL + `/usuario/${usuarioId}/sedes`
    );
  }

  public filterSedes(sedes: SedeDTO[], searchTerm: string): SedeDTO[] {
    if (!searchTerm || searchTerm.trim() === '') {
      return sedes;
    }

    const term = searchTerm.toLowerCase().trim();

    return sedes.filter(sede =>
      sede.nombre.toLowerCase().includes(term)
    );
  }
}
