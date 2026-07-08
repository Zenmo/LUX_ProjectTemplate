double f_createInterface_ProjectTemplate()
{/*ALCODESTART::1726134544288*/
//OVERRIDE THE zero_Interface parameter here
zero_Interface = interface_ProjectTemplate;
/*ALCODEEND*/}

double f_readDatabase_ProjectTemplate()
{/*ALCODESTART::1726156899337*/
//Set the profiles data record
f_setDefaultProfiles_data();
//f_setChargerProfiles_data();
//f_setPBL_data();
//f_setDHWProfile_data();

//Set the GridNode data record
f_setGridNode_data();

//Set the consumption buildings data
f_setBuilding_data();

//Set the EA data
//f_setSolarfarm_data();
//f_setWindfarm_data();
//f_setElectrolyser_data();
//f_setBattery_data();
//f_setChargingstation_data();

//Set the remaining gis object data
f_setNeighbourhood_data();
//f_setParkingSpace_data();
//f_setParcel_data();
//f_setCable_data();

/*ALCODEEND*/}

Building_data f_buildingRecordBuilder(Tuple row)
{/*ALCODESTART::1726157131143*/
//Create a building_data record
Building_data building_data_record = Building_data.builder()

.address_id(row.get( buildings.address_id ))
.building_id(row.get( buildings.building_id ).replace("panduid.",""))
.streetname(row.get( buildings.streetname ))
.house_number(row.get( buildings.house_number ))
.house_letter(row.get( buildings.house_letter ))
.house_addition(row.get( buildings.house_addition ))
.postalcode(row.get( buildings.postalcode ))
.city(row.get( buildings.city ))
.build_year(row.get( buildings.build_year ))	
.status(row.get( buildings.status ))
.purpose(row.get( buildings.purpose ))
.address_floor_surface_m2(row.get( buildings.address_floor_surface_m2 ))
.polygon_area_m2(row.get( buildings.polygon_area_m2 ))
//.gc_id(row.get( buildings.gc_id ))
.annotation(row.get( buildings.annotation ))
.extra_info(row.get( buildings.extra_info ))
.gridnode_id(row.get( buildings.gridnode_id ))
.latitude(row.get( buildings.latitude ))
.longitude(row.get( buildings.longitude ))
.polygon(row.get( buildings.polygon ))

//For Houses
//.contracted_capacity_kw(row.get( buildings.contracted_capacity_kw ))
//.electricity_consumption_kwhpa(row.get( buildings.electricity_consumption_kwhpa ))
//.gas_consumption_m3pa(row.get( buildings.gas_consumption_m3pa ))
//.energy_label(J_PBLUtil.getEnergyLabelOption(row.get( buildings.energy_label )))
//.pv_installed_kwp(pv_installed_kwp)
//.pv_potential_kwp(pv_potential_kwp)
//.has_private_parking(has_private_parking)

//For PBL heating
/*
.use_pbl_data(true)
.dwelling_type(J_PBLUtil.getPBLDwellingTypeOption(row.get( buildings.dwelling_type )))
.ownership_type(J_PBLUtil.getPBLOwnershipTypeOption(row.get( buildings.ownership_type )))
.insulation_label(J_PBLUtil.getInsulationLabelOption(row.get( buildings.insulation_label )))
.local_factor(row.get( buildings.local_factor ))
.regional_climate_correction_factor(row.get( buildings.regional_climate_correction_factor ))

//PBL assets
.cooking_type(J_PBLUtil.getPBLCookingTypeOptionFromPBLData(row.get(buildings.cooking_type)))
.heating_type(J_PBLUtil.getHeatingTypeOptionFromPBLData(row.get(buildings.heating_type)))
*/

.build();

return building_data_record;
/*ALCODEEND*/}

double f_setGridNode_data()
{/*ALCODESTART::1726221369773*/
List<Tuple> rows = selectFrom(gridnodes).where(gridnodes.status).list();
		
for(Tuple row: rows){
	c_gridNode_data.add(GridNode_data.builder()
	
	.gridnode_id(row.get( gridnodes.gridnode_id ))
	.fid(row.get( gridnodes.fid ))
	.status(row.get( gridnodes.status ))
	.type(OL_GridNodeType.valueOf(row.get( gridnodes.type )))
	.description(row.get( gridnodes.description ))
	.latitude(row.get( gridnodes.latitude ))
	.longitude(row.get( gridnodes.longitude ))
	.parent_node_id(row.get( gridnodes.parent_node_id ))
	.is_capacity_available(row.get( gridnodes.is_capacity_available ))
	.capacity_kw(row.get( gridnodes.capacity_kw ))
	//.subscope(row.get( gridnodes.subscope ))
	//.service_area_polygon(row.get( gridnodes.service_area_polygon ))
	.build());
}
/*ALCODEEND*/}

