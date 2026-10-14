import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../cliente.service';
import { Router, RouterLink } from '@angular/router';
import { Cliente } from '../cliente';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../auth.service';
import { FormsModule } from '@angular/forms';
import { Alertas } from '../../../core/alertas';
import { CatalogoService } from '../../../services/catalogo.service';
import { Comercializadora } from '../../../services/catalogo.model';

type ColumnaOrden = 'empleadoNombre' | 'apellidos' | 'dni' | 'telefono' | 'companiaContratada' | 'fechaSubidaContrato';

@Component({
  selector: 'app-lista-clientes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './lista-clientes.component.html',
  styleUrl: './lista-clientes.component.css',
})
export class ListaClientesComponent implements OnInit {
  clientes: Cliente[] = [];
  filteredClientes: Cliente[] = [];
  comercializadoras: Comercializadora[] = [];
  cargando = true;
  error: string | null = null;

  searchText = '';
  selectedCompania = '';
  orderBy: ColumnaOrden = 'apellidos';
  order: 'asc' | 'desc' = 'asc';

  constructor(
    private clienteService: ClienteService,
    private catalogo: CatalogoService,
    private router: Router,
    public authService: AuthService
  ) {}

  get esJefe(): boolean {
    return this.authService.isJefe();
  }

  ngOnInit(): void {
    this.catalogo.obtenerComercializadoras().subscribe({ next: c => this.comercializadoras = c, error: () => this.comercializadoras = [] });
    this.obtenerClientes();
  }

  obtenerClientes(): void {
    this.cargando = true;
    this.error = null;
    this.clienteService.obtenerListaDeClientes().subscribe({
      next: (data) => {
        this.clientes = data;
        this.cargando = false;
        this.applyFilter();
      },
      error: (err) => {
        this.cargando = false;
        this.error = Alertas.mensajeDe(err);
      }
    });
  }

  /** Normaliza texto para buscar sin tildes ni mayúsculas. */
  private normalizar(texto: unknown): string {
    return String(texto ?? '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();
  }

  applyFilter(): void {
    const texto = this.normalizar(this.searchText.trim());
    let resultado = this.clientes;
    if (this.selectedCompania) {
      resultado = resultado.filter(c => c.companiaContratada === this.selectedCompania);
    }
    if (texto) {
      resultado = resultado.filter(c =>
        this.normalizar(`${c.nombre} ${c.apellidos}`).includes(texto) ||
        this.normalizar(c.dni).includes(texto) ||
        this.normalizar(c.telefono).includes(texto) ||
        this.normalizar(c.email).includes(texto) ||
        this.normalizar(c.cups).includes(texto) ||
        this.normalizar(c.companiaContratada).includes(texto) ||
        this.normalizar(c.empleadoNombre).includes(texto)
      );
    }
    this.filteredClientes = this.ordenar([...resultado]);
  }

  sort(column: ColumnaOrden): void {
    if (this.orderBy === column) {
      this.order = this.order === 'asc' ? 'desc' : 'asc';
    } else {
      this.orderBy = column;
      this.order = 'asc';
    }
    this.filteredClientes = this.ordenar([...this.filteredClientes]);
  }

  private ordenar(lista: Cliente[]): Cliente[] {
    const col = this.orderBy;
    const dir = this.order === 'asc' ? 1 : -1;
    return lista.sort((a, b) => {
      const va = this.normalizar(a[col]);
      const vb = this.normalizar(b[col]);
      return va.localeCompare(vb, 'es', { numeric: true }) * dir;
    });
  }

  logoDe(cliente: Cliente): string | undefined {
    return cliente.logo || this.comercializadoras.find(c => c.companiaContratada === cliente.companiaContratada)?.logo;
  }

  verCliente(id: number): void {
    this.router.navigate(['/detalles-cliente', id]);
  }

  actualizarCliente(id: number): void {
    this.router.navigate(['/actualizar-cliente', id]);
  }

  async eliminarCliente(cliente: Cliente): Promise<void> {
    const confirmado = await Alertas.confirmar('¿Eliminar cliente?',
      `Se eliminará a ${cliente.nombre} ${cliente.apellidos} y sus ${cliente.suministros?.length ?? 0} suministro(s).`, 'Sí, eliminar');
    if (!confirmado) {
      return;
    }
    this.clienteService.eliminarCliente(cliente.idCliente).subscribe({
      next: () => {
        Alertas.exito('Cliente eliminado', 'El cliente se ha eliminado correctamente');
        this.obtenerClientes();
      },
      error: (err) => Alertas.error(err, 'No se pudo eliminar el cliente'),
    });
  }
}
