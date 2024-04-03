import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-menu-bola',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-bola.component.html',
  styleUrl: './menu-bola.component.scss'
})
export class MenuBolaComponent {
  mostrarMenu: boolean = false;

  toggleMenu() {
      this.mostrarMenu = !this.mostrarMenu;
  }

  accion1() {
      // Lógica para la acción 1
  }

  accion2() {
      // Lógica para la acción 2
  }

  accion3() {
      // Lógica para la acción 3
  }

  accion4() {
      // Lógica para la acción 4
  }

  accion5() {
      // Lógica para la acción 5
  }
}