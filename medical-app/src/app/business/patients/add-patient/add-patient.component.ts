import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { PatientsService } from '../patients.service';
import { PatientResponse } from '../models/patient-response';
import { NzMessageService } from 'ng-zorro-antd/message';
import {
  FormGroup,
  FormControl,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { PatientRequest } from '../models/patient-request';
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
  selector: 'app-add-patient',
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
  templateUrl: './add-patient.component.html',
  styleUrl: './add-patient.component.css',
})
export class AddPatientComponent implements OnInit {
  @Input() currentRecord: PatientResponse | null = null;
  @Output() _onClose = new EventEmitter();
  @Output() _onRefresh = new EventEmitter();
  loading = false;

  private message = inject(NzMessageService);
  private patientsService = inject(PatientsService);

  addPatientForm = new FormGroup({
    firstName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    lastName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    birthDate: new FormControl<Date>(new Date(), {
      nonNullable: true,
      validators: [Validators.required],
    }),
    placeOfBirth: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),

    gender: new FormControl<Gender>(Gender.Male, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    phoneNumber: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    address: new FormControl<string>(''),
    personToNotifyName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    personToNotifyPhoneNumber: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  ngOnInit(): void {
    if (this.currentRecord) {
      this.addPatientForm.patchValue(this.currentRecord);
    }
  }

  submitForm() {
    if (this.addPatientForm.valid) {
      this.loading = true;
      if (this.currentRecord) {
        this.patientsService
          ._updatePatient(
            this.currentRecord.id,
            this.addPatientForm.value as PatientRequest
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
              this.message.error(error.error.message);
              console.log(error);
            },
            complete: () => {
              this.loading = false;
              console.log('traitement termine.');
            },
          });
      } else {
        this.patientsService
          ._createPatient(this.addPatientForm.value as PatientRequest)
          .subscribe({
            next: () => {
              this.loading = false;
              this.message.success(`Patient crée avec succès`);
              this.onClose();
              this.onRefresh();
            },
            error: (error) => {
              this.loading = false;
              this.message.error(error.error.message);
              console.log(error);
            },
            complete: () => {
              this.loading = false;
              console.log('traitement termine.');
            },
          });
      }
    } else {
      Object.values(this.addPatientForm.controls).forEach((control) => {
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
