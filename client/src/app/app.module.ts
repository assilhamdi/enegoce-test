import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { GraphQLModule } from './graphql.module';
import { DealLcComponent } from './deal-lc/deal-lc.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MappingTableComponent } from './mapping-table/mapping-table.component';
import { MappingTableManagementDrawerComponent } from './mapping-table/mapping-table-management-drawer/mapping-table-management-drawer.component';
import { MappingRulesComponent } from './mapping-table/mapping-rules/mapping-rules.component';

@NgModule({
  declarations: [
    AppComponent,
    DealLcComponent,
    MappingTableComponent,
    MappingTableManagementDrawerComponent,
    MappingRulesComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    GraphQLModule,
    FormsModule,
    BrowserAnimationsModule,
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
  ],
  providers: [
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
