import { Injectable } from '@angular/core';
import { Client } from '../models/client';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  clients: [];

  constructor() { }

  addClient(value: Client): void{

    let a: Client[];
    let clientRef: number;
    a = JSON.parse(localStorage.getItem('ls_clients')) || [];
    
    for (let i = 0; i < a.length; i++){
      if (a[i].dni === value.dni){
        clientRef = 1
        break;
      } 
    }

    if (clientRef != 1) {
      a.push(value);
      Swal('Agregado!', 'Cliente agregado correctamente', 'success');
      setTimeout(() => {
        localStorage.setItem('ls_clients', JSON.stringify(a));
      }, 500);
    } else {
      Swal('Error!', 'El DNI ya existe', 'error');
    }
  }

  updateClient(value: Client): void{

    let a: Client[];
    let clientRef: any;
    a = JSON.parse(localStorage.getItem('ls_clients')) || [];
    
    for (let i = 0; i < a.length; i++){
      if (a[i].dni === value.dni){
        clientRef = i;
        break;
      } 
    }

    if (clientRef >= 0) {
      a[clientRef] = value;
      Swal('Actualizado!', 'Cliente actualizado correctamente', 'success');
      setTimeout(() => {
        localStorage.setItem('ls_clients', JSON.stringify(a));
      }, 500);
    } else {
      Swal('Error!', 'El DNI no coincide', 'error');
    }
  }

  deleteclient(client: Client) {
    const clients: Client[] = JSON.parse(localStorage.getItem('ls_clients'));
    for (let i = 0; i < clients.length; i++) {
      if (clients[i].dni === client.dni) {
        clients.splice(i, 1);
        break;
      }
    }
    localStorage.setItem('ls_clients', JSON.stringify(clients));
    Swal('Eliminado!', 'Cliente eliminado correctamente', 'success');
  }
  
}