double f_setCable_data()
{/*ALCODESTART::1727177706089*/
//Cables
List<Tuple> rowsCables = selectFrom(cables).list();

for(Tuple rowCable : rowsCables){

c_cable_data.add(Cable_data.builder()
	.fid(rowCable.get(cables.fid))
	.type(OL_GISObjectType.valueOf(rowCable.get(cables.type)))
	.status(rowCable.get(cables.status))
	.nominal_voltage_v(rowCable.get(cables.nominal_voltage_v))
	.label(rowCable.get(cables.label))
	.description(rowCable.get(cables.description))
	//.latitude(rowCable.get(cables.latitude))
	//.longitude(rowCable.get(cables.longitude))
	.line(rowCable.get(cables.line))
	.build());
}
/*ALCODEEND*/}

double f_setSolarfarm_data()
{/*ALCODESTART::1727177706091*/
List<Tuple> rows = selectFrom(solarfarms).list();

for(Tuple row : rows){
	c_solarfarm_data.add(Solarfarm_data.builder()
	
	.gc_id(row.get(solarfarms.gc_id))
	.gc_name(row.get(solarfarms.gc_name))
	.owner_id(row.get(solarfarms.owner_id))
	.streetname(row.get(solarfarms.streetname))
	.house_number(row.get(solarfarms.house_number))
	.house_letter(row.get(solarfarms.house_letter))
	.house_addition(row.get(solarfarms.house_addition))
	.postalcode(row.get(solarfarms.postalcode))
	.city(row.get(solarfarms.city))
	.gridnode_id(row.get(solarfarms.gridnode_id))
	.initially_active(row.get(solarfarms.initially_active))
	.capacity_electric_kw(row.get(solarfarms.capacity_electric_kw))
	.connection_capacity_kw(row.get(solarfarms.connection_capacity_kw))
	.contracted_delivery_capacity_kw(row.get(solarfarms.contracted_delivery_capacity_kw))
	.contracted_feed_in_capacity_kw(row.get(solarfarms.contracted_feed_in_capacity_kw))
	.latitude(row.get(solarfarms.latitude))
	.longitude(row.get(solarfarms.longitude))
	.polygon(row.get(solarfarms.polygon))
	.build());
}
/*ALCODEEND*/}

double f_setWindfarm_data()
{/*ALCODESTART::1727177706093*/
List<Tuple> rows = selectFrom(windfarms).list();

for(Tuple row : rows){
	c_windfarm_data.add(Windfarm_data.builder()
	
	.gc_id(row.get(windfarms.gc_id))
	.gc_name(row.get(windfarms.gc_name))
	.owner_id(row.get(windfarms.owner_id))
	.streetname(row.get(windfarms.streetname))
	.house_number(row.get(windfarms.house_number))
	.house_letter(row.get(windfarms.house_letter))
	.house_addition(row.get(windfarms.house_addition))
	.postalcode(row.get(windfarms.postalcode))
	.city(row.get(windfarms.city))
	.gridnode_id(row.get(windfarms.gridnode_id))
	.initially_active(row.get(windfarms.initially_active))
	.capacity_electric_kw(row.get(windfarms.capacity_electric_kw))
	.connection_capacity_kw(row.get(windfarms.connection_capacity_kw))
	.contracted_delivery_capacity_kw(row.get(windfarms.contracted_delivery_capacity_kw))
	.contracted_feed_in_capacity_kw(row.get(windfarms.contracted_feed_in_capacity_kw))
	.latitude(row.get(windfarms.latitude))
	.longitude(row.get(windfarms.longitude))
	.polygon(row.get(windfarms.polygon))
	.build());
}
/*ALCODEEND*/}

