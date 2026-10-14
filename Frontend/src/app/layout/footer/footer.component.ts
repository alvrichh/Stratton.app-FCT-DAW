import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Alertas } from '../../core/alertas';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  email = '';
  anio = new Date().getFullYear();

  suscribirse(): void {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
      Alertas.error(new Error('Introduce una dirección de correo válida.'), 'Correo no válido');
      return;
    }
    Alertas.exito('Suscripción completada', `Te enviaremos las novedades a ${this.email}.`);
    this.email = '';
  }
}
