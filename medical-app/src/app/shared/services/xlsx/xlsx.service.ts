import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';

@Injectable({
  providedIn: 'root'
})
export class XlsxService {

  constructor() { }

  _importFile(file : File) : Promise<any[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e: ProgressEvent<FileReader>) => {
        const data = new Uint8Array(e.target?.result as ArrayBuffer);
        const workbook = XLSX.read(data, { type: 'array' });
        const sheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[sheetName];

        const rows: any[] = XLSX.utils.sheet_to_json(worksheet, { defval: '' });
        resolve(rows);
      };

      reader.onerror = error => reject(error);
      reader.readAsArrayBuffer(file);
    });
  }

  _export<T>(rows: T[], title: string) {
    const worksheet = XLSX.utils.json_to_sheet(rows);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, title);
    XLSX.writeFile(workbook, title+".xlsx" , { compression: true });
  }

  _downloadModel<T>() {
    
  }

}
