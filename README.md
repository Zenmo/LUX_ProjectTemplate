# LUX_ProjectTemplate
This repository contains the template for the project setup alpx. needed to run the LUX EnergyTwin model.

## Use
After creating your own project repository (using this as a template), all "ProjectTemplate" words in the files and filenames in the folder (excluding the data_Generic folder) should be replaced with your project name. Note that this project name can not contain any spaces and special signs. Just letters, digits and _ or -. The project name should also always start with a capital letter. (Except if it starts with a digit, then just somehwere in the name a capital letter is fine!).
Note that "projecttemplate" is also found in the files, which should be replaced with the non-capatilized version of your own projectname as well. -> Meaning find-replace should be case sensitive.

To make this easy, a powershell script has been made that can be ran by executing (double clicking) the Run-Rename.bat file.
Then filling in your project name that meets the requirements, and pressing ok.
The powershell script then replaces all instances of "ProjectTemplate" (and "projecttemplate") in the repository with the new project name (and the non capatilized project name). (Excluding the data_Generic folder).

After running the rename script, the bat and powershell (ps1) file can be removed from the repo. Just like (the contents of) this README.