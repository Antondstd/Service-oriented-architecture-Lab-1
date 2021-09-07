import {ChangeDetectorRef, Component, Inject, ViewChild} from '@angular/core';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatCalendarCellClassFunction} from '@angular/material/datepicker';
import {Event, EventType} from "./Model/event.model";
import {Ticket, TicketType} from "./Model/ticket.model";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
// @ts-ignore
import * as xml2js from 'xml2js';
import * as moment from 'moment';
// @ts-ignore
import * as dateformat from 'dateformat';
import {TicketsService} from "./tickets.service";
import {newArray} from "@angular/compiler/src/util";
import {MatTable} from "@angular/material/table";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {MatSort, Sort} from "@angular/material/sort";
import {ResponsePagesTickets} from "./response-pages-tickets.model";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  filter: Filter = new Filter()
  title = 'SOA_Lab1'
  tickets: Array<Ticket> = []
  sortStateArray: Array<String> = []
  sortedState: Map<String, String> = new Map()
  currentPage: number = 1 //Замена значения данных = объектом
  lastPage: number = 1 //Замена значения данных = объектом
  perPage: number = 2 //Замена значения данных = объектом
  countAll: number = 1 //Замена значения данных = объектом
  displayedColumns: string[] = ['position', //Замена значения данных = объектом
    'id',
    'name',
    'coordinates.x',
    'coordinates.y',
    'creationDate',
    'price',
    'discount',
    'comment',
    'type',
    'event.name',
    'event.date',
    'event.minAge',
    'edit']
  // @ts-ignore
  typeRanges = Object.values(TicketType)
  selectedTypeRange = this.typeRanges[0]
  // @ts-ignore
  eventTypeRanges = Object.values(EventType)
  selectedEventTypeRange = this.eventTypeRanges[0]

  @ViewChild(MatTable, {static: false})
  ticketsTable!: MatTable<any>

  @ViewChild(MatSort) sort!: MatSort;

  applyFilter($event: KeyboardEvent) {

  }

  constructor(public dialog: MatDialog,
              private ticketService: TicketsService,
              private changeDetectorRefs: ChangeDetectorRef,
              private _snackBar: MatSnackBar) {
    console.log(this.eventTypeRanges)
    this.getTickets()
    this.filter.fields.set("name", new FilterField())
    this.filter.fields.set("coordinates.x", new FilterField())
    this.filter.fields.set("coordinates.y", new FilterField())
    this.filter.fields.set("creationDate", new FilterField())
    this.filter.fields.set("price", new FilterField())
    this.filter.fields.set("discount", new FilterField())
    this.filter.fields.set("comment", new FilterField())
    this.filter.fields.set("type", new FilterField())
    this.filter.fields.set("event.name", new FilterField())
    this.filter.fields.set("event.date", new FilterField())
    this.filter.fields.set("event.minage", new FilterField())
  }

  getTickets() {
    this.ticketService.getTickets(this.currentPage, this.perPage, this.sortStateArray, this.filter).subscribe(((response: String) => { //Длинный список параметров
      xml2js.parseString(response, {explicitArray: false}, (error: string | undefined, result: any) => {
        if (error) {
          console.log(error)
        } else {
          let responsePagesTickets = result.ResponsePagesTickets
          console.log("WE GOT TICKETS");
          console.log(response)
          console.log(result)
          console.log(result.ResponsePagesTickets)
          this.currentPage = responsePagesTickets.currentPage
          this.lastPage = responsePagesTickets.lastPage
          this.perPage = responsePagesTickets.perPage
          this.countAll = responsePagesTickets.countAll
          if (responsePagesTickets.tickets != "") {
            if (responsePagesTickets.tickets.Ticket.constructor == Array) {
              this.tickets = responsePagesTickets.tickets.Ticket
            } else {
              let ticket = new Ticket(responsePagesTickets.tickets.Ticket)
              this.tickets = [ticket]
            }
          } else
            this.tickets = []
          this.ticketsTable.renderRows()
        }
      });
    }))
  }

  deleteTicket(position: number) {
    let curId = this.tickets[position].id
    this.ticketService.deleteTicket(this.tickets[position].id).subscribe((data: Response) => {
        console.log(data)
        if (data.ok)
          console.log("DATA OK")
        if (data.ok && curId == this.tickets[position].id) {
          this.tickets.splice(position, 1)
          console.log("SPLICED")
          this.ticketsTable.renderRows()
        }
        console.log(this.sort.sortables.get("id"))
        // let newTicket:Array<Ticket> = []
        // newTicket.concat(this.tickets)
        // this.tickets = newTicket
        // console.log(this.tickets)
      }
    )
  }

  openAddTicketDialog(isNew: boolean, position: number): void {
    let ticket = new Ticket(Object())
    if (!isNew)
      ticket = this.tickets[position]
    const dialogRef = this.dialog.open(AddTicketDialog, {
      width: '450px',
      data: {isNew: isNew, ticket: ticket, message: ""}
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      if (result)
        this.getTickets()
    });
  }

  dateClass: MatCalendarCellClassFunction<Date> = (cellDate, view) => {
    // Only highligh dates inside the month view.
    if (view === 'month') {
      const date = cellDate.getDate()

      // Highlight the 1st and 20th day of each month.
      return (date === 1 || date === 20) ? 'example-custom-date-class' : ''
    }

    return ''
  }

  openSnackBarMessage(message: string) {
    this._snackBar.open(message, "close", {
      duration: 2 * 1000,
      horizontalPosition: 'left',
      verticalPosition: 'bottom',
    });
  }

  sortData(sort: Sort) {
    if (sort.direction == "")
      this.sortedState.delete(sort.active)
    else if (!this.sortedState.has(sort.active)) {
      this.sortedState.set(sort.active, sort.direction)
    } else {
      this.sortedState.delete(sort.active)
      this.sortedState.set(sort.active, sort.direction)
    }
    this.sortStateArray.length = 0
    this.sortedState.forEach((value: String, key: String) =>
      this.sortStateArray.unshift(value + "(" + key + ")")
    )
    this.getTickets()
    console.log(this.sortStateArray)
  }

  paginator(event: PageEvent) {
    this.currentPage = event.pageIndex + 1
    console.log("NOW PageIndex is " + event.pageIndex)
    this.getTickets()
  }
}