double f_setElectrolyser_data()
{/*ALCODESTART::1727177706095*/
List<Tuple> rows = selectFrom(electrolysers).list();

for(Tuple row : rows){
	c_electrolyser_data.add(Electrolyser_data.builder()
	
	.gc_id(row.get(electrolysers.gc_id))
	.gc_name(row.get(electrolysers.gc_name))
	.owner_id(row.get(electrolysers.owner_id))
	.streetname(row.get(electrolysers.streetname))
	.house_number(row.get(electrolysers.house_number))
	.house_letter(row.get(electrolysers.house_letter))
	.house_addition(row.get(electrolysers.house_addition))
	.postalcode(row.get(electrolysers.postalcode))
	.city(row.get(electrolysers.city))
	.gridnode_id(row.get(electrolysers.gridnode_id))
	.initially_active(row.get(electrolysers.initially_active))
	.capacity_electric_kw(row.get(electrolysers.capacity_electric_kw))
	.connection_capacity_kw(row.get(electrolysers.connection_capacity_kw))
	.contracted_delivery_capacity_kw(row.get(electrolysers.contracted_delivery_capacity_kw))
	.contracted_feed_in_capacity_kw(row.get(electrolysers.contracted_feed_in_capacity_kw))
	
	.default_operation_mode(OL_ElectrolyserOperationMode.valueOf(row.get(electrolysers.default_operation_mode)))
	.conversion_efficiency(row.get(electrolysers.conversion_efficiency))
	.min_production_ratio(row.get(electrolysers.min_production_ratio))
	.idle_consumption_power_ratio(row.get(electrolysers.idle_consumption_power_ratio))
	.start_up_time_shutdown_h(row.get(electrolysers.start_up_time_shutdown_h))
	.start_up_time_standby_h(row.get(electrolysers.start_up_time_standby_h))
	.start_up_time_idle_h(row.get(electrolysers.start_up_time_idle_h))
	.load_change_time_h(row.get(electrolysers.load_change_time_h))
	
	.latitude(row.get(electrolysers.latitude))
	.longitude(row.get(electrolysers.longitude))
	.polygon(row.get(electrolysers.polygon))
	.build());
}	

/*ALCODEEND*/}

double f_setBattery_data()
{/*ALCODESTART::1727177706097*/
List<Tuple> rows = selectFrom(batteries).list();

for(Tuple row : rows){
	c_battery_data.add(Battery_data.builder()
	
	.gc_id(row.get(batteries.gc_id))
	.gc_name(row.get(batteries.gc_name))
	.owner_id(row.get(batteries.owner_id))
	.streetname(row.get(batteries.streetname))
	.house_number(row.get(batteries.house_number))
	.house_letter(row.get(batteries.house_letter))
	.house_addition(row.get(batteries.house_addition))
	.postalcode(row.get(batteries.postalcode))
	.city(row.get(batteries.city))
	.gridnode_id(row.get(batteries.gridnode_id))
	.initially_active(row.get(batteries.initially_active))
	
	.capacity_electric_kw(row.get(batteries.capacity_electric_kw))
	.connection_capacity_kw(row.get(batteries.connection_capacity_kw))
	.contracted_delivery_capacity_kw(row.get(batteries.contracted_delivery_capacity_kw))
	.contracted_feed_in_capacity_kw(row.get(batteries.contracted_feed_in_capacity_kw))
	
	.storage_capacity_kwh(row.get(batteries.storage_capacity_kwh))
	.operation_mode(OL_BatteryOperationMode.valueOf(row.get(batteries.operation_mode)))
	.latitude(row.get(batteries.latitude))
	.longitude(row.get(batteries.longitude))
	.polygon(row.get(batteries.polygon))
	.build());
}	
/*ALCODEEND*/}

