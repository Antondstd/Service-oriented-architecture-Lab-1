import {Injectable} from '@angular/core';
import {Ticket} from "./Model/ticket.model";
// @ts-ignore
import * as xml2js from 'xml2js';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Filter} from "./app.component";
import {FilterField} from "./app.component";
// @ts-ignore
import * as dateformat from 'dateformat';

@Injectable({
  providedIn: 'root'
})
export class TicketsService {

  constructor(private http: HttpClient) {
  }

  addTicket(ticket: Ticket): Observable<any> {
    var builder = new xml2js.Builder({'rootName': 'Ticket'});
    var bodyXML = builder.buildObject(ticket);
    const myHeaders = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded;charset=utf-8');
    return this.http.put("http://localhost:8080/SOA_Backend_Lab1-1.0-SNAPSHOT/api/ticket", bodyXML, {
      headers: myHeaders,
      observe: 'response',
      responseType: "text"
    })

  }

  getTickets(page: number, ticketsPerPage: number, sortState: Array<String>, filter: Filter): Observable<String> { //длинный список параметров
    const myHeaders = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded;charset=utf-8');
    var params = new HttpParams().append('page', page)
      .append('perPage', ticketsPerPage)
      .append('sortState', sortState.join(','));
    if (filter.isActive) {
      filter.fields.forEach((field: FilterField, key: String) => {
          if (field.isActive && field.data != null && field.data != "")
            if (key == "event.date" || key == "creationDate")
              if (key == "event.date")
                params = params.append(key.toString(), dateformat(field.data.toString(), "HH:MM:ss dd/mm/yy"))
                else
                params = params.append(key.toString(), dateformat(field.data.toString(), "HH:MM:ss dd/mm/yy z"))
            else
              params = params.append(key.toString(), field.data.toString())
        }
      )
    }
    return this.http.get("http://localhost:8080/SOA_Backend_Lab1-1.0-SNAPSHOT/api/ticket", {
      headers: myHeaders,
      params: params,
      responseType: "text"
    })
  }

  updateTicket(ticket: Ticket): Observable<any> {
    var builder = new xml2js.Builder({'rootName': 'Ticket'});
    var bodyXML = builder.buildObject(ticket);
    const myHeaders = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded;charset=utf-8');
    var params = new HttpParams().append('id', ticket.id);
    return this.http.post("http://localhost:8080/SOA_Backend_Lab1-1.0-SNAPSHOT/api/ticket", bodyXML, {
      headers: myHeaders,
      params: params,
      observe: 'response',
      responseType: "text"
    })
  }

  deleteTicket(id: number): Observable<any> {
    const myHeaders = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded;charset=utf-8');
    var params = new HttpParams().append('id', id);
    return this.http.delete("http://localhost:8080/SOA_Backend_Lab1-1.0-SNAPSHOT/api/ticket", {
      headers: myHeaders,
      params: params,
      observe: 'response'
    })
  }
}
