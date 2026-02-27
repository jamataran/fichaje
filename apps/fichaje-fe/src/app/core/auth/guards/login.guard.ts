import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { TokenService } from '../service/token.service';

@Injectable({
  providedIn: 'root'
})
export class LoginGuard  {

  constructor(
    private tokenService: TokenService,
    private router: Router
  ) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.tokenService.isLogged()) {
      this.router.navigate(['/intranet'])
      return false
    }
    return true;
  }

}
