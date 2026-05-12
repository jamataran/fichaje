import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { DataCsv } from 'src/app/shared/interfaces/dataCsv';
import { NuevasVacaciones } from '../models/nuevasVacaciones';
import { Vacaciones } from '../models/vacaciones';
import { VacacionesDto } from '../models/vacacionesDto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VacacionesService implements DataCsv {
  private readonly httpClient = inject(HttpClient);
  private readonly endPoint = `${environment.apiURL}/vacaciones`;

  getElements(
    dto: VacacionesDto,
    page: number,
    size: number,
    order: string,
    asc: boolean
  ): Observable<any> {
    const params = `?page=${page}&size=${size}&order=${order}&asc=${asc}`;
    return this.httpClient.post<any>(`${this.endPoint}/pagesFiltered${params}`, dto);
  }

  public detail(id: number): Observable<Vacaciones> {
    return this.httpClient.get<Vacaciones>(`${this.endPoint}/${id}`);
  }

  public delete(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.endPoint}/${id}`);
  }

  public aprobar(id: number): Observable<any> {
    return this.httpClient.put<any>(`${this.endPoint}/aprobar/${id}`, null);
  }

  public denegar(id: number): Observable<any> {
    return this.httpClient.put<any>(`${this.endPoint}/denegar/${id}`, null);
  }

  getCsvData(dto: VacacionesDto): Observable<any> {
    return this.httpClient.post<any[]>(`${this.endPoint}/listFiltered`, dto);
  }

  public create(vacaciones: NuevasVacaciones): Observable<any> {
    return this.httpClient.post<any>(`${this.endPoint}/create`, vacaciones);
  }
}
