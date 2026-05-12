import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from 'src/app/core/auth/service/token.service';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css'],
    standalone: false
})
export class HeaderComponent implements OnInit {

  isLogged = false;
  isAdmin = false;
  nombre = '';
  showMobileMenu = false;

  constructor(
    private tokenService: TokenService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.isLogged = this.tokenService.isLogged();
    this.isAdmin = this.tokenService.isRRHH();
    this.nombre = this.tokenService.getNombre();
  }

  onLogOut(): void {
    this.tokenService.logOut();
    this.router.navigate(['/']);
    window.location.reload();
  }

  toggleMobileMenu(): void {
    this.showMobileMenu = !this.showMobileMenu;
  }
}
