import {
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzCheckboxModule, NzCheckboxOption } from 'ng-zorro-antd/checkbox';
import { NzSpaceModule } from 'ng-zorro-antd/space';
import { FeaturePermission } from '../models/feature-permission';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzUploadModule } from 'ng-zorro-antd/upload';
import { RolesService } from '../roles.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { PermissionItem } from '../models/permission-item';
import { NzListModule } from 'ng-zorro-antd/list';
import { AssignPermission } from '../models/assign-permission';
import { AppPermission } from '../models/app-permission';
import { NzGridModule } from 'ng-zorro-antd/grid';

@Component({
  standalone: true,
  selector: 'app-add-permissions',
  imports: [
    NzDividerModule,
    NzTableModule,
    NzCheckboxModule,
    NzSpaceModule,
    NzUploadModule,
    NzButtonModule,
    NzIconModule,
    NzListModule,
    NzGridModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './add-permissions.component.html',
  styleUrl: './add-permissions.component.css',
})
export class AddPermissionsComponent implements OnInit {
  @Input() currentRoleName: string = '';
  @Output() _onClose = new EventEmitter();

  loading = false;
  formLoading = false;
  rolesService = inject(RolesService);
  msg = inject(NzMessageService);
  permissionsList: FeaturePermission[] = [];
  selectedPermissions: PermissionItem[] = [];

  permissions: AppPermission[] = [];
  options: NzCheckboxOption[] = [];

  value: Array<string | number> = [];

  ngOnInit(): void {
    this.getPermissionsByRole();
  }

  isAllCheckedFirstChange = true;
  allChecked = false;

  updateAllChecked(): void {
    if (!this.isAllCheckedFirstChange) {
      this.value = this.allChecked
        ? this.options.map((item) => item.value)
        : [];
    }
    this.isAllCheckedFirstChange = false;
  }

  updateSingleChecked(): void {
    this.allChecked = this.value.length === this.options.length;
  }

  getAllPermissions() {
    this.loading = true;
    this.rolesService._getAllPermissions().subscribe({
      next: (data) => {
        // this.permissions = data.map((item) => {
        //   if (this.value.includes(item.name)) {
        //     this.selectedPermissions.push(item);
        //     return {
        //       ...item,
        //       checked: true
        //     }
        //   }else{
        //     return {
        //       ...item,
        //       checked: false
        //     }
        //   }

        this.permissions = data.map((item) => {
          const tempPerms = item.permissions.map((p) => {
            if (this.value.includes(p.name)) {
              this.selectedPermissions.push(p);
              return {
                ...p,
                checked: true,
              };
            } else {
              return {
                ...p,
                checked: false,
              };
            }
          });
          return {
            ...item,
            permissions: tempPerms,
          };
        });

        //console.log(this.permissions);
        //this.permissions = data;
        //this.options =  this.permissions.map((p) => ({ label: p.name, value: p.name }));
        this.loading = false;
      },
      error: (error) => {
        console.error('Error adding permissions:', error);
        this.loading = false;
      },
      complete: () => {
        console.log('Permission addition process completed.');
        this.loading = false;
      },
    });
  }

  getPermissionsByRole() {
    this.rolesService._getPermissionsByRole(this.currentRoleName).subscribe({
      next: (data) => {
        this.value = data;
        this.getAllPermissions();
      },
      error: (error) => {
        console.error('Error adding permissions:', error);
      },
      complete: () => {
        console.log('Permission addition process completed.');
      },
    });
  }

  log(value: any): void {
    console.log(value);
  }

  onChange(e: any, item: PermissionItem) {
    if (e) {
      this.selectedPermissions.push(item);
    } else {
      this.selectedPermissions = this.selectedPermissions.filter(
        (p) => p.name !== item.name
      );
    }
    console.log(this.selectedPermissions);
  }

  savePermissions() {
    this.formLoading = true;
    this.rolesService
      ._assignPermissions(this.currentRoleName, {
        permissions: this.selectedPermissions,
      } as AssignPermission)
      .subscribe({
        next: () => {
          this.formLoading = false;
          this.onClose();
          this.msg.success('Permissions assignées avec succès');
        },
        error: (error) => {
          console.error('Error adding permissions:', error);
          this.formLoading = false;
        },
        complete: () => {
          console.log('Permission addition process completed.');
          this.formLoading = false;
        },
      });
  }

  onClose() {
    this._onClose.emit();
  }
}
