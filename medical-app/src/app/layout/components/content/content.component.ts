import { Component, Input } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NzContentComponent } from 'ng-zorro-antd/layout';
import { BreadcrumbsComponent } from '../breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-content',
  standalone : true,
  imports: [NzContentComponent, RouterOutlet, BreadcrumbsComponent],
  templateUrl: './content.component.html',
  styleUrl: './content.component.css'
})
export class ContentComponent {
  @Input() isCollapsed : boolean = false;
}
