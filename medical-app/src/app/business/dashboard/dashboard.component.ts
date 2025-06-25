import { Component, OnInit } from '@angular/core';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { CommonModule } from '@angular/common';
import { NzFlexModule } from 'ng-zorro-antd/flex';

@Component({
  selector: 'app-dashboard',
  imports: [
    CommonModule,
    NzCardModule,
    NzGridModule,
    NzFlexModule
],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  ngOnInit(): void {
    
  }
}
