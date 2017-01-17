#############
Status
#############


.. |check| unicode:: 10003 .. checkmark





.. list-table:: Technical Requirements
   :widths: 15 10 30
   :header-rows: 1
   
   * - Requirement
     - Ref
     - Satsified?
   * - Platform: Java Standard Edition 8
     - 4.3.1
     - |check| [#f1]_
   * - Application Server: Jetty
     - 4.3.2
     - |check| [#f2]_
   * - Form of requests and  responses (JSON,REST,GET only, error codes, date formats)
     - 4.3.3
     - |check| [#f3]_ 
   * - Validates configuration on application start and logs errors               
     - 4.3.4
     - |check| [#f4]_
   * - Configurable Throttling based on the number of requests from an IP address 
     - 4.3.4(b)
     - |check| [#f5]_ 
   * - Security - secured aganist JSON and REGEX DOS attacks
     - 4.3.5(a)
     - |check| [#f6]_
   * - Security - Securured against CSRF attacks
     - 4.3.5(b)
     - |check| [#f11]_
   * - Security - configured for TLS 1.2 exclusively
     - 4.3.5(c)
     - |check| [#f7]_ 
   * - Security - validates incoming Content-Types and Response-Types
     - 4.3.5(d)
     - |check| [#f7]_ 
   * - Security - responses include header: X-Content-Type-Options: nosniff.
     - 4.3.5(e)
     - |check| [#f8]_
   * - Server Configuration - CORS enabled
     - 4.3.6(a)
     - |check| [#f9]_ 
   * - Server Configuration - Gzip compression enabled
     - 4.3.6(b)
     - |check| [#f10]_

      
   

     


.. rubric:: Notes

.. [#f1] Java version "1.8.0_111" Java(TM)<br>SE Runtime Environment (build 1.8.0_111-b14)<br>Java HotSpot(TM) 64-Bit Server VM (build 25.111-b14, mixed mode)
.. [#f2] Runs on Jetty Distribution 9.3.14.

.. [#f3] See methods handling HTTP requests in https://raw.githubusercontent.com/govlawtech/dva-sop-api/devtest/src/main/java/au/gov/dva/sopapi/Application.java.
 Java's OffsetDateTime class with standard formatters for ISO date times.  Date strings ending in 'Z' with no time information are assumed to be 12am midnight UTC. (eg '2017-01-01Z')

.. [#f4] Logging throughout application using SL4J.

.. [#f5] Configurable but not configured. To configure, add the Jetty Denial of Service filter as described here: http://www.eclipse.org/jetty/documentation/current/dos-filter.html.

.. [#f6] Parsing of API routes primarily uses Java's equality operator, not REGEX: see https://github.com/perwendel/spark/blob/master/src/main/java/spark/route/RouteEntry.java.  A regex is used for matching query parameters, however it does not have any groups with repetition: see https://github.com/perwendel/spark/blob/master/src/main/java/spark/QueryParamsMap.java.

          The API uses the Jackson library to parse JSON in requests.  By default, this includes protection against JSON DOS attacks: see FAIL_ON_SYMBOL_HASH_OVERFLOW(true) in https://github.com/FasterXML/jackson-core/blob/master/src/main/java/com/fasterxml/jackson/core/JsonFactory.java

.. [#f7] Jetty uses this configuration by default: see http://www.eclipse.org/jetty/documentation/current/configuring-ssl.html

.. [#f8] The API returns HTTP status code 406 if Content-Type is not 'application/json'.  See: https://raw.githubusercontent.com/govlawtech/dva-sop-api/devtest/src/main/java/au/gov/dva/sopapi/Application.java

.. [#f9] Enabled via Windows Azure management portal.  Could also be enabled via web.xml: see http://www.eclipse.org/jetty/documentation/current/cross-origin-filter.html.

.. [#f10] Jetty applies Gzip compression for all GET methods by default: see /etc/jetty-gzip.xml.

.. [#f11] The API is secured against this by design as it is stateless.




