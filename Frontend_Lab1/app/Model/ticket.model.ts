import {Time} from "@angular/common";
import {Coordinates} from "./coordinates.model";
import {Event} from "./event.model";
// @ts-ignore
import * as dateformat from 'dateformat';

export class Ticket {
  id:number = 0;
  name:String = "";
  coordinates:Coordinates;
  creationDate:String = "";
  price:number = 0;
  discount:number = 0;
  comment:String = "";
  type:TicketType = TicketType.USUAL;
  event:Event;

  constructor(obj:any) {
    obj && Object.assign(this,obj)
    this.coordinates = new Coordinates(obj.coordinates)
    this.event = new Event(obj.event)
  }

  setCreationDateFromDate(date:Date){
    this.creationDate = dateformat(date, "HH:MM:ss dd/mm/yy")
  }

  // constructor(obj:any) {
  //   console.log("IN CONSTRUCTOR")
  //   console.log(obj)
  //   this.id = obj.id
  //   this.name = obj.name
  //   this.coordinates = new Coordinates()
  //   this.coordinates.id = obj.coordinates.id
  //   this.coordinates.x = obj.coordinates.x
  //   this.coordinates.y = obj.coordinates.y
  //   this.creationDate = obj.creationDate
  //   this.price = obj.price
  //   this.discount = obj.discount
  //   this.comment = obj.comment
  //   this.type = obj.type
  //   this.event = new Event()
  //   this.event.id = obj.event.id
  //   this.event.eventType = obj.event.eventType
  //   this.event.ticketsCount = obj.event.ticketsCount
  //   this.event.name = obj.event.name
  // }
}

export enum TicketType {
  VIP= "VIP",
  USUAL = "USUAL",
  BUDGETARY = "BUDGETARY",
  CHEAP = "CHEAP"
// VIP= "Vip",
//   USUAL = "Usual",
//   BUDGETARY = "Budgetary",
//   CHEAP = "Cheap"
}