double f_setNeighbourhood_data()
{/*ALCODESTART::1727177706099*/
List<Tuple> rows = selectFrom(neighbourhoods).list();
		
for(Tuple row: rows){
	c_neighbourhood_data.add(Neighbourhood_data.builder()

	//Default	
	.neighbourhoodcode(row.get( neighbourhoods.neighbourhoodcode ))
	.neighbourhoodname(row.get( neighbourhoods.neighbourhoodname ))
	.districtcode(row.get( neighbourhoods.districtcode ))
	.neighbourhoodtype(OL_GISObjectType.valueOf(row.get( neighbourhoods.neighbourhoodtype)))
	.latitude(row.get( neighbourhoods.latitude ))
	.longitude(row.get( neighbourhoods.longitude ))
	.polygon(row.get( neighbourhoods.polygon ))
	
	//Energy totals
	/*
	.avg_house_elec_delivery_kwh_p_yr(row.get( neighbourhoods.avg_house_elec_delivery_kwh_p_yr))
	.avg_house_gas_delivery_m3_p_yr(row.get( neighbourhoods.avg_house_gas_delivery_m3_p_yr))
	.avg_number_of_cars_per_house(row.get( neighbourhoods.avg_number_of_cars_per_house))
	.total_comp_elec_delivery_kwh_p_yr(row.get( neighbourhoods.total_comp_elec_delivery_kwh_p_yr))
	.total_comp_gas_delivery_m3_p_yr(row.get( neighbourhoods.total_comp_gas_delivery_m3_p_yr))
	.total_nr_comp_cars(row.get( neighbourhoods.total_nr_comp_cars))
	.total_nr_comp_vans(row.get( neighbourhoods.total_nr_comp_vans))
	.total_nr_comp_trucks(row.get( neighbourhoods.total_nr_comp_trucks))
	*/
	.build());
}
/*ALCODEEND*/}

double f_setChargingstation_data()
{/*ALCODESTART::1727177706101*/
List<Tuple> rows = selectFrom(chargingstations).list();

for(Tuple row : rows){
	c_chargingstation_data.add(Chargingstation_data.builder()
	
	//Basic
	.gc_id(row.get(chargingstations.gc_id))
	.gc_name(row.get(chargingstations.gc_name))
	.owner_id(row.get(chargingstations.owner_id))
	.streetname(row.get(chargingstations.streetname))
	.house_number(row.get(chargingstations.house_number))
	.house_letter(row.get(chargingstations.house_letter))
	.house_addition(row.get(chargingstations.house_addition))
	.postalcode(row.get(chargingstations.postalcode))
	.city(row.get(chargingstations.city))
	.gridnode_id(row.get(chargingstations.gridnode_id))
	.initially_active(row.get(chargingstations.initially_active))
	
	//Specific
	.connection_capacity_kw(row.get(chargingstations.connection_capacity_kw))
	.uses_charging_sessions(row.get(chargingstations.uses_charging_sessions))
	.vehicle_type(OL_VehicleType.valueOf(row.get(chargingstations.vehicle_type)))
	.number_of_sockets(row.get(chargingstations.number_of_sockets))
	.power_per_socket_kw(row.get(chargingstations.power_per_socket_kw))

	//GIS
	.latitude(row.get(chargingstations.latitude))
	.longitude(row.get(chargingstations.longitude))
	.polygon(row.get(chargingstations.polygon))
	.build());
}	

/*ALCODEEND*/}

double f_setParcel_data()
{/*ALCODESTART::1727177706103*/
List<Tuple> rows = selectFrom(parcels).list();

for(Tuple row : rows){
	c_parcel_data.add(Parcel_data.builder()
	
	.parcel_id(row.get(parcels.parcel_id))
	.name(row.get(parcels.name))
	.streetname(row.get(parcels.streetname))
	.house_number(row.get(parcels.house_number))
	.house_letter(row.get(parcels.house_letter))
	.house_addition(row.get(parcels.house_addition))
	.postalcode(row.get(parcels.postalcode))
	.city(row.get(parcels.city))
	.polygon_area_m2(row.get(parcels.polygon_area_m2))
	
	.latitude(row.get(parcels.latitude))
	.longitude(row.get(parcels.longitude))
	.polygon(row.get(parcels.polygon))
	.build());
}

/*ALCODEEND*/}

double f_setBuilding_data()
{/*ALCODESTART::1727177860192*/
//Loop to fill the different building type collections
List<Tuple> rows = selectFrom(buildings).list();

for(Tuple row : rows){

	//Survey companies
	if(row.get( buildings.purpose ) != null && !row.get( buildings.purpose ).contains("woonfunctie")){
		c_companyBuilding_data.add(f_buildingRecordBuilder(row));
	}
	
	//Houses
	else if(row.get( buildings.purpose ) != null && row.get( buildings.purpose ).contains("woonfunctie")){
		c_houseBuilding_data.add(f_buildingRecordBuilder(row));
	}
	
	else{
		c_remainingBuilding_data.add(f_buildingRecordBuilder(row));
	}
}


/*ALCODEEND*/}

