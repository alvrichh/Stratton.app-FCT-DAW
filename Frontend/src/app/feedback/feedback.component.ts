import { Component } from '@angular/core';
import { CargarScriptsService } from '../cargar-scripts.service';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.css'
})

export class FeedbackComponent {

  script: any;

  constructor(private _CargarScripts:CargarScriptsService){
    _CargarScripts.Cargar(["script"]);
  }  

}