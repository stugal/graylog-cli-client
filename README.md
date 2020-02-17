# graylog-cli-client
Simple Graylog CLI Client

Project composed out of the follwing components
- parent [maven parent]
- api [project api, for providing custom implementation of file parser and graylog dispatchers]
- app [app main module]

Project can be build by running 'mvn clean install' in the parent project folder. 
The runnable application can be found in app/target/app-1.0-jar-with-dependencies.jar, 
all required dependencies are packaged within that jar. Additionally, the manifest contains 
'custom-impl.jar' entry in the classpath. Custom File Processor and Graylog Dispatcher 
implementations should be packaged into 'custom-impl.jar' and placed next to app runnable jar.

Project usage:
java -jar <runnable-app-jar-name.jar> with following arguments:
-h --help 
-fp --file-path - [required] path to file to be processed 
-prop --properties - [optional] path to properties, if not provided defaults will be used

Providing custom log4j2 config:
java -Dlog4j.configurationFile=/path/to/alternate/config/file

Properties - sample file located in app module folder

Custom implementations:
- File processor extend org.graylog.interview.stuglik.files.AbstractFileProcessor from 'api' module
- Graylog dispatcher extend org.graylog.interview.stuglik.api.graylog.AbstractGraylogDispatcher from 'api' module
Custom classes shall be packaged into 'custom-impl.jar' and placed next to runnable jar build out of the 'app' module