@Component({
  selector: 'add.ticket.dialog',
  templateUrl: 'add.ticket.dialog.html', styleUrls: ['./app.component.css']
})
export class AddTicketDialog {
  ticket: Ticket = new Ticket(Object())
  typeRanges = Object.values(TicketType)
  eventTypeRanges = Object.values(EventType)
  isNew: Boolean = true
  curDate: Date = new Date()
  // eventDate: Date = new Date()
  eventDate: String = new Date().toISOString().slice(0, 16)
  parser = new xml2js.Parser();
  builder = new xml2js.Builder()
  SNACK_BAR_DURATION = 4 * 1000

  constructor(
    public dialogRef: MatDialogRef<AddTicketDialog>,
    private ticketService: TicketsService,
    private _snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.ticket = new Ticket(data.ticket)
    this.isNew = data.isNew
    if (!this.isNew) {
      // this.eventDate = new Date(moment(data.ticket.event.date.toString(),'H:m:s dd/MM/yy',true).format())
      let mom = moment(data.ticket.event.date.toString(), 'HH:mm:ss DD/MM/YY z', true).format("YYYY-MM-DDTHH:mm z")
      // this.eventDate = new Date(mom)
    }
  }

  upload(): void {
    console.log(this.ticket)
    // console.log(this.builder.build(this.ticket))
    var builder = new xml2js.Builder({'rootName': 'Ticket'});
    this.ticket.creationDate = dateformat(this.curDate, "HH:MM:ss dd/mm/yy") //extract method
    this.ticket.event.date = dateformat(this.eventDate, "HH:MM:ss dd/mm/yy") //extract method
    var xml = builder.buildObject(this.ticket);
    console.log(xml)

    this.ticketService.addTicket(this.ticket).toPromise().then((response: Response) => {
      this.showMessageAndCloseDialog(response.ok)
    })
  }

  showMessageAndCloseDialog(responseAnswer: Boolean) {
    if (responseAnswer) {
      this.openSnackBarMessage("Successfully added new Ticket")
      this.dialogRef.close(true);
    } else {
      this.openSnackBarMessage("Wasn't able to add the Ticket :(")
      this.dialogRef.close(false);
    }
  }

  update(): void {
    console.log(this.ticket)
    // console.log(this.builder.build(this.ticket))
    var builder = new xml2js.Builder({'rootName': 'Ticket'});
    this.ticket.creationDate = dateformat(this.curDate, "HH:MM:ss dd/mm/yy z")
    console.log("DATA AAAA : " + this.eventDate)
    this.ticket.event.date = dateformat(this.eventDate, "HH:MM:ss dd/mm/yy")
    var xml = builder.buildObject(this.ticket);
    console.log(xml)
    this.ticketService.updateTicket(this.ticket).toPromise().then((response: Response) => {
      this.showMessageAndCloseDialog(response.ok)
    })
    this.dialogRef.close();
  }

  openSnackBarMessage(message: string) {
    this._snackBar.open(message, "close", {
      duration: this.SNACK_BAR_DURATION,
      horizontalPosition: 'left',
      verticalPosition: 'bottom',
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}

export interface DialogData {
  isNew: boolean;
  ticket: Ticket;
  message: String;
}

export class Filter {
  fields: Map<String, FilterField> = new Map<String, FilterField>()
  isActive: Boolean = true
}

export class FilterField {
  data: any = null
  isActive: Boolean = true
}

