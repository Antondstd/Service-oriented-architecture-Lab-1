export class Event {
  id:number = 0
  name:String = ""
  date:String = ""
  minAge:number = 0
  // ticketsCount:number = 0
  // eventType:EventType = EventType.CONCERT

  constructor(obj:any) {
    obj && Object.assign(this,obj)
  }
}

export enum EventType {
  CONCERT= "CONCERT",
  E_SPORTS = "E_SPORTS",
  BASEBALL = "BASEBALL",
  THEATRE_PERFORMANCE = "THEATRE_PERFORMANCE"
// CONCERT= "Concert",
//   E_SPORTS = "E_Sports",
//   BASEBALL = "Baseball",
//   THEATRE_PERFORMANCE = "Theatre Performance"
}
