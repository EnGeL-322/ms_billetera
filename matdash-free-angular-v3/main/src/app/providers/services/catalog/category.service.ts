import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { END_POINTS } from '../../utils/end-points';
import {EntityDataService} from "../../utils/entity-data";


@Injectable({ providedIn: 'root' })
export class CategoryService extends EntityDataService<any[]> {

  constructor(protected override httpClient: HttpClient) {
    super(httpClient, END_POINTS.category);
  }


}
