import {Filter, FilterField} from "../app.component";
import {filter} from "rxjs/operators";

export class TableAdditions {
  public currentPage:number = 1
  public lastPage: number = 1
  public perPage: number = 2
  public countAll: number = 1
  public columnsForFiltering:string[] = ['name',
    'coordinates.x',
    'coordinates.y',
    'creationDate',
    'price',
    'discount',
    'comment',
    'type',
    'event.name',
    'event.date',
    'event.minAge']
  public columnsShow:string[] = ['position','edit']
  public filter:Filter = new Filter()

  getAllTableFields(){
    return this.columnsForFiltering.concat(this.columnsShow)
  }

  constructor() {
    this.columnsForFiltering.forEach(value => {
      console.log("FILTER COLUMN " + value)
      this.filter.fields.set(value,new FilterField())
    })
    console.log("FILTER " + this.filter.fields.values())
  }
}