double f_setDefaultProfiles_data()
{/*ALCODESTART::1738938959965*/
//Profile Arguments
List<Double> arguments_hr = selectFrom( profiles ).list( profiles.t_h );

//Weather data
List<Double> ambientTemperatureProfile_degC = selectFrom( profiles ).list( profiles.ambient_temperature_deg_c );
List<Double> PVProductionProfile35DegSouth_fr = selectFrom( profiles ).list( profiles.solar_e_prod_south35deg_normalized);
List<Double> PVProductionProfile15DegEastWest_fr = selectFrom( profiles ).list( profiles.solar_e_prod_eastwest15deg_normalized);
List<Double> windProductionProfile_fr = selectFrom( profiles ).list( profiles.wind_e_prod_normalized );

//Epex data
List<Double> epexProfile_eurpMWh = selectFrom( profiles ).list( profiles.day_ahead_price_eur_p_mwh ); 

//CO2 Emission data
List<Double> CO2EmissionFactorElectricityImport_kgpkWh = selectFrom( profiles ).list( profiles.co2_factor_kg_per_kwh ); 

//Various demand profile data
List<Double> defaultHouseElectricityDemandProfile_fr = selectFrom( profiles ).list( profiles.house_e_demand_other );
List<Double> defaultHouseHotWaterDemandProfile_fr = selectFrom( profiles ).list( profiles.house_h_demand_hot_water );
List<Double> defaultHouseCookingDemandProfile_fr = selectFrom( profiles ).list( profiles.house_cooking_demand );

List<Double> defaultOfficeElectricityDemandProfile_fr = selectFrom( profiles ).list( profiles.building_e_demand_other );
List<Double> defaultBuildingHeatDemandProfile_fr = selectFrom( profiles ).list( profiles.building_h_demand );

//Create Profiles_data object
defaultProfiles_data = DefaultProfiles_data.builder()
.arguments_hr(arguments_hr)
.ambientTemperatureProfile_degC(ambientTemperatureProfile_degC)
.PVProductionProfile35DegSouth_fr(PVProductionProfile35DegSouth_fr)
.PVProductionProfile15DegEastWest_fr(PVProductionProfile15DegEastWest_fr)
.windProductionProfile_fr(windProductionProfile_fr)
.epexProfile_eurpMWh(epexProfile_eurpMWh)
.CO2EmissionFactorElectricityImport_kgpkWh(CO2EmissionFactorElectricityImport_kgpkWh)
.defaultHouseElectricityDemandProfile_fr(defaultHouseElectricityDemandProfile_fr)
.defaultHouseHotWaterDemandProfile_fr(defaultHouseHotWaterDemandProfile_fr)
.defaultHouseCookingDemandProfile_fr(defaultHouseCookingDemandProfile_fr)
.defaultOfficeElectricityDemandProfile_fr(defaultOfficeElectricityDemandProfile_fr)
.defaultBuildingHeatDemandProfile_fr(defaultBuildingHeatDemandProfile_fr)
.build();
/*ALCODEEND*/}

double f_setParkingSpace_data()
{/*ALCODESTART::1749800394236*/
List<Tuple> rows = selectFrom(parking_spaces).list();
		
for(Tuple row: rows){
	c_parkingSpace_data.add(ParkingSpace_data.builder()
	
	.parking_id(row.get( parking_spaces.parking_id ))	
	.street(row.get( parking_spaces.street ))
	.type(OL_ParkingSpaceType.valueOf(row.get( parking_spaces.type )))
	.additional_info(row.get( parking_spaces.additional_info ))
	
	.pv_potential_kwp(row.get( parking_spaces.pv_potential_kwp ))
	
	.gridnode_id(row.get( parking_spaces.gridnode_id ))	

	.latitude(row.get( parking_spaces.latitude ))
	.longitude(row.get( parking_spaces.longitude ))
	.polygon(row.get( parking_spaces.polygon ))
	.build());
}
/*ALCODEEND*/}

double f_setChargerProfiles_data()
{/*ALCODESTART::1750329954408*/
for(int i = 1; i < 80; i++){

	String columnName = "CS" + i;
	List<String> chargerProfile_list = selectValues(String.class, "SELECT " + columnName + " FROM chargerprofiles;");
	chargerProfile_list.removeIf(e -> e == null); // Remove null values
	
	c_chargerProfiles_data.add(ChargerProfile_data.builder()
	.chargerProfileID("ChargerProfile: " + columnName)
	.valuesList(chargerProfile_list)
	.build());
}
/*ALCODEEND*/}

