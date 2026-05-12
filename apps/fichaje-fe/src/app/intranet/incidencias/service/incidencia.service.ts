import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChartDataService } from 'src/app/shared/interfaces/ChartDataService';
import { DataCsv } from 'src/app/shared/interfaces/dataCsv';
import { Incidencia } from '../model/incidencia';
import { IncidenciaDto } from '../model/incidenciaDto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class IncidenciaService implements DataCsv, ChartDataService {
  private readonly http = inject(HttpClient);
  private readonly endPoint = `${environment.apiURL}/incidencia`;

  getElements(
    dto: IncidenciaDto,
    page: number,
    size: number,
    order: string,
    asc: boolean
  ): Observable<any> {
    const params = {
      page: page.toString(),
      size: size.toString(),
      order,
      asc: asc.toString()
    };
    return this.http.post<any>(`${this.endPoint}/pagesFiltered`, dto, { params });
  }

  detail(id: number): Observable<Incidencia> {
    return this.http.get<Incidencia>(`${this.endPoint}/${id}`);
  }

  update(id: number, model: Incidencia): Observable<any> {
    return this.http.put<any>(`${this.endPoint}/${id}`, model);
  }

  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.endPoint}/${id}`);
  }

  getCsvData(dto: IncidenciaDto): Observable<any> {
    return this.http.post<any[]>(`${this.endPoint}/listFiltered`, dto);
  }

  getChartData(): Observable<any> {
    return this.http.get<any[]>(`${this.endPoint}/count`);
  }

  getUserTableData(): Observable<any> {
    return this.http.get<any[]>(`${this.endPoint}/count/users`);
  }

  getRankingIncidencias(): Observable<any> {
    return this.http.get<any[]>(`${this.endPoint}/count/top`);
  }
}
