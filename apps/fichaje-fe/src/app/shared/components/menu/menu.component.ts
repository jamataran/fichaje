import { Component, OnInit } from '@angular/core';
import { TokenService } from 'src/app/core/auth/service/token.service';

@Component({
    selector: 'app-menu',
    templateUrl: './menu.component.html',
    styleUrls: ['./menu.component.css'],
    standalone: false
})
export class MenuComponent implements OnInit {


  isRRHH: boolean = false;
  isSuperAdmin: boolean = false;
  isAdmin: boolean = false;
  canViewAdminPanel: boolean = false;
  canViewSuperAdminPanel: boolean = false;
  numero:string='';


  constructor(
    private service: TokenService
  ) { }

  ngOnInit(): void {
    this.isRRHH = this.service.isRRHH();
    this.isAdmin = this.service.isAdmin();
    this.isSuperAdmin = this.service.isSuperAdmin();
    this.canViewAdminPanel = this.isSuperAdmin || this.isAdmin || this.isRRHH;
    this.canViewSuperAdminPanel = this.isAdmin || this.isSuperAdmin;
    this.numero = this.service.getNumero();
  }


}
