import { Component, OnInit, ViewChild } from '@angular/core';
import { Client } from '../../models/client';
import { ClientService } from '../../services/client.service';
import { AddclientComponent } from '../../dialogs/addclient/addclient.component';
import { EditclientComponent } from '../../dialogs/editclient/editclient.component';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../modules/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import swal from 'sweetalert2';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [MaterialModule, CommonModule, FormsModule, ReactiveFormsModule, EditclientComponent, AddclientComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.sass']
})
export class HomeComponent implements OnInit {

  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  @ViewChild(MatTable, { static: false }) table: MatTable<any>;

  clients: Client;
  client: Client;
  dataSource: MatTableDataSource<Client>;
  displayedColumns = ['seqNo', 'name', 'surname', 'date', 'country', 'dni', 'city', 'address', 'admin'];

  constructor(
    public dialog: MatDialog,
    private clientService: ClientService,
  ) {}

  ngOnInit() {
    this.refreshClients()
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim(); // Remove whitespace
    filterValue = filterValue.toLowerCase(); // Datasource defaults to lowercase matches
    this.dataSource.filter = filterValue;
  }

  addClientDialog() {
    let dialogRef = this.dialog.open(AddclientComponent);
    dialogRef.afterClosed().subscribe(
      res => {
        setTimeout(() => {
          this.refreshClients();
        }, 500);
      },
      err => {
        console.log(err)
      }
    )
  }

  editClientDialog(clients: Client) {
    let dialogRef = this.dialog.open(EditclientComponent, { data: clients });
    dialogRef.afterClosed().subscribe(
      res => {
        setTimeout(() => {
          this.refreshClients();
          for (let i = 0; i < this.dataSource.data.length; i++) {
            if (this.dataSource.data[i].dni === clients.dni) {
              this.clients = this.dataSource.data[i];
              break;
            }
          }
          this.viewClient(this.clients);
        }, 500);
      },
      err => {
        console.log(err)
      }
    )
  }

  //Refresh Table
  refreshClients() {
    const clients: Client[] = [];
    this.clients = JSON.parse(localStorage.getItem('ls_clients')) || [];
    for (let i = 0; i < Object.keys(this.clients).length; i++) { clients.push(this.clients[i]); }
    this.dataSource = new MatTableDataSource(clients);
  }

  //View Client
  viewClient(clients: Client) {
    this.client = clients;
    return this.client;
  }

  //Deleted Client
  deleteClient(client: Client) {
    this.dataSource.data = this.dataSource.data.filter((value) => {
      this.clientService.deleteclient(client);
      this.client = null;
      this.refreshClients();
      return value.dni != client.dni;
    });
    swal(
      'Cliente eliminado',
      'El Cliente ha sido eliminado con exito',
      'success'
    )

    
  }
}
