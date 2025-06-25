import { Component } from '@angular/core';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { ContentComponent } from './components/content/content.component';
import { HeaderComponent } from './components/header/header.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';

@Component({
  selector: 'app-layout',
  imports: [NzLayoutModule, SidebarComponent, HeaderComponent , ContentComponent],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent {
  isCollapsed = false;

  onToggle() {
    this.isCollapsed = !this.isCollapsed;
  }
}
