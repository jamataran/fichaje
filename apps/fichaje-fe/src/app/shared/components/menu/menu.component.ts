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
  isAdmin: boolean = false;
  canViewAdminPanel: boolean = false;
  canViewSystemPanel: boolean = false;
  numero:string='';


  constructor(
    private service: TokenService
  ) { }

  ngOnInit(): void {
    this.isRRHH = this.service.isRRHH();
    this.isAdmin = this.service.isAdmin();
    this.canViewAdminPanel = this.isRRHH || this.isAdmin;
    this.canViewSystemPanel = this.isAdmin;
    this.numero = this.service.getNumero();
  }


}
