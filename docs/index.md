siddhi-gpl-execution-r
======================================

The **siddhi-gpl-execution-r extension** is an extension to <a target="_blank" href="https://wso2.github.io/siddhi">Siddhi</a> that can be used to process events with R scripts.

Find some useful links below:

* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r">Source code</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r/releases">Releases</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r/issues">Issue tracker</a>

## Latest API Docs 

Latest API Docs is <a target="_blank" href="https://wso2-extensions.github.io/siddhi-gpl-execution-r/api/4.0.2-SNAPSHOT">4.0.2-SNAPSHOT</a>.

## How to use 

**Prerequisites for using the feature**

***For Linux***
   - [Download](https://cran.r-project.org/mirrors.html) and install R
   - Install JRI.
   - Add R_HOME and JRI_HOME as environment variables and export

***For UNIX***
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

**Using the extension in <a target="_blank" href="https://github.com/wso2/product-sp">WSO2 Stream Processor</a>**

* You can use this extension in the latest <a target="_blank" href="https://github.com/wso2/product-sp/releases">WSO2 Stream Processor</a> that is a part of <a target="_blank" href="http://wso2.com/analytics?utm_source=gitanalytics&utm_campaign=gitanalytics_Jul17">WSO2 Analytics</a> offering, with editor, debugger and simulation support. 

* You can use  this extension after copying the component <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r/releases">jar</a> to the `<STREAM_PROCESSOR_HOME>/lib` directory.

**Using the extension as a <a target="_blank" href="https://wso2.github.io/siddhi/documentation/running-as-a-java-library">java library</a>**

* This extension can be added as a maven dependency along with other Siddhi dependencies to your project.

```
     <dependency>
        <groupId>org.wso2.extension.siddhi.gpl.execution.r</groupId>
        <artifactId>siddhi-gpl-execution-r</artifactId>
        <version>x.x.x</version>
     </dependency>
```

## Jenkins Build Status

---

|  Branch | Build Status |
| :------ |:------------ | 
| master  | [![Build Status](https://wso2.org/jenkins/job/siddhi/job/siddhi-gpl-execution-r/badge/icon)](https://wso2.org/jenkins/job/siddhi/job/siddhi-gpl-execution-r/) |

---

## Features

* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-gpl-execution-r/api/4.0.2-SNAPSHOT/#eval-stream-processor">eval</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#stream-processors">Stream Processor</a>)*<br><div style="padding-left: 1em;"><p>R script Stream processor. This extension runs the R script loaded from a file to each event and produces aggregated outputs based on the provided input variable parameters and expected output attributes.</p></div>
* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-gpl-execution-r/api/4.0.2-SNAPSHOT/#evalsource-stream-processor">evalSource</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#stream-processors">Stream Processor</a>)*<br><div style="padding-left: 1em;"><p>R source Stream processor. This extension the R script loaded from a file to each event and produces aggregated outputs based on the provided input variable parameters and expected output attributes.</p></div>

## How to Contribute
 
  * Please report issues at <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r/issues">GitHub Issue Tracker</a>.
  
  * Send your contributions as pull requests to <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r/tree/master">master branch</a>.
 
## Contact us 

 * Post your questions with the <a target="_blank" href="http://stackoverflow.com/search?q=siddhi">"Siddhi"</a> tag in <a target="_blank" href="http://stackoverflow.com/search?q=siddhi">Stackoverflow</a>. 
 
 * Siddhi developers can be contacted via the mailing lists:
 
    Developers List   : [dev@wso2.org](mailto:dev@wso2.org)
    
    Architecture List : [architecture@wso2.org](mailto:architecture@wso2.org)
 
## Support 

* We are committed to ensuring support for this extension in production. Our unique approach ensures that all support leverages our open development methodology and is provided by the very same engineers who build the technology. 

* For more details and to take advantage of this unique opportunity contact us via <a target="_blank" href="http://wso2.com/support?utm_source=gitanalytics&utm_campaign=gitanalytics_Jul17">http://wso2.com/support/</a>. 
