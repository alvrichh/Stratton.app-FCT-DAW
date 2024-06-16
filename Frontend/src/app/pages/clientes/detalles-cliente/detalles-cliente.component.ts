import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteService } from '../cliente.service';
import { Cliente } from '../cliente';

@Component({
  selector: 'app-detalles-cliente',
  standalone: true,
  imports: [],
  templateUrl: './detalles-cliente.component.html',
  styleUrl: './detalles-cliente.component.css'
})

export class DetallesClienteComponent implements OnInit {
  id: number;
  cliente: Cliente;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clienteService: ClienteService
  ) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.clienteService.obtenerClientePorId(this.id).subscribe(
      data => {
        this.cliente = data;
      },
      error => console.log(error)
    );
  }

  regresar() {
    this.router.navigate(['/clientes']);
  }
}