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
import { DoctorsService } from './doctors.service';
import { DoctorResponse } from './models/doctor-response';
import { AddDoctorComponent } from './add-doctor/add-doctor.component';

@Component({
  selector: 'app-doctors',
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
     AddDoctorComponent,
   ],
  templateUrl: './doctors.component.html',
  styleUrl: './doctors.component.css'
})
export class DoctorsComponent {
   RESOURCES = RESOURCES;
    SCOPES = SCOPES;
    loading = false;
    open = false;
    listDoctors: DoctorResponse[] = [];
    currentRecord: DoctorResponse | null = null;
    private doctorsService = inject(DoctorsService);
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
        this.getDoctors();
      });
    }
  
    getDoctors() {
      this.loading = true;
      this.doctorsService
        ._getDoctors(this.pageNumber, this.pageSize)
        .subscribe({
          next: (data) => {
            this.listDoctors = data.content;
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
      this.getDoctors();
    }
  
    onAdd() {
      this.open = true;
      this.currentRecord = null;
    }
  
    onUpdate(record: DoctorResponse) {
      this.open = true;
      this.currentRecord = record;
    }
  
    onDelete(id: string) {
      this.modal.confirm({
        nzTitle: 'Suppression?',
        nzContent: 'Etes-vous sûr de vouloir continuer ?',
        nzOnOk: () =>
          this.doctorsService._deleteDoctor(id).subscribe({
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
      this.router.navigate(['/doctors'], {
        queryParams: {
          page: pageIndex,
          size: this.pageSize,
        },
      });
      this.getDoctors();
    }
  
    onPageSizeChange(pageSize: number): void {
      this.pageSize = pageSize;
      //this.pageNumber = 1;
      this.router.navigate(['/doctors'], {
        queryParams: {
          page: this.pageNumber,
          size: pageSize,
        },
      });
      this.getDoctors();
    }

}
