import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './business/dashboard/dashboard.component';
import { canActivateAuthRole } from './auth/guard/auth.guard';
import { RolesComponent } from './business/roles/roles.component';
import { UsersComponent } from './business/users/users.component';
import { PatientsComponent } from './business/patients/patients.component';
import { DoctorsComponent } from './business/doctors/doctors.component';
import { AppointmentsComponent } from './business/appointments/appointments.component';
import { BillingComponent } from './business/billing/billing.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full',
  },
  {
    path: '',
    component: LayoutComponent,
    data: {
      breadcrumb: 'Accueil',
    },
    canActivate: [canActivateAuthRole],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
          breadcrumb: 'Tableau de bord',
        },
      },
      {
        path: 'patients',
        component: PatientsComponent,
        data: {
          breadcrumb: 'Liste des patients',
        },
      },
      {
        path: 'doctors',
        component: DoctorsComponent,
        data: {
          breadcrumb: 'Liste des Medecins',
        },
      },
       {
        path: 'appointments',
        component: AppointmentsComponent,
        data: {
          breadcrumb: 'Liste des Rendez-vous',
        },
      },
      {
        path: 'billing',
        component: BillingComponent,
        data: {
          breadcrumb: 'Facturation',
        },
      },
      {
        path: 'users',
        component: UsersComponent ,
        data: {
          breadcrumb: 'Comptes Utilisateurs',
        },
      },
      {
        path: 'roles',
        component: RolesComponent,
        data: {
          breadcrumb: 'Rôles',
        },
      },
    ],
  },
];
