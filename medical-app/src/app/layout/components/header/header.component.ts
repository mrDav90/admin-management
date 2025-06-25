import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzAffixModule } from 'ng-zorro-antd/affix';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import Keycloak from 'keycloak-js';
import { User } from '../../../shared/models/user';
import { AuthService } from '../../../auth/services/auth.service';
import { UserInfos } from '../../../auth/models/UserInfos';
import { CaslService } from '../../../shared/services/casl/casl.service';
import { NzTagModule } from 'ng-zorro-antd/tag';

@Component({
  selector: 'app-header',
  imports: [
    NzLayoutModule,
    NzIconModule,
    NzButtonModule,
    NzDropDownModule,
    NzAvatarModule,
    NzAffixModule,
    NzTagModule
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  @Input() isCollapsed: boolean = false;
  @Output() _onToggle = new EventEmitter();
  keycloak = inject(Keycloak);
  user: User | undefined;
  offsetTop: number = 0.1;
  authService = inject(AuthService);
  userInfos : UserInfos | undefined;
  caslService = inject(CaslService);

  async ngOnInit() {
    if (this.keycloak?.authenticated) {
      // this.keycloak.loadUserProfile().then((userInfo) => {
      //   console.log(userInfo);
      //   this.user = userInfo as User;
      // });

      this.authService._getMe().subscribe((data) => {
        this.userInfos = data;
        this.caslService.defineAbilityFor(this.userInfos);
        localStorage.setItem('userInfos', JSON.stringify(this.userInfos));
      });
    }
  }

  logout() {
    this.keycloak.logout();
  }

  onToggle() {
    this._onToggle.emit();
  }
}
