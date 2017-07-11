# siddhi-gpl-execution-r

This extension runs R script loaded from a file or written in the siddhi query, and each event and produces
aggregated outputs based on the provided input variable parameters and expected output attributes.

#### Prerequisites for using the feature
##### For Linux
   - [Download](https://cran.r-project.org/mirrors.html) and install R
   - Install JRI.
   - Add R_HOME and JRI_HOME as environment variables and export

##### For UNIX
   - [Download](https://cran.r-project.org/mirrors.html) and install R
   - Verify whether the R installation is successful by typing command "R" in a new terminal.
   - Install JRI.
    - In R console, install using the following command :
        install.packages(‘rJava’, repos='http://cran.us.r-project.org')
    - This will download the rJava package, and the downloaded location will be shown in the R console
    - In the UNIX terminal, type R CMD INSTALL {{location of downloaded file}}.
    - Above command will install JRI in the system.
   - Set environment variables
    - Get R_home location via R console using : R.home(component="home").
    - This will result in somewhat equal to /Library/Frameworks/R.framework/Resources
    - Add the above result to an environment variable: R_HOME
    - export the variable: export R_HOME
    - JRI location will be in a similar location to:
        /Library/Frameworks/R.framework/Versions/3.3/Resources/library/rJava/jri/
    - Add the above result to an environment variable: JRI_HOME
    - export JRI_HOME

## How to Contribute
  * Please report issues at
  [Siddhi Github Issue Tacker](https://github.com/wso2-extensions/siddhi-gpl-execution-r/issues)
  * Carbon Developers List : dev@wso2.org
  * Carbon Architecture List : architecture@wso2.org

**We welcome your feedback and contribution.**

Siddhi Stream Processor Team

## API Docs:

1. <a href="./api/4.0.2-SNAPSHOT">4.0.2-SNAPSHOT</a>
