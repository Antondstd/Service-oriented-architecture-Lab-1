export class Coordinates {
  id:number = 0
  x:number = 0
  y:number = 0

  constructor(obj:any) {
    obj && Object.assign(this,obj)
  }
}
