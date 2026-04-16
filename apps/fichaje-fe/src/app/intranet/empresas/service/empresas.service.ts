import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Empresa, EmpresaCreate, EmpresaUpdate, SedeEmpresa } from '../model/empresa.model';

@Injectable({
  providedIn: 'root'
})
export class EmpresasService {

  endPoint = environment.apiURL + '/empresas';

  constructor(private httpClient: HttpClient) { }

  public getElements(page: number, size: number, order: string, asc: boolean): Observable<any> {
    const direction = asc ? 'asc' : 'desc';
    return this.httpClient.get<any>(this.endPoint + `?page=${page}&size=${size}&sort=${order},${direction}`);
  }

  public create(model: EmpresaCreate): Observable<Empresa> {
    return this.httpClient.post<Empresa>(this.endPoint, model);
  }

  public update(id: number, model: EmpresaUpdate): Observable<Empresa> {
    return this.httpClient.put<Empresa>(this.endPoint + `/${id}`, model);
  }

  public delete(id: number): Observable<any> {
    return this.httpClient.delete<any>(this.endPoint + `/${id}`);
  }

  public getSedesByEmpresa(id: number): Observable<SedeEmpresa[]> {
    return this.httpClient.get<SedeEmpresa[]>(environment.apiURL + `/sedes/empresa/${id}`);
  }
}
