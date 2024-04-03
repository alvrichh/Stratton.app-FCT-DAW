import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuBolaComponent } from './menu-bola/menu-bola.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MenuBolaComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'proyecto-arsync';
}
