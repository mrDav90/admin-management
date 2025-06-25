import { Component, inject, Input, OnInit, OnDestroy } from '@angular/core';
import { MenuItem } from '../sidebar.component';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { Subscription, filter } from 'rxjs';

@Component({
  selector: 'app-menu-item',
  standalone: true,
  imports: [RouterLink, NzIconModule, NzLayoutModule, NzMenuModule],
  templateUrl: './menu-item.component.html',
  styleUrl: './menu-item.component.css'
})
export class MenuItemComponent implements OnInit, OnDestroy {
  @Input() item!: MenuItem;

  router = inject(Router);
  currentRoute: string = '';
  private routeSub!: Subscription;

  ngOnInit(): void {
    this.currentRoute = this.router.url;

    this.routeSub = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.urlAfterRedirects;
      });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  compareRoute(route: string): boolean {
    return this.currentRoute.includes(route);
  }
}
