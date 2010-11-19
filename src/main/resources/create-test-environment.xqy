xquery version "1.0-ml";

import module namespace admin = "http://marklogic.com/xdmp/admin" 
          at "/MarkLogic/admin.xqy";

(:
 : GLOBAL VARIABLES
 :)
 
declare variable $TEST_DB as xs:string := "unit-test-db";
declare variable $TEST_DB_MODULES as xs:string := "unit-test-modules";
declare variable $TEST_FOREST as xs:string := "unit-test-forest01";
declare variable $TEST_FOREST_MODULES as xs:string := "unit-test-modules-forest01";


declare function local:create-forest(
    $config as element(configuration),
    $NewForest as xs:string) as element(configuration)
    {
    try {

    (: Get all of the existing forests :)
         let $ExistingForests :=
             for $id in admin:get-forest-ids($config)
                return admin:forest-get-name($config, $id)

    (: Check to see if forest already exists. If not, create new forest :)
         let $config :=
            if ($NewForest = $ExistingForests) 
            then $config
            else 
              admin:forest-create(
                $config, 
                $NewForest, 
                xdmp:host(), 
                "")

         return $config

    } catch($e) {
        xdmp:log($e)
    }
};

declare function local:create-database(
    $config as element(configuration), 
    $NewDatabase as xs:string) as element(configuration)
    {
    try {
         let $security-db := admin:database-get-id($config, "Security")
         let $schema-db :=   admin:database-get-id($config, "Schemas")

    (: Get all of the existing databases :)
         let $ExistingDatabases :=
             for $id in admin:get-database-ids($config)
                return admin:database-get-name($config, $id)

    (: Check to see if database already exists. If not, create new 
       database :)
         let $config :=
            if ($NewDatabase = $ExistingDatabases) 
            then $config
            else  
              admin:database-create(
                $config, 
                $NewDatabase, 
                $security-db, 
                $schema-db)

         return $config

    } catch($e) {
         xdmp:log($e)
    }
};

declare function local:create-forests(
      $config as element(configuration), 
      $NewForests as xs:string+) 
    {
    for $NewForest in $NewForests
        let $config := local:create-forest($config, $NewForest)
        return (admin:save-configuration($config), $config)
        
};


declare function local:create-databases(
      $config as element(configuration), 
      $NewDatabases as xs:string+) 
    {
        for $NewDatabase in $NewDatabases
            return local:create-database($config, $NewDatabase)  
};


(:~
 : The function that does all the work...
 : TODO = attach forest doesn't work properly if a forest is already attached..
 :)
declare function local:setup-test-resources(){
    let $log := xdmp:log("Setting up...")
    let $config := admin:get-configuration()
    let $log := xdmp:log("1. creating dbs")
    let $config := local:create-database($config, $TEST_DB)
    let $config := local:create-database($config, $TEST_DB_MODULES) 
    (:let $log := xdmp:log(fn:concat("- DATABASES Created: ", xdmp:database($TEST_DB), ", ", xdmp:database($TEST_DB_MODULES))) :)
    let $log := xdmp:log("2. creating forests")
    let $config := local:create-forest($config, $TEST_FOREST)
    let $config := local:create-forest($config, $TEST_FOREST_MODULES) 
    let $log := xdmp:log("3. Saving configuration")
    let $config := (admin:save-configuration($config), $config)
    (:let $log := xdmp:log(fn:concat("- FORESTS Created: ", xdmp:forest($TEST_FOREST), ", ", xdmp:forest($TEST_FOREST_MODULES))):)
    (: Enable the URI lexicon on the test-db for CORB :)
    let $log := xdmp:log("4. Enabling URI Lexicon on db")
    let $config := admin:database-set-uri-lexicon($config, xdmp:database($TEST_DB), fn:true())
    let $log := xdmp:log("5. Attaching Test DB forests...")
    let $config := admin:database-attach-forest($config, xdmp:database($TEST_DB), xdmp:forest($TEST_FOREST) )
    let $log := xdmp:log("6. Attaching Test DB MODULE forests...")
    let $config := admin:database-attach-forest($config, xdmp:database($TEST_DB_MODULES), xdmp:forest($TEST_FOREST_MODULES) )
    let $log := xdmp:log("7. Saving configuration")
    let $config := (admin:save-configuration($config), $config)
    let $log := xdmp:log("Setup Complete")
    return $config
};

(: step one - create databases and forests and add lexicons :)
local:setup-test-resources()