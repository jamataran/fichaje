import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Sede, SedeCreate, SedeUpdate, SedeParametro, SedeParametroCreate } from '../model/sede.model';
import { EmpleadosService } from '../../empleados/service/empleados.service';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SedesService {

  endPoint = environment.apiURL + '/sedes';

  constructor(
    private httpClient: HttpClient,
    private empleadosService: EmpleadosService
  ) { }

  public getMisSedes(): Observable<Sede[]> {
    return this.empleadosService.getMyUsuario().pipe(
      map(usuario => usuario.sedes ?? [])
    );
  }

  public getSedesByEmpresa(empresaId: number): Observable<Sede[]> {
    return this.httpClient.get<Sede[]>(environment.apiURL + `/empresas/${empresaId}/sedes`);
  }

  public create(empresaId: number, model: SedeCreate): Observable<Sede> {
    return this.httpClient.post<Sede>(environment.apiURL + `/empresas/${empresaId}/sedes`, model);
  }

  public update(id: number, model: SedeUpdate): Observable<Sede> {
    return this.httpClient.put<Sede>(this.endPoint + `/${id}`, model);
  }

  public deactivate(id: number): Observable<any> {
    return this.httpClient.patch<any>(this.endPoint + `/${id}/desactivar`, {});
  }

  public activate(id: number): Observable<any> {
    return this.httpClient.patch<any>(this.endPoint + `/${id}/activar`, {});
  }

  // Parameter methods
  public listParametros(sedeId: number): Observable<SedeParametro[]> {
    return this.httpClient.get<SedeParametro[]>(this.endPoint + `/${sedeId}/parametros`);
  }

  public addParametro(sedeId: number, model: SedeParametroCreate): Observable<SedeParametro> {
    return this.httpClient.post<SedeParametro>(this.endPoint + `/${sedeId}/parametros`, model);
  }

  public updateParametro(sedeId: number, parametroId: number, model: SedeParametroCreate): Observable<SedeParametro> {
    return this.httpClient.put<SedeParametro>(this.endPoint + `/${sedeId}/parametros/${parametroId}`, model);
  }

  public deleteParametro(sedeId: number, parametroId: number): Observable<any> {
    return this.httpClient.delete<any>(this.endPoint + `/${sedeId}/parametros/${parametroId}`);
  }
}
