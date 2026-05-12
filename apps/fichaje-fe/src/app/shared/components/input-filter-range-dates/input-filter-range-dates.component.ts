import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-input-filter-range-dates',
  templateUrl: './input-filter-range-dates.component.html',
  styleUrls: ['./input-filter-range-dates.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class InputFilterRangeDatesComponent {
  @Input() name: string = "Text filter";
  @Input() id: string = "text";
  @Input() inputValue: string = '';
  @Output() inputValueChange = new EventEmitter<string>();
  @Output() list = new EventEmitter<void>();
  @Output() clear = new EventEmitter<void>();

  listElements(): void {
    this.list.emit();
  }

  clearField(): void {
    this.clear.emit();
  }
}
