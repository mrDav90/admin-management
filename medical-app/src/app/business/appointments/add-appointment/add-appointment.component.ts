import { Component ,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { AppointmentsService } from '../appointments.service';
import { AppointmentResponse } from '../models/appointment-response';
import { NzMessageService } from 'ng-zorro-antd/message';
import {
  FormGroup,
  FormControl,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AppointmentRequest } from '../models/appointment-request';
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
import { DoctorResponse } from '../../doctors/models/doctor-response';
import { DoctorsService } from '../../doctors/doctors.service';
@Component({
  selector: 'app-add-appointment',
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
  templateUrl: './add-appointment.component.html',
  styleUrl: './add-appointment.component.css'
})
export class AddAppointmentComponent implements OnInit {
@Input() currentRecord: AppointmentResponse | null = null;
@Input() patientId: string = "";
  @Output() _onClose = new EventEmitter();
  @Output() _onRefresh = new EventEmitter();
  loading = false;
  listDoctors:DoctorResponse[]=[];
  listDoctorsloading=false;
  doctorsService=inject(DoctorsService)


  private message = inject(NzMessageService);
  private appointmentsService = inject(AppointmentsService);

  addAppointmentForm = new FormGroup({
    appointmentDate: new FormControl<Date>(new Date(),{
      nonNullable: true,
      validators: [Validators.required],
    }),
    reason: new FormControl<string>(''),
    // patientId: new FormControl<string>('', {
    //   nonNullable: true,
    //  validators: [Validators.required],
    // }),
   
    doctorId: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    
  });

  ngOnInit(): void {
    this.getDoctors();
    if (this.currentRecord) {
      this.addAppointmentForm.patchValue(this.currentRecord);
    }
    
  }
  getDoctors() {
      this.listDoctorsloading = true;
      this.doctorsService
        ._getDoctors(1, 30)
        .subscribe({
          next: (data) => {
            this.listDoctors = data.content;
            this.listDoctorsloading = false;
          },
          error: (error) => {
            console.log(error);
            this.listDoctorsloading = false;
          },
          complete: () => {
            console.log('traitement termine.');
            this.listDoctorsloading = false;
          },
        });
    }
  submitForm() {
    if (this.addAppointmentForm.valid) {
      
        const payload:AppointmentRequest={
            appointmentDate:new Date(this.addAppointmentForm.value.appointmentDate as Date),
            reason:this.addAppointmentForm.value.reason as string,
            doctorId:this.addAppointmentForm.value.doctorId as string,
            patientId: this.patientId,
        }

        this.loading = true;
      if (this.currentRecord) {
         
        this.appointmentsService
          ._updateAppointment(
            this.currentRecord.id,
           payload
          )
          .subscribe({
            next: () => {
              this.loading = false;
              this.message.success('Informations du rendez-vous modifiées avec succès');
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
        this.appointmentsService
          ._createAppointment(payload)
          .subscribe({
            next: () => {
              this.loading = false;
              this.message.success(`Rendez-vous crée avec succès`);
              this.onClose();
              this.onRefresh();
            },
            error: (error) => {
              this.loading = false;
              console.log(error);
            },
            complete: () => {
              this.loading = false;
              console.log('traitement terminé.');
            },
          });
      }
    } else {
      Object.values(this.addAppointmentForm.controls).forEach((control) => {
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
