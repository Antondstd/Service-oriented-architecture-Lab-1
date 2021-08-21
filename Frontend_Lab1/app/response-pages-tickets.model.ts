import {Ticket} from "./Model/ticket.model";

export class ResponsePagesTickets {
  currentPage:number = 1
  lastPage:number = 1
  perPage:number = 25
  countAll:number = 1
  tickets: Array<Ticket> = []
}
