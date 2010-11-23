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