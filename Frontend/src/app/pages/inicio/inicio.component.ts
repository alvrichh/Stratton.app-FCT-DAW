import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';
import { CatalogoService } from '../../services/catalogo.service';
import { Comercializadora } from '../../services/catalogo.model';

interface Oferta {
  nombre: string;
  logo: string;
  precio: string;
  descripcion: string;
  ventajas: string[];
  desventajas: string[];
}

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css'
})
export class InicioComponent implements OnInit {

  comercializadoras: Comercializadora[] = [];

  /** Comparativa de ofertas que la asesoría muestra a sus clientes. */
  ofertas: Oferta[] = [
    {
      nombre: 'Iberdrola',
      logo: 'assets/images/iberdrola.png',
      precio: '0,167 €/kWh. Con el 20% de descuento, el precio se queda en 0,127 €/kWh.',
      descripcion: 'Ofrecen un pack inicial con un coste de 3,5 € los primeros 4 meses (50% de descuento), luego 9 €.',
      ventajas: ['Regalo de una tarjeta de 50 € en Carrefour o Leroy Merlin si vives en Sevilla, Málaga o Badajoz.', 'No tiene permanencia ni costes de mantenimiento.'],
      desventajas: ['El pack inicial tiene un coste.'],
    },
    {
      nombre: 'Endesa',
      logo: 'assets/images/endesa.png',
      precio: '0,17 €/kWh. Con un descuento del 25%, el precio final es de 0,127 €/kWh.',
      descripcion: 'Tienen la posibilidad de ofrecer un precio de 0,10 €/kWh, pero con permanencia de un año.',
      ventajas: ['Descuento inicial del 25% durante dos años.'],
      desventajas: ['Permanencia de un año.', 'Mantenimiento "obligatorio" con coste de 7 €/mes.'],
    },
    {
      nombre: 'Naturgy',
      logo: 'assets/images/naturgy.png',
      precio: '0,11 €/kWh. El precio no tiene descuentos.',
      descripcion: 'No ofrecen descuentos, manteniendo el mismo precio en todo momento.',
      ventajas: ['Precio constante sin variaciones.'],
      desventajas: ['Cobro de 2 €/mes por la batería virtual.', 'Muchos mensajes de spam.'],
    },
  ];

  constructor(public authService: AuthService, private catalogo: CatalogoService) { }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.catalogo.obtenerComercializadoras().subscribe({
        next: (c) => this.comercializadoras = c,
        error: () => this.comercializadoras = [],
      });
    }
  }
}
