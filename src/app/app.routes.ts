import { Routes } from '@angular/router';
 
import { Dashboard } from './dashboard/dashboard';
import { Inventory } from './inventory/inventory';
import { Productionnroling } from './productionnroling/productionnroling';
import { DispatchandpackingComponent } from './dispatchandpacking/dispatchandpacking';
import { PackingComponent } from './packing/packing';
import { Jobwork } from './jobwork/jobwork';
import { Reportandanalytics } from './reportandanalytics/reportandanalytics';
import { Rawmaterials } from './rawmaterials/rawmaterials';
import { PurchaseOrder } from './purchase-order/purchase-order';
import { LOIComponent } from './loi/loi';
 
export const routes: Routes = [
    { path: '', redirectTo: 'dash', pathMatch: 'full' },
    {path:'dash',component:Dashboard},
    {path:'addpo', component:PurchaseOrder},
    {path:'rawmaterial',component:Rawmaterials},
    {path:'inventory',component:Inventory},
    {path:'loi',component:LOIComponent},
    {path:'production',component:Productionnroling},
    {path:'packing',component:PackingComponent},
    {path:'dispatch',component:DispatchandpackingComponent},
    {path:'jobwork',component:Jobwork},
    {path:'reports',component:Reportandanalytics},
    { path: 'po', component: PurchaseOrder },  
 
 
   
];