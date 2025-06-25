import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { DoctorsService } from '../doctors.service';
import {DoctorResponse } from '../models/doctor-response';
import { NzMessageService } from 'ng-zorro-antd/message';
import {
  FormGroup,
  FormControl,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { DoctorRequest } from '../models/doctor-request';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzSpaceModule } from 'ng-zorro-antd/space';
import { NzRadioModule } from 'ng-zorro-antd/radio';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { Gender } from '../../../shared/enums/gender';

@Component({
  selector: 'app-add-doctor',
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
    NzSelectModule,
    NzRadioModule,
    NzDatePickerModule

  ],
  templateUrl: './add-doctor.component.html',
  styleUrl: './add-doctor.component.css',
})
export class AddDoctorComponent implements OnInit {
  @Input() currentRecord: DoctorResponse | null = null;
  @Output() _onClose = new EventEmitter();
  @Output() _onRefresh = new EventEmitter();
  loading = false;

  private message = inject(NzMessageService);
  private doctorsService = inject(DoctorsService);

  addDoctorForm = new FormGroup({
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
   
    telephone: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    
    specialty: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  ngOnInit(): void {
    if (this.currentRecord) {
      this.addDoctorForm.patchValue(this.currentRecord);
    }
  }

  submitForm() {
    if (this.addDoctorForm.valid) {
      this.loading = true;
      if (this.currentRecord) {
        this.doctorsService
          ._updateDoctor(
            this.currentRecord.id,
            this.addDoctorForm.value as DoctorRequest
          )
          .subscribe({
            next: () => {
              this.loading = false;
              this.message.success('Informations du patient modifiées avec succès');
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
        this.doctorsService
          ._createDoctor(this.addDoctorForm.value as DoctorRequest)
          .subscribe({
            next: () => {
              this.loading = false;
              this.message.success(`Medecin crée avec succès`);
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
      }
    } else {
      Object.values(this.addDoctorForm.controls).forEach((control) => {
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
