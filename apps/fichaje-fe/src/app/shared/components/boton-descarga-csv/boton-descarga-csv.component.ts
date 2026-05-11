import { Component, Input } from '@angular/core';
import { DataCsv } from '../../interfaces/dataCsv';
import { AngularCsv } from 'angular-csv-ext/dist/Angular-csv';
import { unpack } from 'json-unpacker';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-boton-descarga-csv',
  templateUrl: './boton-descarga-csv.component.html',
  styleUrls: ['./boton-descarga-csv.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class BotonDescargaCsvComponent {
  @Input() apiEndPoint: string = '';
  @Input() headers: string[] = [];
  @Input() service: DataCsv | null = null;
  @Input() dto: any | null = null;
  @Input() fileName: string = 'downloaded-csv';

  downloadCsv() {
    if (this.service) {
      this.service.getCsvData(this.dto).subscribe({
        next: data => {
          const options = {
            fieldSeparator: ',',
            quoteStrings: '"',
            decimalseparator: '.',
            showLabels: false,
            showTitle: false,
            title: this.fileName,
            useBom: false,
            noDownload: false,
            headers: this.headers,
            useHeader: true,
            nullToEmptyString: false,
          };

          const date = new Date();
          const stringDate = `-${date.getDate()}-${date.getMonth() + 1}-${date.getFullYear()}`;
          new AngularCsv(unpack(data), this.fileName + stringDate, options);
        },
        error: err => console.error(err)
      });
    }
  }
}