double f_setPBL_data()
{/*ALCODESTART::1768559136655*/
List<Tuple> lookupTablePBL_spaceHeatingAndResidents = selectFrom(pbl_spaceheating_and_residents).list();

for(Tuple row : lookupTablePBL_spaceHeatingAndResidents){
	c_lookupTablePBL_spaceHeatingAndResidents.add(PBL_SpaceHeatingAndResidents_data.builder()
	.dwelling_type(J_PBLUtil.getPBLDwellingTypeOption(row.get(pbl_spaceheating_and_residents.dwelling_type)))
	.construction_period(row.get(pbl_spaceheating_and_residents.construction_period))
	.ownership_type(J_PBLUtil.getPBLOwnershipTypeOption(row.get(pbl_spaceheating_and_residents.ownership_type)))
	.insulation_label(J_PBLUtil.getInsulationLabelOption(row.get(pbl_spaceheating_and_residents.insulation_label)))	
	.regression_population(row.get(pbl_spaceheating_and_residents.regression_population))// 1, 2 or 3	
	.slope_space_heating_gas_m3pm2a(row.get(pbl_spaceheating_and_residents.slope_space_heating_gas_m3pm2a))
	.constant_space_heating_gas_m3pa(row.get(pbl_spaceheating_and_residents.constant_space_heating_gas_m3pa))	
	.slope_residents(row.get(pbl_spaceheating_and_residents.slope_residents))
	.constant_residents(row.get(pbl_spaceheating_and_residents.constant_residents))
	.build());
}	

List<Tuple> lookupTablePBL_DHWAndCooking = selectFrom(pbl_dhw_and_cooking).list();

for(Tuple row : lookupTablePBL_DHWAndCooking){
	c_lookupTablePBL_DHWAndCooking.add(PBL_DHWAndCooking_data.builder()
	.construction_period(row.get(pbl_dhw_and_cooking.construction_period))
	.surface_code(row.get(pbl_dhw_and_cooking.surface_code))
	.household_size(row.get(pbl_dhw_and_cooking.household_size))
	.cooking_gas_demand_m3pa(row.get(pbl_dhw_and_cooking.cooking_gas_demand_m3pa))
	.dhw_gas_demand_m3pa(row.get(pbl_dhw_and_cooking.dhw_gas_demand_m3pa))
	.build());
}
/*ALCODEEND*/}

double f_setDHWProfile_data()
{/*ALCODESTART::1777130007989*/
List<Double> arguments_hr = selectFrom( dhw_profiles ).list( dhw_profiles.t_h );

// Dynamically fetch all column names
Connection conn = getEngine().getModelDatabase().getConnection();
List<String> columnNames = new ArrayList<>();

try{
    java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM dhw_profiles LIMIT 1");
    java.sql.ResultSetMetaData rsmd = rs.getMetaData();
    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        columnNames.add(rsmd.getColumnName(i));
    }
} catch (java.sql.SQLException e) {
    e.printStackTrace();
}

// Group profiles by resident number
for(String columnName : columnNames){
    if (!columnName.toLowerCase().startsWith("dhw")) continue;
    
    int residents = 1;
    try {
        if (columnName.toLowerCase().endsWith("p")) {
            String[] parts = columnName.split("_");
            String pPart = parts[parts.length - 1]; // e.g. "1p"
            residents = Integer.parseInt(pPart.substring(0, pPart.length() - 1));
        }
    } catch (Exception e) {
        traceln("Warning: Could not parse residents from DHW profile column " + columnName + ", defaulting to 2p.");
        residents = 2;
    }
    List<Double> DHWProfile = selectValues(Double.class, "SELECT " + columnName + " FROM dhw_profiles;");
    
    if (DHWProfile == null || DHWProfile.isEmpty()) continue;
    
    if (!map_nrOfResidentsToDHWProfiles_data.containsKey(residents)) {
        map_nrOfResidentsToDHWProfiles_data.put(residents, new ArrayList<>());
    }
    
    map_nrOfResidentsToDHWProfiles_data.get(residents).add(DHWProfile_data.builder()
        .DHWProfileID(columnName)
        .arguments_hr(arguments_hr)
        .valuesList(DHWProfile)
        .profileUnits(OL_ProfileUnits.KWHPQUARTERHOUR)
        .build());
        
}
/*ALCODEEND*/}

