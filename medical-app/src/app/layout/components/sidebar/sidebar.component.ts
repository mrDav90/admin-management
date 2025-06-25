import { Component, inject, Input } from '@angular/core';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { MenuItemComponent } from './menu-item/menu-item.component';
import { CaslService } from '../../../shared/services/casl/casl.service';
import { RESOURCES } from '../../../shared/constants/resources.constants';
import { SCOPES } from '../../../shared/constants/scopes.constants';

export interface MenuItem {
  key?: string;
  title: string;
  icon?: string;
  route: string;
  children?: MenuItem[];
  group?: MenuItem[];
  display?: boolean;
}

@Component({
  selector: 'app-sidebar',
  imports: [NzIconModule, NzLayoutModule, NzMenuModule, MenuItemComponent],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  @Input() isCollapsed: boolean = false;
  caslService = inject(CaslService);
  can = this.caslService.can.bind(this.caslService);

  rawMenuItems: MenuItem[] = [
    {
      key: RESOURCES.DASHBOARD,
      title: 'Tableau de bord',
      icon: 'dashboard',
      route: '/dashboard',
    },
    {
      key: RESOURCES.PATIENT,
      title: 'Patients',
      icon: 'user',
      route: '/patients',
    },
    {
      key: RESOURCES.STAFF,
      title: 'Medecin',
      icon: 'team',
      route: '/doctors',
    },
    {
      key: RESOURCES.APPOINTMENT,
      title: 'Rendez-vous',
      icon: 'clock-circle',
      route: '/appointments',
    },
     {
      key: RESOURCES.BILLING,
      title: 'Facturation',
      icon: 'dollar',
      route: '/billing',
    },
    {
      title: 'Paramètres',
      icon: 'settings',
      route: '/settings',
      // children: [
      //   {
      //     title: 'Mon compte',
      //     route: '/settings/account'
      //   }
      // ],
      group: [
        {
          key: 'account',
          title: 'Mon compte',
          route: '/settings/account',
          icon: 'user',
        },
      ],
    },
    {
      key: RESOURCES.ACCOUNT,
      title: 'Comptes utilisateurs',
      icon: 'team',
      route: '/users',
    },
    {
      key: RESOURCES.ROLES,
      title: 'Rôles',
      icon: 'safety',
      route: '/roles',
    },
  ];
  selectedMenuItem: MenuItem | null = null;


  get menuItems(): MenuItem[] {
    let tempMenu : MenuItem[] = [];
    this.rawMenuItems.forEach(element => {
      if (element.group) {
        let tempGroup : MenuItem[] = [];
        element.group.forEach(elt2 => {
          if (this.can(SCOPES.READ, elt2.key!)) {
            tempGroup.push(elt2);
          }
        });
        if (tempGroup.length > 0) {
          element.group = tempGroup;
          tempMenu.push(element);
        }
      }else{
        if (this.can(SCOPES.READ, element.key!)) {
          tempMenu.push(element);
        }
      }
    });
    return tempMenu;
  }

}
