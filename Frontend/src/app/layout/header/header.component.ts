import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatToolbarModule, MatFormFieldModule, MatIconModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
 
  initializeCustomScript(): void {
    
    // Cambiar icono de sol a luna
    const toggleContrastButton = document.getElementById('toggleContrastButton');
    if (toggleContrastButton) {
      toggleContrastButton.addEventListener('click', () => {
        document.body.classList.toggle('high-contrast');
        const contrastIcon = document.getElementById('contrastIcon');
        const isHighContrast = document.body.classList.contains('high-contrast');
        if (contrastIcon) {
          contrastIcon.classList.toggle('bi-moon', isHighContrast);
          contrastIcon.classList.toggle('bi-sun', !isHighContrast);
        }
        const svgImage = document.getElementById('svg');
      /*  if (svgImage) {
          svgImage.src = isHighContrast ? 'images/letrero-dark.svg' : 'images/letrero.svg';
        }*/
      });
    }

    //ANIMACION CAMPANITA===================================
    const campana = document.getElementById("campana");
    if (campana) {
      campana.addEventListener("mouseenter", () => {
        campana.classList.add("animate__swing");
      });
      campana.addEventListener("mouseleave", () => {
        campana.classList.remove("animate__swing");
      });
    }

    // Añadir Toast al hacer clic en la campana de notificaciones
    const notificaciones = document.getElementById('notificaciones');
    if (notificaciones) {
      notificaciones.addEventListener('click', () => {
        //const toast = new bootstrap.Toast(document.querySelector('.toast'));
        //toast.show();
      });
    }

    // Aquí puedes agregar más funciones de inicialización de JavaScript según sea necesario
  }

  logout(): void {
    // Implementación del método logout
    console.log('Usuario deslogueado');
  }

  sidebarOpen = false;
  navbarOpen = false;

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
    console.log('Barra lateral toggled:', this.sidebarOpen ? 'Abierta' : 'Cerrada');
  }

  toggleNavbar(): void {
    this.navbarOpen = !this.navbarOpen;
    console.log('Navegación toggled:', this.navbarOpen ? 'Abierta' : 'Cerrada');
  }
}