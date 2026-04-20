import { Component } from '@angular/core';
import { PopupBridgeService } from './shared/helper/popup-bridge.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    standalone: false
})
export class AppComponent {
  title = 'angular-fichajesPi';

  constructor(private popupBridge: PopupBridgeService) {}
}
