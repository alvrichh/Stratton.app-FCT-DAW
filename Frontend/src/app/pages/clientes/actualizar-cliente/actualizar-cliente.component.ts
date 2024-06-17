import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Cliente } from '../cliente';
import { ClienteService } from '../cliente.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-actualizar-cliente',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './actualizar-cliente.component.html',
  styleUrl: './actualizar-cliente.component.css'
})
export class ActualizarClienteComponent implements OnInit {
  id: number;
  cliente: Cliente = new Cliente();
selectComercializadora: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clienteService: ClienteService
  ) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.clienteService.obtenerClientePorId(this.id).subscribe(data => {
      this.cliente = data;
    }, error => console.log(error));
  }

  onSubmit() {
    this.clienteService.actualizarCliente(this.id, this.cliente).subscribe(data => {
      this.irAlaListaDeClientes();
    }, error => console.log(error));
  }

  irAlaListaDeClientes() {
    this.router.navigate(['/clientes']);
  }
}