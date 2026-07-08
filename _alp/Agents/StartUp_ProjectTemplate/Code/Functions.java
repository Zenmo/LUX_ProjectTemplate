double f_overwriteAvgValuesAVGCDatabase()
{/*ALCODESTART::1726226738848*/
if (avg_electricity_consumption_house_kWh_p_yr != null && avg_electricity_consumption_house_kWh_p_yr > 0){
	loader_ProjectTemplate.avgc_data.p_avgHouseElectricityConsumption_kWh_yr = avg_electricity_consumption_house_kWh_p_yr;
}
if (avg_gas_consumption_house_m3_p_yr != null && avg_gas_consumption_house_m3_p_yr > 0){
	loader_ProjectTemplate.avgc_data.p_avgHouseGasConsumption_m3_yr = avg_gas_consumption_house_m3_p_yr;
}
if (avg_number_of_cars_per_house != null && avg_number_of_cars_per_house >= 0){
	loader_ProjectTemplate.avgc_data.p_avgNrOfCarsPerHouse = avg_number_of_cars_per_house;
}
/*ALCODEEND*/}

double f_setProject_data()
{/*ALCODESTART::1726227634344*/
//Create the project data record and send it to loader
loader_ProjectTemplate.project_data = Project_data.builder().

//Project settings
project_name(project_name).
project_type(project_type).
survey_type(survey_type).

//Database names
databaseNames(databaseNames).

//Project specific actors
grid_operator(grid_operator).
hasCongestionPricing(hasCongestionPricing).
energy_coop(energy_coop).
energy_supplier(energy_supplier).

//Project totals
total_electricity_consumption_companies_kWh_p_yr(total_electricity_consumption_companies_kWh_p_yr).
total_gas_consumption_companies_m3_p_yr(total_gas_consumption_companies_m3_p_yr).
avg_electricity_consumption_house_kWh_p_yr(avg_electricity_consumption_house_kWh_p_yr).
avg_gas_consumption_house_m3_p_yr(avg_gas_consumption_house_m3_p_yr).
avg_number_of_cars_per_house(avg_number_of_cars_per_house).
total_cars_companies(total_cars_companies).
total_vans_companies(total_vans_companies).
total_trucks_companies(total_trucks_companies).
build();

/*ALCODEEND*/}

double f_startModel()
{/*ALCODESTART::1726231260460*/
//Set model user
f_setUser();

//Subscopes
f_setSubScopesToSimulate();

//Send settings to the loader
f_setSettings();

//Send project data to the loader
f_setProject_data();

//Project specific initialize functions
f_projectSpecificInitializeFunctions();

//Startup loader
loader_ProjectTemplate.f_startUpLoader_default();
/*ALCODEEND*/}

double f_projectSpecificInitializeFunctions()
{/*ALCODESTART::1726235796348*/
//Manually overwrite the avgc values
f_overwriteAvgValuesAVGCDatabase();
/*ALCODEEND*/}

double f_setSettings()
{/*ALCODESTART::1726238799507*/
//Create the project data record and send it to loader
loader_ProjectTemplate.settings = Settings.builder().
//Simulation settings
reloadDatabase(p_reloadDatabase).
createCurrentElectricityEA(p_createCurrentElectricityEA).
runHeadlessAtStartup(p_runHeadlessAtStartup).
showKPISummary(p_showKPISummary).
isPublicModel(p_isPublicModel).
subscopesToSimulate(c_subScopesToSimulate).
resultsUISelectedChartTypes_Energy(resultsUISelectedChartTypes_Energy).
//resultsUISelectedChartTypes_Economic(resultsUISelectedChartTypes_Economic). 
//activeMapOverlayTypes(c_selectedMapOverlayTypes).
build();
/*ALCODEEND*/}

double f_setUser()
{/*ALCODESTART::1726498824702*/
//Set the user
loader_ProjectTemplate.user = User.builder().

//Zorm API login parameters
PROJECT_CLIENT_ID(System.getenv("CLIENT_ID")).
PROJECT_CLIENT_SECRET(System.getenv("CLIENT_SECRET")).

//UserIdToken (Used for scenario saving)
userIdToken(p_userIdToken).

//User accesibility
NBHAccessType(p_userNBHAccessType).
accessibleNBH(accessibleNBHCodes).
GCAccessType(p_userGCAccessType).
accessibleCompanyIDs(accessibleCompanyUUIDs).
build();
/*ALCODEEND*/}

double f_setSubScopesToSimulate()
{/*ALCODESTART::1749712371844*/
if(p_subScope_A){
	c_subScopesToSimulate.add("A");
}
if(p_subScope_B){
	c_subScopesToSimulate.add("B");
}
if(p_subScope_C){
	c_subScopesToSimulate.add("C");
}
if(p_subScope_D){
	c_subScopesToSimulate.add("D");
}
/*ALCODEEND*/}

