siddhi-gpl-execution-r
======================================

The **siddhi-gpl-execution-r extension** is an extension to <a target="_blank" href="https://wso2.github.io/siddhi">Siddhi</a> that can be used to process events with R scripts.

Find some useful links below:

* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r">Source code</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r/releases">Releases</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-gpl-execution-r/issues">Issue tracker</a>

## Latest API Docs 

Latest API Docs is <a target="_blank" href="https://wso2-extensions.github.io/siddhi-gpl-execution-r/api/4.0.1-SNAPSHOT">4.0.1-SNAPSHOT</a>.

## How to use 

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

* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-gpl-execution-r/api/4.0.1-SNAPSHOT/#eval-stream-processor">eval</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#stream-processors">Stream Processor</a>)*<br><div style="padding-left: 1em;"><p>R script Stream processor. This extension runs the R script loaded from a file to each event and produces aggregated outputs based on the provided input variable parameters and expected output attributes.</p></div>
* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-gpl-execution-r/api/4.0.1-SNAPSHOT/#evalsource-stream-processor">evalSource</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#stream-processors">Stream Processor</a>)*<br><div style="padding-left: 1em;"><p>R source Stream processor. This extension the R script loaded from a file to each event and produces aggregated outputs based on the provided input variable parameters and expected output attributes.</p></div>

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
