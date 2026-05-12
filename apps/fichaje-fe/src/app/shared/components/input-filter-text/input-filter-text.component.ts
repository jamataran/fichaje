import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-input-filter-text',
  templateUrl: './input-filter-text.component.html',
  styleUrls: ['./input-filter-text.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class InputFilterTextComponent {
  @Input() inputValue: string = "";
  @Input() name: string = "Text filter";
  @Input() id: string = "text";
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
