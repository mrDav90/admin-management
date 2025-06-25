import { Component, inject, OnInit } from '@angular/core';
import { AddRoleComponent } from './add-role/add-role.component';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzModalModule, NzModalService } from 'ng-zorro-antd/modal';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { ReloadButtonComponent } from '../../shared/components/reload-button/reload-button.component';
import { SearchBarComponent } from '../../shared/components/search-bar/search-bar.component';
import { RoleResponse } from './models/role-response';
import { RolesService } from './roles.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AddPermissionsComponent } from './add-permissions/add-permissions.component';
import { CaslService } from '../../shared/services/casl/casl.service';
import { RESOURCES } from '../../shared/constants/resources.constants';
import { SCOPES } from '../../shared/constants/scopes.constants';

@Component({
  selector: 'app-roles',
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
    AddRoleComponent,
    AddPermissionsComponent,
  ],
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.css',
})
export class RolesComponent implements OnInit {
  RESOURCES = RESOURCES;
  SCOPES = SCOPES;
  loading = false;
  open = false;
  openPermissionsModal = false;
  listRoles: RoleResponse[] = [];
  currentRecord: RoleResponse | null = null;
  private rolesService = inject(RolesService);
  private modal = inject(NzModalService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);
  pageNumber = 1;
  pageSize = 5;
  totalElements = 0;
  isLastPage = false;
  currentRoleName : string = "";


  caslService = inject(CaslService);
  can = this.caslService.can.bind(this.caslService);

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.pageNumber = params['page'] || 1;
      this.pageSize = params['size'] || 5;
      this.getRoles();
    });
  }

  getRoles() {
    this.loading = true;
    this.rolesService._getRoles(this.pageNumber, this.pageSize).subscribe({
      next: (data) => {
        this.listRoles = data.content;
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
    this.getRoles();
  }

  onAdd() {
    this.open = true;
    this.currentRecord = null;
  }

  onUpdate(record: RoleResponse) {
    this.open = true;
    this.currentRecord = record;
  }

  onDelete(id: string) {
    this.modal.confirm({
      nzTitle: 'Suppression?',
      nzContent: 'Etes-vous sûr de vouloir continuer ?',
      nzOnOk: () => console.log('A supprimer'),

      // this.rolesService._deleteRole(id).subscribe({
      //   next: () => {
      //     this.onRefresh();
      //   },
      //   error: (error) => {
      //     console.log(error);
      //   },
      //   complete: () => {
      //     console.log('traitement termine.');
      //   },
      // }),
    });
  }

  configurePermissions(name: string) {
    this.openPermissionsModal = true;
    this.currentRoleName = name;
  }

  handlePermissionsModalCancel(): void {
    this.openPermissionsModal = false;
  }

  handleCancel(): void {
    this.open = false;
  }

  onPageChange(pageIndex: number): void {
    this.pageNumber = pageIndex;
    this.router.navigate(['/roles'], {
      queryParams: {
        page: pageIndex,
        size: this.pageSize,
      },
    });
    this.getRoles();
  }

  onPageSizeChange(pageSize: number): void {
    this.pageSize = pageSize;
    //this.pageNumber = 1;
    this.router.navigate(['/roles'], {
      queryParams: {
        page: this.pageNumber,
        size: pageSize,
      },
    });
    this.getRoles();
  }
}
