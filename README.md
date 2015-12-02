[![Build Status](https://travis-ci.org/ableasdale/corb.svg?branch=master)](https://travis-ci.org/ableasdale/corb)

Before running the unit tests, you need to configure MarkLogic with one initial
application server, from this, the Unit test scripts will be able to prepare all
other databases, forests, Documents, indexes and Application Servers necessary for
the CORB TestSuite.


###The initial application server should be set up as such:###

| Configure     | Value         |
| ------------- | ------------- |
| Port          | 8010          |
| Database DB   | Documents     |
| Modules  DB   | Modules       |

Please note that no updates should occur in either the Documents or Modules DBs; this application server is just configured to give enough access to create the necessary
resources required by the test suite.


###Alternatively you can paste the following XQuery into CQ/DQ to create the server:###
```xquery
(: start of module :)
xquery version "1.0-ml";

import module namespace admin = "http://marklogic.com/xdmp/admin" 
          at "/MarkLogic/admin.xqy";

declare variable $port as xs:string := "8010";
declare variable $config := admin:get-configuration();

(: create the initial application server :)
let $config := admin:xdbc-server-create(   
                            $config, 
                            admin:group-get-id($config, "Default"), 
                            fn:concat("xdbc-",$port), 
                            "/", 
                            xs:unsignedLong($port), 
                            xdmp:database("Documents"), 
                            xdmp:database("Modules"))

return admin:save-configuration($config)
(: end of module :)
```

Both ports can be changed and currently these are configured by editing lines 43 and 44
of src/test/java/com/marklogic/developer/TestHelper.java

For more information see [http://marklogic.github.com/corb/](http://marklogic.github.com/corb/)
