# Zero_ProjectTemplate
This repository contains the template for the project setup alp. needed to run the Zenmo Zero model.

## Use
After creating your own project repository, all "ProjectTemplate" words, strings, agents, etc. you can find in the Alp file should be replaced with your own project name. 
For example: Loader_ProjectTemplate ->  Loader_Example, project_name = ProjectTemplate -> project_name = Example.

All the Excel files in the repository data_ProjectTemplate have suffix "_ProjectTemplate.xlsx", these, along with the folder name itself, should also be replaced by your project name. 
To bulk rename the excel files on windows you can use the following command in the PowerShell after navigating to the data_ProjectTemplate folder.

Get-ChildItem *ProjectTemplate.xlsx | Rename-Item -NewName { $_.Name -replace 'ProjectTemplate.xlsx', 'Your_Name_Here.xlsx'}
