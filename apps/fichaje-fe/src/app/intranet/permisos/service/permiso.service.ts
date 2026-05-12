import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChartDataService } from 'src/app/shared/interfaces/ChartDataService';
import { DataCsv } from 'src/app/shared/interfaces/dataCsv';
import { NuevoPermiso } from '../model/nuevoPermiso';
import { Permiso } from '../model/permiso';
import { PermisoDto } from '../model/permisoDto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PermisoService implements DataCsv, ChartDataService {

  private readonly http = inject(HttpClient);
  private readonly endPoint = `${environment.apiURL}/permiso`;

  getElements(
    dto: PermisoDto,
    page: number,
    size: number,
    order: string,
    asc: boolean
  ): Observable<any> {
    const payload = Object.fromEntries(
      Object.entries(dto).filter(([_, v]) => v !== '' && v !== null && v !== undefined)
    );

    const params = {
      page: page.toString(),
      size: size.toString(),
      order,
      asc: asc.toString()
    };

    return this.http.post<any>(`${this.endPoint}/pagesFiltered`, payload, { params });
  }

  detail(id: number): Observable<Permiso> {
    return this.http.get<Permiso>(`${this.endPoint}/${id}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endPoint}/${id}`);
  }

  aprobar(id: number): Observable<Permiso> {
    return this.http.put<Permiso>(`${this.endPoint}/aprobar/${id}`, null);
  }

  denegar(id: number): Observable<Permiso> {
    return this.http.put<Permiso>(`${this.endPoint}/denegar/${id}`, null);
  }

  getCsvData(dto: PermisoDto): Observable<any[]> {
    return this.http.post<any[]>(`${this.endPoint}/listFiltered`, dto);
  }

  create(permiso: NuevoPermiso): Observable<Permiso> {
    return this.http.post<Permiso>(`${this.endPoint}/create`, permiso);
  }

  getChartData(): Observable<any[]> {
    return this.http.get<any[]>(`${this.endPoint}/count`);
  }

  getUserTableData(): Observable<any[]> {
    return this.http.get<any[]>(`${this.endPoint}/count/users`);
  }
}
