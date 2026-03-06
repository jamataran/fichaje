import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'app-public',
    template: '<router-outlet></router-outlet>',
    styles: [''],
    standalone: false
})
export class PublicComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
