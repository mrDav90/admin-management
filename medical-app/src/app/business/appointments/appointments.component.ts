import { Component, inject } from '@angular/core';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzModalModule, NzModalService } from 'ng-zorro-antd/modal';
import { NzTableModule } from 'ng-zorro-antd/table';
import { ReloadButtonComponent } from '../../shared/components/reload-button/reload-button.component';
import { SearchBarComponent } from '../../shared/components/search-bar/search-bar.component';
import { Router, ActivatedRoute } from '@angular/router';
import { RESOURCES } from '../../shared/constants/resources.constants';
import { SCOPES } from '../../shared/constants/scopes.constants';
import { CaslService } from '../../shared/services/casl/casl.service';
import { AppointmentsService } from './appointments.service';
import { AppointmentResponse } from './models/appointment-response';
import { AddAppointmentComponent } from './add-appointment/add-appointment.component';
import { AppointmentStatus } from './models/appointment-status';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-appointments',
  imports: [
    CommonModule,
     NzModalModule,
     NzButtonModule,
     NzFlexModule,
     NzDividerModule,
     NzTableModule,
     NzDropDownModule,
     NzTagModule,
     NzIconModule,
     ReloadButtonComponent,
     SearchBarComponent,
     AddAppointmentComponent,
   ],
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.css'
})
export class AppointmentsComponent {
   RESOURCES = RESOURCES;
    SCOPES = SCOPES;
    loading = false;
    open = false;
    listAppointments: AppointmentResponse[] = [];
    currentRecord: AppointmentResponse | null = null;
    patientId: string = "";
    private appointmentsService = inject(AppointmentsService);
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
        this.getAppointments();
      });
    }
  
    getAppointments() {
      this.loading = true;
      this.appointmentsService
        ._getAppointments(this.pageNumber, this.pageSize)
        .subscribe({
          next: (data) => {
            this.listAppointments= data.content;
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
      this.getAppointments();
    }
  
    onAdd() {
      this.open = true;
      this.currentRecord = null;
    }
  
    onUpdate(record: AppointmentResponse) {
      this.open = true;
      this.currentRecord = record;
      this.patientId = record.patientId;
    }
  
    onDelete(id: string) {
      this.modal.confirm({
        nzTitle: 'Suppression?',
        nzContent: 'Etes-vous sûr de vouloir continuer ?',
        nzOnOk: () =>
          this.appointmentsService._deleteAppointment(id).subscribe({
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
      this.router.navigate(['/appointments'], {
        queryParams: {
          page: pageIndex,
          size: this.pageSize,
        },
      });
      this.getAppointments();
    }
  
    onPageSizeChange(pageSize: number): void {
      this.pageSize = pageSize;
      //this.pageNumber = 1;
      this.router.navigate(['/appointments'], {
        queryParams: {
          page: this.pageNumber,
          size: pageSize,
        },
      });
      this.getAppointments();
    }

    displayTagForAppointmentStatus(status: AppointmentStatus): { text: string; color: string } {
      let result: { text: string; color: string } = { text: '', color: '' };
      switch (status) {
        case AppointmentStatus.SCHEDULED:
          result.text = 'En attente';
          result.color = 'blue';
          break;
        case AppointmentStatus.COMPLETED:
          result.text = 'Terminé';
          result.color = 'green';
          break;
        case AppointmentStatus.CANCELLED:
          result.text = 'Annulé';
          result.color = 'red';
          break;
        default:
          result.text = 'En attente';
          result.color = 'blue';
          break;
      }

      return result;
    }
}
