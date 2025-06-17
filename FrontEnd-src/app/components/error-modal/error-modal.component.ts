import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-modal.component.html',
  styleUrls: ['./error-modal.component.css']
})
export class ErrorModalComponent {
  @Input() message: string = 'Ocorreu um erro inesperado.';
  @Output() close = new EventEmitter<void>();

  onClose() {
    this.close.emit();
  }
}