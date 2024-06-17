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
  
  function toggleSearch() {
    var icon = document.querySelector('.search-icon');
    var input = document.getElementById('searchInput');
    var filter = input.value.toUpperCase();
    var table = document.getElementById('tableBody');
    var rows = table.getElementsByTagName('tr');

    icon.classList.add('small');
    setTimeout(function() {
      icon.innerHTML = '🔎';
    }, 200);

    // Loop through all table rows, and hide those who don't match the search query
    for (var i = 0; i < rows.length; i++) {
      var cells = rows[i].getElementsByTagName('td');
      var found = false;
      for (var j = 0; j < cells.length; j++) {
        var cell = cells[j];
        if (cell) {
          var txtValue = cell.textContent || cell.innerText;
          if (txtValue.toUpperCase().indexOf(filter) > -1) {
            found = true;
            break;
          }
        }
      }
      if (found) {
        rows[i].style.display = '';
      } else {
        rows[i].style.display = 'none';
      }
    }
  }
   // Obtener el contexto del lienzo (canvas)
   var ctx = document.getElementById('grafico').getContext('2d');
  
   // Datos aleatorios para los minutos en llamada, espera y pausa
   var minutosLlamada = [25, 40, 30, 20, 45];
   var minutosEspera = [15, 20, 35, 10, 25];
   var minutosPausa = [10, 5, 15, 8, 12];
   var nombresEmpleados = ['Empleado 1', 'Empleado 2', 'Empleado 3', 'Empleado 4', 'Empleado 5'];
 
   // Crear el gráfico de barras
   var chart = new Chart(ctx, {
     type: 'bar',
     data: {
       labels: nombresEmpleados,
       datasets: [
         {
           label: 'Minutos en Llamada',
           backgroundColor: 'rgba(255, 99, 132, 0.5)',
           data: minutosLlamada
         },
         {
           label: 'Minutos en Espera',
           backgroundColor: 'rgba(255, 206, 86, 0.5)',
           data: minutosEspera
         },
         {
           label: 'Minutos en Pausa',
           backgroundColor: 'rgba(54, 162, 235, 0.5)',
           data: minutosPausa
         }
       ]
     },
     options: {
       scales: {
         yAxes: [{
           ticks: {
             beginAtZero: true
           }
         }]
       }
     }
   });
   gsap.set('.main', {position:'fixed', background:'#fff', width:'100%', maxWidth:'1200px', height:'100%', top:0, left:'50%', x:'-50%'})
   gsap.set('.scrollDist', {width:'100%', height:'200%'})
   gsap.timeline({scrollTrigger:{trigger:'.scrollDist', start:'top top', end:'bottom bottom', scrub:1}})
       .fromTo('.sky', {y:0},{y:-200}, 0)
       .fromTo('.cloud1', {y:100},{y:-800}, 0)
       .fromTo('.cloud2', {y:-150},{y:-500}, 0)
       .fromTo('.cloud3', {y:-50},{y:-650}, 0)
       .fromTo('.mountBg', {y:-10},{y:-100}, 0)
       .fromTo('.mountMg', {y:-30},{y:-250}, 0)
       .fromTo('.mountFg', {y:-50},{y:-600}, 0)
   
   $('#arrowBtn').on('mouseenter', (e)=>{ gsap.to('.arrow', {y:10, duration:0.8, ease:'back.inOut(3)', overwrite:'auto'}); })
   $('#arrowBtn').on('mouseleave', (e)=>{ gsap.to('.arrow', {y:0, duration:0.5, ease:'power3.out', overwrite:'auto'}); })
   $('#arrowBtn').on('click', (e)=>{ gsap.to(window, {scrollTo:innerHeight, duration:1.5, ease:'power1.inOut'}); }) // scrollTo requires the ScrollTo plugin (not to be confused w/ ScrollTrigger)
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