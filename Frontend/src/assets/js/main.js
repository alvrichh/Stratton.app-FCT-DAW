// Import our custom CSS
import '../scss/styles.scss'
// Import all of Bootstrap's JS
import * as bootstrap from 'bootstrap'

document.addEventListener("DOMContentLoaded", function () {

// Cambiar icono de sol a luna
document.getElementById('toggleContrastButton').addEventListener('click', function () {
  document.body.classList.toggle('high-contrast');
  const contrastIcon = document.getElementById('contrastIcon');
  const isHighContrast = document.body.classList.contains('high-contrast');

  // Cambiar el icono de sol a luna y viceversa
  contrastIcon.classList.toggle('bi-moon', isHighContrast);
  contrastIcon.classList.toggle('bi-sun', !isHighContrast);

  // Cambiar la imagen con el toggle
  const svgImage = document.getElementById('svg');
  svgImage.src = isHighContrast ? 'images/letrero-dark.svg' : 'images/letrero.svg';
  // Puedes agregar más elementos que quieras cambiar aquí
});
  //ANIMACION CAMPANITA===================================
  let campana = document.getElementById("campana");
  campana.addEventListener("mouseenter", function () {
    campana.classList.add("animate__swing");
  });
  campana.addEventListener("mouseleave", function () {
    campana.classList.remove("animate__swing");
  });

  // Añadir Toast al hacer clic en la campana de notificaciones
  document.getElementById('notificaciones').addEventListener('click', function () {
    // Crear un nuevo Toast
    var toast = new bootstrap.Toast(document.querySelector('.toast'));

    // Mostrar el Toast
    toast.show();
  });


  //ANIMACIÓN LIKE Y CONTADOR================================================
  document.querySelectorAll('.btn-heart').forEach(function (button) {
    button.addEventListener('click', function (event) {
      like(event.currentTarget);
    });
  });

  // Función para cambiar la clase del icono de corazón a "fill" o viceversa
  function like(button) {
    let heartIcon = button.querySelector('i.bi-heart-fill');
    heartIcon.classList.toggle('fill'); // Añadir o quitar la clase 'fill'
    //contador de articulos a like
    let fillElements = document.querySelectorAll('.fill');
    let fillCount = fillElements.length;
    let fillCountBadge = document.getElementById('fillCountBadge');
    if (fillCountBadge) {
      fillCountBadge.textContent = fillCount.toString();
    }
  }


  //VIDEO=============================================
  let containerVideo = document.getElementById("video");
  var video = document.getElementById("myVideo");

  // Get the button
  var btn = document.getElementById("myBtn");

  let cerrar = document.getElementById("close");

  // Pause and play the video, and change the button text
  function myFunction() {
    if (video.paused) {
      video.play();
      btn.innerHTML = "Pause";
    } else {
      video.pause();
      btn.innerHTML = "Play";
    }
  }

  btn.addEventListener("click", myFunction);

  cerrar.addEventListener("click", cerrarVideo);
  function cerrarVideo() {
    containerVideo.style.display = "none";
  }

  /*

  document.getElementById("newsletter").addEventListener("click", confeti);

  function confeti(){
    confetti({
      particleCount: 100,
      startVelocity: 30,
      spread: 360,
      origin: {
        x: Math.random(),
        // since they fall down, start a bit higher than random
        y: Math.random() - 0.2
      }
    });
  }*/
});