import { Component, inject } from '@angular/core';
import { UserResponse } from './models/user-response';
import { UsersService } from './users.service';
import { NzModalModule, NzModalService } from 'ng-zorro-antd/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { AddUserComponent } from './add-user/add-user.component';
import { ReloadButtonComponent } from '../../shared/components/reload-button/reload-button.component';
import { SearchBarComponent } from '../../shared/components/search-bar/search-bar.component';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { CaslService } from '../../shared/services/casl/casl.service';
import { RESOURCES } from '../../shared/constants/resources.constants';
import { SCOPES } from '../../shared/constants/scopes.constants';

@Component({
  selector: 'app-users',
  imports: [
    NzButtonModule,
    NzFlexModule,
    NzDividerModule,
    NzTableModule,
    NzModalModule,
    NzDropDownModule,
    NzTagModule,
    NzIconModule,
    ReloadButtonComponent,
    SearchBarComponent,
    AddUserComponent,
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css',
})
export class UsersComponent {
  RESOURCES = RESOURCES;
  SCOPES = SCOPES;
  loading = false;
  open = false;
  listUsers: UserResponse[] = [];
  currentRecord: UserResponse | null = null;
  private usersService = inject(UsersService);
  private modal = inject(NzModalService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);
  pageNumber = 1;
  pageSize = 5;
  totalElements = 0;
  isLastPage = false;

  caslService = inject(CaslService);
  can = this.caslService.can.bind(this.caslService);
  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.pageNumber = params['page'] || 1;
      this.pageSize = params['size'] || 5;
      this.getUsers();
    });
  }

  getUsers() {
    this.loading = true;
    this.usersService._getUsers(this.pageNumber, this.pageSize).subscribe({
      next: (data) => {
        this.listUsers = data.content;
        this.totalElements = data.totalElements;
        this.isLastPage = data.last;
        this.loading = false;
      },
      error: (error) => {
        console.log(error);
        this.loading = false;
      },
      complete: () => {
        console.log('traitement termine.');
        this.loading = false;
      },
    });
  }

  onRefresh() {
    this.getUsers();
  }

  onAdd() {
    this.open = true;
    this.currentRecord = null;
  }

  onUpdate(record: UserResponse) {
    this.open = true;
    this.currentRecord = record;
  }

  onDelete(id: string) {
    this.modal.confirm({
      nzTitle: 'Suppression?',
      nzContent: 'Etes-vous sûr de vouloir continuer ?',
      nzOnOk: () =>
        this.usersService._deleteUser(id).subscribe({
          next: () => {
            this.onRefresh();
          },
          error: (error) => {
            console.log(error);
          },
          complete: () => {
            console.log('traitement termine.');
          },
        }),
    });
  }

  handleCancel(): void {
    this.open = false;
  }

  onPageChange(pageIndex: number): void {
    this.pageNumber = pageIndex;
    this.router.navigate(['/users'], {
      queryParams: {
        page: pageIndex,
        size: this.pageSize,
      },
    });
    this.getUsers();
  }

  onPageSizeChange(pageSize: number): void {
    this.pageSize = pageSize;
    //this.pageNumber = 1;
    this.router.navigate(['/users'], {
      queryParams: {
        page: this.pageNumber,
        size: pageSize,
      },
    });
    this.getUsers();
  }

  getAppRoleName(roles: string[]): string {
    for (let i = 0; i < roles.length; i++) {
      const role = roles[i];

      if (role.startsWith('app_')) {
        return role;
      }
    }

    return '';
  }
}
