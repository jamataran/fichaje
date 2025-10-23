import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginUsuario } from 'src/app/core/auth/model/login-usuario';
import { AuthService } from 'src/app/core/auth/service/auth.service';
import { TokenService } from 'src/app/core/auth/service/token.service';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {

  loginForm: FormGroup;
  errMsg: string = '';
  isLoading: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private tokenService: TokenService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      numero: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
  }

  onLogin(): void {
    if (!this.loginForm.valid) {
      return;
    }

    this.isLoading = true;
    this.errMsg = '';

    const loginUsuario = new LoginUsuario(
      this.loginForm.get('numero')?.value,
      this.loginForm.get('password')?.value
    );

    this.authService.login(loginUsuario).subscribe(
      data => {
        this.isLoading = false;
        this.tokenService.setToken(data.token);
        this.router.navigate(['/intranet']);
      },
      err => {
        this.isLoading = false;
        this.errMsg = err.error?.error || 'Error al iniciar sesión. Intenta nuevamente.';
      }
    );
  }

  clearError(): void {
    this.errMsg = '';
  }
}
