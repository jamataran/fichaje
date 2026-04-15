import { HttpClient } from '@angular/common/http';
import { Injectable, SkipSelf } from '@angular/core';
import { Observable } from 'rxjs';
import { JwtDTO } from '../model/jwt-dto';
import { LoginUsuario } from '../model/login-usuario';
import { NuevoUsuario } from '../model/nuevo-usuario';
import { environment } from 'src/environments/environment';

export interface EmpresaAuthOption {
  id: number;
  nombre: string;
  cif?: string;
  razonSocial?: string;
  activa?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  //endPoint = 'http://localhost:8080/auth'
  endPoint = environment.apiURL + '/auth';

  constructor(private httpClient: HttpClient) { }

  public nuevo(nuevoUsuario: NuevoUsuario): Observable<any> {
    return this.httpClient.post<any>(this.endPoint + "/nuevo", nuevoUsuario)
  }
  public login(loginUsuario: LoginUsuario): Observable<any> {
    return this.httpClient.post<JwtDTO>(this.endPoint + "/login", loginUsuario)
  }

  public getEmpresasAuth(): Observable<EmpresaAuthOption[]> {
    return this.httpClient.get<EmpresaAuthOption[]>(this.endPoint + '/empresas')
  }

  public authEmpresa(empresaId: number): Observable<JwtDTO> {
    return this.httpClient.post<JwtDTO>(this.endPoint + '/empresa', { empresaId })
  }




}
