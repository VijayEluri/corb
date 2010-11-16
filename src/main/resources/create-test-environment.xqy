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



declare function local:clone-database(  $config,
                                        $source-database-name,
                                        $target-database-name,
                                        $triggers-database-name ){
let $config :=
    if ( $triggers-database-name eq "none" )
    then $config
    else local:clone-database( $config, "Triggers", $triggers-database-name, "none" )
        
let $config := admin:forest-copy($config, admin:forest-get-id( $config, $source-database-name ), $target-database-name, ())
let $config := admin:database-copy($config, admin:database-get-id( $config, $source-database-name ), $target-database-name)
let $config := (admin:save-configuration($config), $config)
let $config := admin:database-attach-forest($config, xdmp:database($target-database-name), xdmp:forest($target-database-name) )
let $config :=
        if ( $triggers-database-name eq "none" )
        then $config
        else admin:database-set-triggers-database($config, admin:database-get-id( $config, $target-database-name ), admin:database-get-id( $config, $triggers-database-name ))
return 
    admin:save-configuration($config)
}; 

(:local:clone-database(admin:get-configuration(), "test", "test2", "none"):)

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
      $NewForests as xs:string+) as element(configuration)
    {
    for $NewForest in $NewForests
    let $config := local:create-forest($config, $NewForest)
    let $config := (admin:save-configuration($config), $config)
    return $config
};


declare function local:create-databases(
      $config as element(configuration), 
      $NewDatabases as xs:string+) as element(configuration)
    {
    for $NewDatabase in $NewDatabases
    let $config := local:create-database($config, $NewDatabase)
    let $config := (admin:save-configuration($config), $config)
    return $config
};

(:
 :
 :
 :              TEARDOWN UNIT TEST MODULE CODE BELOW:
 :
 :
 :
 :)

declare function local:delete-forest(
    $config as element(configuration), 
    $ForestName as xs:string) as element(configuration)
    {
    try {

         let $forest := admin:forest-get-id($config, $ForestName)

    (: Get all of the existing forests :)
         let $ExistingForests :=
             for $id in admin:get-forest-ids($config)
                return admin:forest-get-name($config, $id)

    (: Check to see if forest exists. If so, remove the forest :)
         let $config :=
            if ($ForestName = $ExistingForests) 
            then admin:forest-delete(
                   $config, 
                   admin:forest-get-id($config, $ForestName),
                   fn:true())
            else $config

         return $config

    } catch($e) {
         xdmp:log($e)
    }
};

declare function local:delete-database(      
    $config as element(configuration), 
    $DatabaseName as xs:string) as element(configuration) 
    {
    try {
         let $database := admin:database-get-id($config, $DatabaseName)
    (: Get all of the existing databases :)
         let $ExistingDatabases :=
             for $id in admin:get-database-ids($config)
                return admin:database-get-name($config, $id)

    (: Check to see if database exists. If so, remove the database :)
         let $config :=
            if ($DatabaseName = $ExistingDatabases) 
            then admin:database-delete(
                   $config, 
                   admin:database-get-id($config, $DatabaseName))
            else $config
         return $config
    } catch($e) {
         xdmp:log($e)
    }
};

declare function local:delete-forests(
      $config as element(configuration), 
      $forests as xs:string+)
    {
    for $forest in $forests
    let $config := local:delete-forest($config, $forest)
    let $config := (admin:save-configuration($config), $config)
    return $config
};

declare function local:delete-databases(
      $config as element(configuration), 
      $databases as xs:string+) as element(configuration)
    {
    for $database in $databases
    let $config := local:delete-database($config, $database)
    let $config := (admin:save-configuration($config), $config)
    return $config
};

declare function local:detach-forest(
    $config as element(configuration), 
    $db-name as xs:string,
    $forest-name as xs:string) as element(configuration)
    {  
    try {
        let $forests := admin:database-get-attached-forests($config, xdmp:database($db-name)) 
        let $config := 
            if (xdmp:forest($forest-name) = $forests) 
            then admin:database-detach-forest($config, xdmp:database($db-name), xdmp:forest($forest-name) )
            else $config
        return $config
    } catch($e) {
         xdmp:log($e)
    }
};


(:~
 : The function that does all the work...
 :)
declare function local:setup-test-resources(){
    let $log := xdmp:log("setting up...")
    let $config := local:create-databases(admin:get-configuration(), ($TEST_DB, $TEST_DB_MODULES) )
    let $config := local:create-forests($config, ($TEST_FOREST, $TEST_FOREST_MODULES) )
    (: Enable the URI lexicon on the test-db for CORB :)
    let $config := admin:database-set-uri-lexicon($config, xdmp:database($TEST_DB), fn:true())
    let $config := (admin:save-configuration($config), $config)
    let $config := admin:database-attach-forest($config, xdmp:database($TEST_DB), xdmp:forest($TEST_FOREST) )
    let $config := (admin:save-configuration($config), $config)
    let $config := admin:database-attach-forest($config, xdmp:database($TEST_DB_MODULES), xdmp:forest($TEST_FOREST_MODULES) )
    let $config := (admin:save-configuration($config), $config)
    return $config
};

(:~
 : The function that tears down...
 :)
declare function local:teardown-test-resources(){
    let $log := xdmp:log("tearing down...")    
    let $config := local:detach-forest(admin:get-configuration(), $TEST_DB, $TEST_FOREST) 
    let $config := (admin:save-configuration($config), $config)
    let $config := local:detach-forest($config, $TEST_DB_MODULES, $TEST_FOREST_MODULES) 
    let $config := (admin:save-configuration($config), $config)
    let $config := local:delete-forests($config, ($TEST_FOREST, $TEST_FOREST_MODULES)) 
    let $config := local:delete-databases($config, ($TEST_DB, $TEST_DB_MODULES)) 
    let $config := (admin:save-configuration($config), $config)
    return $config 
};

(: step one - create databases and forests and add lexicons :)
(:local:setup-test-resources():)

(: step two - insert a bunch of random docs into the new db for testing :)

(: step three - create an application server and modules db for testing :)


(: step four - remove databases and forests :)
(:local:teardown-test-resources() :)