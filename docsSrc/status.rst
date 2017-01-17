#############
Status
#############


.. |check| unicode:: 10003 .. checkmark

+--------------------------+------+----------------+ 
| Requirement              | Ref  | Satisfied?     |
+==========================+======+================+
|Platform: Java            |4.3.1 | |check| [#f1]_ | 
|Standard Edition 8        |      |                |
+--------------------------+------+----------------+
|Application Server: Jetty |4.3.2 | |check| [#f2]_ |
+--------------------------+------+----------------+
| Form of requests and     |4.3.3 | |check| [#f3]_ |
| responses                |      |                |
+--------------------------+------+----------------+
|Validates configuration   |4.3.4 | |check| [#f4]_ |
|on application start and  |      |                |
|logs errors               |      |                |
+--------------------------+------+----------------+



.. rubric:: Notes

.. [#f1] Java version "1.8.0_111" Java(TM)<br>SE Runtime Environment (build 1.8.0_111-b14)<br>Java HotSpot(TM) 64-Bit Server VM (build 25.111-b14, mixed mode)
.. [#f2] Runs on Jetty Distribution 9.3.14.

.. [#f3] See methods handling HTTP requests in https://raw.githubusercontent.com/govlawtech/dva-sop-api/devtest/src/main/java/au/gov/dva/sopapi/Application.java.
 Java's OffsetDateTime class with standard formatters for ISO date times.  Date strings ending in 'Z' with no time information are assumed to be 12am midnight UTC. (eg '2017-01-01Z')

.. [#f4] Logging throughout application using SL4J.


.. raw:: html 


   <tr>
       <td>Validates configuration on application start and logs errors</td>
       <td>4.3.4(a)</td>
       <td>&#x2713</td>
       <td>Logging throughout application using SL4J.</td>
   </tr>
        
   <tr>
       <td>Configurable Throttling based on the number of requests from an IP address</td>
       <td>4.3.4(b)</td>
       <td>&#x2713</td>
       <td>Configurable but not configured. To configure, add the Jetty Denial of Service filter as described here: http://www.eclipse.org/jetty/documentation/current/dos-filter.html</td>
   </tr>

   <tr>
        <td>Security - secured against JSON and REGEX DOS attacks</td>
        <td>4.3.5(a)</td>
        <td>&#x2713</td>
        <td>Parsing of API routes primarily uses Java's equality operator, not REGEX: see https://github.com/perwendel/spark/blob/master/src/main/java/spark/route/RouteEntry.java.  A regex is used for matching query parameters, however it does not have any groups with repetition: see https://github.com/perwendel/spark/blob/master/src/main/java/spark/QueryParamsMap.java.<br>
          The API uses the Jackson library to parse JSON in requests.  By default, this includes protection against JSON DOS attacks: see FAIL_ON_SYMBOL_HASH_OVERFLOW(true) in https://github.com/FasterXML/jackson-core/blob/master/src/main/java/com/fasterxml/jackson/core/JsonFactory.java
        </td>
   </tr>
        
   <tr>
        <td>Security - configured for TLS 1.2 exclusively</td>
        <td>4.3.5(c)</td>
        <td>&#x2713</td>
        <td>Jetty uses this configuration by default: see http://www.eclipse.org/jetty/documentation/current/configuring-ssl.html</td>
   </tr>

   <tr>
        <td>Security - validates incoming Content-Types and Response-Types</td>
        <td>4.3.5(d)</td>
        <td>&#x2713</td>
        <td>The API returns HTTP status code 406 if Content-Type is not 'application/json'.  See: https://raw.githubusercontent.com/govlawtech/dva-sop-api/devtest/src/main/java/au/gov/dva/sopapi/Application.java</td>
   </tr>

   <tr>
        <td>Security - responses include header: X-Content-Type-Options: nosniff.</td>
        <td>4.3.5(e)</td>
        <td>&#x2713</td>
        <td>The API sets this header on all responses.  See:  https://raw.githubusercontent.com/govlawtech/dva-sop-api/devtest/src/main/java/au/gov/dva/sopapi/Application.java</td>
   </tr>

   <tr>
        <td>Server Configuration - CORS enabled</td>
        <td>4.3.6(a)</td>
        <td>&#x2713</td>
        <td>Enabled via Windows Azure management portal.  Could also be enabled via web.xml: see http://www.eclipse.org/jetty/documentation/current/cross-origin-filter.html</td>
   </tr>
   
   <tr>
        <td>Server Configuration - Gzip compression enabled</td>
        <td>4.3.6(b)</td>
        <td>&#x2713</td>
        <td>Jetty applies Gzip compression for all GET methods by default: see /etc/jetty-gzip.xml.</td>
   </tr>
        
   <table> 
  

