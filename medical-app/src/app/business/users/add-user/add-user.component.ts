import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { UsersService } from '../users.service';
import { UserRequest } from '../models/user-request';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { UserResponse } from '../models/user-response';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzSpaceModule } from 'ng-zorro-antd/space';
import { RoleResponse } from '../../roles/models/role-response';
import { RolesService } from '../../roles/roles.service';

@Component({
  selector: 'app-add-user',
  imports: [
    NzPageHeaderModule,
    NzFormModule,
    ReactiveFormsModule,
    NzInputModule,
    NzSpaceModule,
    NzButtonModule,
    NzFlexModule,
    NzCardModule,
    NzDividerModule,
    NzSelectModule
  ],
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css'
})
export class AddUserComponent implements OnInit {
  @Input() currentRecord: UserResponse | null = null;
  @Output() _onClose = new EventEmitter();
  @Output() _onRefresh = new EventEmitter();
  loading = false;
  rolesLoading = false;

  listRoles : RoleResponse[] = [];


  private message = inject(NzMessageService);
  private usersService = inject(UsersService);
  private rolesService = inject(RolesService);

  addUserForm = new FormGroup({
    firstName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    lastName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    role: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    password: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  ngOnInit(): void {
    this.getRoles();
    if (this.currentRecord) {
      this.addUserForm.patchValue(this.currentRecord);
    }
  }

  getRoles() {
    this.rolesLoading = true;
    this.rolesService._getRoles(1, 20).subscribe({
      next: (data) => {
        this.listRoles = data.content;
        this.rolesLoading = false;
      },
      error: (error) => {
        console.log(error);
        this.rolesLoading = false;
      },
      complete: () => {
        console.log('traitement termine.');
        this.rolesLoading = false;
      },
    });
  }


  submitForm() {
    if (this.addUserForm.valid) {
      this.loading = true;
      if (this.currentRecord) {
        this.usersService
          ._updateUser(
            this.currentRecord.id,
            this.addUserForm.value as UserRequest
          )
          .subscribe({
            next: () => {
              this.loading = false;
              this.message.success('Compte utilisateur modifié avec succès');
              this.onClose();
              this.onRefresh();
            },
            error: (error) => {
              this.loading = false;
              console.log(error);
            },
            complete: () => {
              this.loading = false;
              console.log('traitement termine.');
            },
          });
      } else {
        this.usersService
          ._createUser(this.addUserForm.value as UserRequest)
          .subscribe({
            next: () => {
              this.message.success(`Compte utilisateur crée avec succès`);
              this.onClose();
              this.onRefresh();
            },
            error: (error) => {
              console.log(error);
            },
            complete: () => {
              console.log('traitement termine.');
            },
          });
      }
    } else {
      Object.values(this.addUserForm.controls).forEach((control) => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      });
    }
  }

  onRefresh() {
    this._onRefresh.emit();
  }

  onClose() {
    this._onClose.emit();
  }
}
