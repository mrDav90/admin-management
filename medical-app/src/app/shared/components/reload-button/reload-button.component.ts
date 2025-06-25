import { Component, EventEmitter, Output } from '@angular/core';
import { NzButtonModule } from "ng-zorro-antd/button";
import { NzIconModule } from "ng-zorro-antd/icon";
@Component({
  selector: 'app-reload-button',
  imports: [ NzButtonModule, NzIconModule ],
  templateUrl: './reload-button.component.html',
  styleUrl: './reload-button.component.css'
})
export class ReloadButtonComponent {
  @Output() _onRefresh = new EventEmitter();

  onRefresh(){
    this._onRefresh.emit();
  }
}
