import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { TokenService } from '../service/token.service';

@Injectable({
  providedIn: 'root'
})
export class GuardService  {

  constructor(
    private tokenService: TokenService,
    private router: Router
  ) { }

  canActivate(route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {

    const expectedRoles: string[] = route.data.expectedRol || [];

    if (!this.tokenService.isLogged()) {
      this.router.navigate(['/']);
      return false;
    }

    if (expectedRoles.length === 0) {
      return this.checkEmpresaAccess();
    }

    const userRoles: string[] = [];
    if (this.tokenService.isAdmin()) {
      userRoles.push('admin');
    }
    if (this.tokenService.isRRHH()) {
      userRoles.push('rrhh');
    }
    if (userRoles.length === 0) {
      userRoles.push('user');
    }

    const hasRole = expectedRoles.some(role => userRoles.includes(role));

    if (!hasRole) {
      this.router.navigate(['/intranet/home']); // Redirigir a home en lugar de fuera para evitar bucles
      return false;
    }

    return this.checkEmpresaAccess();
  }

  private checkEmpresaAccess(): boolean {
    if(!this.tokenService.isAdmin() && !this.tokenService.getEmpresaId()){
      this.router.navigate(['/public/landing/home']);
      return false;
    }
    return true;
  }
}
