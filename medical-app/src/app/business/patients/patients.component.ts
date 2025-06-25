import { Component, inject, OnInit } from '@angular/core';
import { PatientResponse } from './models/patient-response';
import { PatientsService } from './patients.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzModalModule, NzModalService } from 'ng-zorro-antd/modal';
import { NzTableModule } from 'ng-zorro-antd/table';
import { CaslService } from '../../shared/services/casl/casl.service';
import { AddPatientComponent } from './add-patient/add-patient.component';
import { ReloadButtonComponent } from '../../shared/components/reload-button/reload-button.component';
import { SearchBarComponent } from '../../shared/components/search-bar/search-bar.component';
import { RESOURCES } from '../../shared/constants/resources.constants';
import { SCOPES } from '../../shared/constants/scopes.constants';
import { AddAppointmentComponent } from "../appointments/add-appointment/add-appointment.component";
import { AppointmentResponse } from '../appointments/models/appointment-response';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [
    NzModalModule,
    NzButtonModule,
    NzFlexModule,
    NzDividerModule,
    NzTableModule,
    NzDropDownModule,
    NzIconModule,
    ReloadButtonComponent,
    SearchBarComponent,
    AddPatientComponent,
    AddAppointmentComponent
  ],
  templateUrl: './patients.component.html',
  styleUrl: './patients.component.css',
})
export class PatientsComponent implements OnInit {
  RESOURCES = RESOURCES;
  SCOPES = SCOPES;
  loading = false;
  open = false;
  open1 = false;
  listPatients: PatientResponse[] = [];
  currentRecord: PatientResponse | null = null;
  aptRecord: AppointmentResponse | null = null;
  patientId: string = '';
  private patientsService = inject(PatientsService);
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
      this.getPatients();
    });
  }

  getPatients() {
    this.loading = true;
    this.patientsService
      ._getPatients(this.pageNumber, this.pageSize)
      .subscribe({
        next: (data) => {
          this.listPatients = data.content;
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
    this.getPatients();
  }

  onAdd() {
    this.open = true;
    this.currentRecord = null;
  }

  onUpdate(record: PatientResponse) {
    this.open = true;
    this.currentRecord = record;
  }

  onDelete(id: string) {
    this.modal.confirm({
      nzTitle: 'Suppression?',
      nzContent: 'Etes-vous sûr de vouloir continuer ?',
      nzOnOk: () =>
        this.patientsService._deletePatient(id).subscribe({
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

   handleCancel1(): void {
    this.open1 = false;
  }

  onPageChange(pageIndex: number): void {
    this.pageNumber = pageIndex;
    this.router.navigate(['/patients'], {
      queryParams: {
        page: pageIndex,
        size: this.pageSize,
      },
    });
    this.getPatients();
  }

  onPageSizeChange(pageSize: number): void {
    this.pageSize = pageSize;
    //this.pageNumber = 1;
    this.router.navigate(['/patients'], {
      queryParams: {
        page: this.pageNumber,
        size: pageSize,
      },
    });
    this.getPatients();
  }

  onCreateAppointment(patientId:string){
    this.open1 = true;
    this.aptRecord=null;
    this.patientId = patientId;
  }
}

