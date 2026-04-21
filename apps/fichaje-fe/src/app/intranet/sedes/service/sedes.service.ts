import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Sede, SedeCreate, SedeUpdate } from '../model/sede.model';

@Injectable({
  providedIn: 'root'
})
export class SedesService {

  endPoint = environment.apiURL + '/sedes';

  constructor(private httpClient: HttpClient) { }

  public getMisSedes(): Observable<Sede[]> {
    return this.httpClient.get<Sede[]>(this.endPoint + `/mis-sedes`);
  }

  public create(empresaId: number, model: SedeCreate): Observable<Sede> {
    return this.httpClient.post<Sede>(this.endPoint + `/empresa/${empresaId}`, model);
  }

  public update(id: number, model: SedeUpdate): Observable<Sede> {
    return this.httpClient.put<Sede>(this.endPoint + `/${id}`, model);
  }

  public deactivate(id: number): Observable<any> {
    return this.httpClient.patch<any>(this.endPoint + `/${id}/desactivar`, {});
  }
}
