import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { FichajeDto } from '../../fichajes/model/fichajeDto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private readonly httpClient = inject(HttpClient);
  private readonly endPoint = environment.apiURL + '/fichaje';

  public now(dto: FichajeDto): Observable<any> {
    return this.httpClient.post<any>(`${this.endPoint}/now`, dto);
  }
}
