import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css'
})
export class InicioComponent implements OnInit {
 google: any;
  codigoPostal = '41018'; // Código postal de ejemplo
  ubicacion: any; // Variable para almacenar la ubicación obtenida

  constructor(private http: HttpClient) { }
  // Ejemplo de reseñas de Google (debes adaptarlo a tus datos reales)
  googleReviews: any[] = [
    {
      authorName: 'Juan Pérez',
      authorAvatar: 'https://randomuser.me/api/portraits/men/1.jpg',
      rating: 5,
      content: '¡Excelente servicio! Muy recomendado.',
      date: '15 de junio de 2024'
    },
    {
      authorName: 'María García',
      authorAvatar: 'https://randomuser.me/api/portraits/women/2.jpg',
      rating: 4,
      content: 'Buena experiencia, aunque podría mejorar.',
      date: '12 de junio de 2024'
    },
    {
      authorName: 'Carlos Martínez',
      authorAvatar: 'https://randomuser.me/api/portraits/men/3.jpg',
      rating: 3,
      content: 'Servicio aceptable, pero tardaron en resolver el problema.',
      date: '10 de junio de 2024'
    },
    {
      authorName: 'Ana Rodríguez',
      authorAvatar: 'https://randomuser.me/api/portraits/women/4.jpg',
      rating: 5,
      content: '¡Increíble atención al cliente! Resolverían cualquier duda rápidamente.',
      date: '5 de junio de 2024'
    }
  ];


  ngOnInit(): void {
    this.obtenerUbicacionZippopotam();
    // Aquí podrías cargar las reseñas de Google desde tu servicio o API
    // y asignarlas a la propiedad googleReviews.
  }

  // Función para generar un arreglo de estrellas basado en la calificación
  stars(rating: number): number[] {
    return Array.from({ length: rating }, (_, index) => index + 1);
  }
  obtenerUbicacionZippopotam() {
    const url = `http://api.zippopotam.us/ES/${this.codigoPostal}`;
    this.http.get(url)
      .subscribe((data: any) => {
        console.log('Ubicación:', data);
        this.ubicacion = data;
        this.inicializarMapa();
      }, error => {
        console.error('Error al obtener la ubicación:', error);
      });
  }


  inicializarMapa() {
    const latitud = parseFloat(this.ubicacion.places[0].latitude);
    const longitud = parseFloat(this.ubicacion.places[0].longitude);

    const mapaOptions = {
      center: { lat: latitud, lng: longitud },
      zoom: 12,
    };

    const mapa = new this.google.maps.Map(document.getElementById('mapa'), mapaOptions);

    const marker = new this.google.maps.Marker({
      position: { lat: latitud, lng: longitud },
      map: mapa,
      title: 'Ubicación del código postal'
    });
  }

}