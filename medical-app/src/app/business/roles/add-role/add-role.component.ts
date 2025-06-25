import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  FormGroup,
  FormControl,
  Validators,
  ReactiveFormsModule,
  ValidatorFn,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { RolesService } from '../roles.service';
import { RoleRequest } from '../models/role-request';
import { RoleResponse } from '../models/role-response';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzSpaceModule } from 'ng-zorro-antd/space';

@Component({
  selector: 'app-add-role',
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
  ],
  templateUrl: './add-role.component.html',
  styleUrl: './add-role.component.css',
})
export class AddRoleComponent implements OnInit {
  @Input() currentRecord: RoleResponse | null = null;
  @Output() _onClose = new EventEmitter();
  @Output() _onRefresh = new EventEmitter();
  loading = false;

  private message = inject(NzMessageService);
  private rolesService = inject(RolesService);

  startsWithAppValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      
      if (!control.value) {
        return null;
      }

      const value: string = control.value;
      const isValid = value.startsWith('app_');

      return isValid ? null : { startsWithApp: true };
    };
  }

  addRoleForm = new FormGroup({
    name: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required , this.startsWithAppValidator()],
    }),
    description: new FormControl<string>(''),
  });

  ngOnInit(): void {
    if (this.currentRecord) {
      this.addRoleForm.patchValue(this.currentRecord);
    }
  }

  submitForm() {
    if (this.addRoleForm.valid) {
      this.loading = true;
      if (this.currentRecord) {
        this.message.warning(
          `Modification du compte utilisateur pas encore disponible`
        );
        this.loading = false;
      } else {
        this.rolesService
          ._createRole(this.addRoleForm.value as RoleRequest)
          .subscribe({
            next: () => {
              this.message.success(`Rôle crée avec succès`);
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
      Object.values(this.addRoleForm.controls).forEach((control) => {
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
