import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-th-sort',
  templateUrl: './th-sort.component.html',
  styleUrls: ['./th-sort.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [CommonModule]
})
export class ThSortComponent {
  @Input() entity: string = "";
  @Input() name: string = "Title";
  @Input() asc: boolean = true;
  @Input() order: string = "id";
  @Output() sort = new EventEmitter<string>();

  setOrder(): void {
    this.sort.emit(this.entity);
  }
}
