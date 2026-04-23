import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DataCsv } from 'src/app/shared/interfaces/dataCsv';
import { Password } from '../../home/models/password';
import { Empleado } from '../model/empleado';
import { EmpleadoDto } from '../model/empleadoDto'
import { environment } from 'src/environments/environment';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EmpleadosService implements DataCsv {

  endPoint = environment.apiURL + '/usuario';

  constructor(private httpClient: HttpClient) { }

  /**
   * Obtiene una lista paginada y filtrada de usuarios usando el punto único GET /usuario
   */
  getElements(
    dto: EmpleadoDto,
    page: number,
    size: number,
    order: string,
    asc: boolean): Observable<any> {
    
    const sort = `${order},${asc ? 'asc' : 'desc'}`;
    let params = `?page=${page}&size=${size}&sort=${sort}`;
    params = this.addFilterParams(params, dto);

    return this.httpClient.get<any>(this.endPoint + params);
  }

  /**
   * Obtiene la lista completa para exportación CSV
   */
  getCsvData(dto: EmpleadoDto): Observable<any> {
    // Para CSV pedimos un tamaño muy grande para simular una lista completa desde el endpoint paginado
    let params = `?size=10000`;
    params = this.addFilterParams(params, dto);

    return this.httpClient.get<any>(this.endPoint + params).pipe(
      map(response => response.content) // Extraemos solo el array de usuarios para el CSV
    );
  }

  /**
   * Método de utilidad para añadir parámetros de filtrado a la URL
   */
  private addFilterParams(currentParams: string, dto: EmpleadoDto): string {
    let params = currentParams;
    if (dto.email) params += `&email=${dto.email}`;
    if (dto.numero) params += `&numero=${dto.numero}`;
    if (dto.nombreEmpleado) params += `&nombreEmpleado=${dto.nombreEmpleado}`;
    if (dto.dni) params += `&dni=${dto.dni}`;
    if (dto.empresaId) params += `&empresaId=${dto.empresaId}`;
    if (dto.sedeId) params += `&sedeId=${dto.sedeId}`;
    if (dto.working !== undefined && dto.working !== null) params += `&working=${dto.working}`;
    if (dto.enVacaciones !== undefined && dto.enVacaciones !== null) params += `&enVacaciones=${dto.enVacaciones}`;
    if (dto.deBaja !== undefined && dto.deBaja !== null) params += `&deBaja=${dto.deBaja}`;
    return params;
  }

  public detail(id: number): Observable<Empleado> {
    return this.httpClient.get<Empleado>(this.endPoint + `/${id}`)
  }

  public update(id: number, model: Empleado): Observable<any> {
    return this.httpClient.put<any>(this.endPoint + `/${id}`, model)
  }

  public delete(id: number): Observable<any> {
    return this.httpClient.delete<any>(this.endPoint + `/${id}`)
  }

  public getMyUsuario(): Observable<Empleado> {
    return this.httpClient.get<Empleado>(this.endPoint + '/miusuario')
  }

  public changePassword(id: number, model: Password): Observable<any> {
    return this.httpClient.put<any>(this.endPoint + `/password/${id}`, model)
  }
}
